package com.paypal.invoices.extractioncommons.services;

import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoices;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.infrastructure.support.date.TimeMachine;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import com.paypal.invoices.extractioncommons.model.InvoiceTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.mirakl.client.mmp.domain.accounting.document.MiraklAccountingDocumentPaymentStatus.PENDING;
import static com.mirakl.client.mmp.domain.accounting.document.MiraklAccountingDocumentType.MANUAL_CREDIT;
import static com.mirakl.client.mmp.request.payment.invoice.MiraklAccountingDocumentState.COMPLETE;
import static com.paypal.infrastructure.hyperwallet.constants.HyperWalletConstants.MIRAKL_MAX_RESULTS_PER_PAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractAccountingDocumentsExtractServiceImplTest {

	private static final String ACCOUNTDOCUMENT_ID_1 = "ACCOUNTDOCUMENT_ID_1";

	private static final String ACCOUNTDOCUMENT_ID_2 = "ACCOUNTDOCUMENT_ID_2";

	private static final String ACCOUNTDOCUMENT_ID_3 = "ACCOUNTDOCUMENT_ID_3";

	@Value("${hmc.jobs.settings.search-invoices-maxdays}")
	protected int maxNumberOfDaysForInvoiceIdSearch;

	@InjectMocks
	@Spy
	private MyAccountingDocumentsExtractServiceImplTest testObj;

	@Mock
	protected MiraklClient miraklMarketplacePlatformOperatorApiClient;

	@Mock
	private Converter<MiraklInvoice, MyAccountingDocumentModel> invoiceConverterMock;

	@Mock
	private MyAccountingDocumentModel myAccountingDocumentModel1Mock, myAccountingDocumentModel2Mock;

	@Mock
	private MiraklInvoice miraklInvoice1Mock, miraklInvoice2Mock, miraklInvoice3Mock;

	@Mock
	private MiraklInvoices miraklInvoicesMock, miraklInvoices2Mock;

	@Captor
	private ArgumentCaptor<MiraklGetInvoicesRequest> miraklGetInvoicesRequestArgumentCaptor;

	@Test
	void extractAccountingDocumentsById_shouldReturnDocuments_whenTheyAreInsideSearchWindow() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());

		when(miraklMarketplacePlatformOperatorApiClient.getInvoices(any())).thenReturn(miraklInvoicesMock);
		when(miraklInvoicesMock.getInvoices())
				.thenReturn(List.of(miraklInvoice1Mock, miraklInvoice2Mock, miraklInvoice3Mock));
		when(miraklInvoice1Mock.getId()).thenReturn(ACCOUNTDOCUMENT_ID_1);
		when(miraklInvoice2Mock.getId()).thenReturn(ACCOUNTDOCUMENT_ID_2);
		when(miraklInvoice3Mock.getId()).thenReturn(ACCOUNTDOCUMENT_ID_3);
		when(invoiceConverterMock.convert(miraklInvoice1Mock)).thenReturn(myAccountingDocumentModel1Mock);
		when(invoiceConverterMock.convert(miraklInvoice2Mock)).thenReturn(myAccountingDocumentModel2Mock);

		final Collection<MyAccountingDocumentModel> result = testObj
				.extractAccountingDocuments(List.of(ACCOUNTDOCUMENT_ID_1, ACCOUNTDOCUMENT_ID_2));

		assertThat(result).containsExactlyInAnyOrder(myAccountingDocumentModel1Mock, myAccountingDocumentModel2Mock);
		verify(miraklMarketplacePlatformOperatorApiClient)
				.getInvoices(miraklGetInvoicesRequestArgumentCaptor.capture());
		assertThat(miraklGetInvoicesRequestArgumentCaptor.getValue().getStartDate()).isAfterOrEqualTo(searchWindow());
		assertThat(miraklGetInvoicesRequestArgumentCaptor.getValue().getType())
				.hasToString(testObj.getInvoiceType().toString());
	}

	@Test
	void extractAccountingDocument_whenNoInvoicesAreReturned_shouldReturnEmptyList() {
		final LocalDateTime now = LocalDateTime.now();
		TimeMachine.useFixedClockAt(now);
		final Date nowAsDate = DateUtil.convertToDate(now, ZoneId.systemDefault());

		doReturn(Collections.emptyList()).when(testObj).extractAccountingDocuments(nowAsDate);

		final List<MyAccountingDocumentModel> result = testObj.extractAccountingDocuments(nowAsDate);

		assertThat(result).isEmpty();
	}

	@Test
	void extractAccountingDocuments_shouldReturnListOfAccountingDocuments_notIncludingPaidInvoices() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 0, 55));
		final Date now = DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault());

		when(miraklMarketplacePlatformOperatorApiClient.getInvoices(any())).thenReturn(miraklInvoicesMock);
		when(miraklInvoicesMock.getInvoices()).thenReturn(List.of(miraklInvoice1Mock, miraklInvoice2Mock));
		when(invoiceConverterMock.convert(miraklInvoice1Mock)).thenReturn(myAccountingDocumentModel1Mock);
		when(invoiceConverterMock.convert(miraklInvoice2Mock)).thenReturn(myAccountingDocumentModel2Mock);

		final List<MyAccountingDocumentModel> creditNoteList = testObj.extractAccountingDocuments(now);

		verify(miraklMarketplacePlatformOperatorApiClient)
				.getInvoices(miraklGetInvoicesRequestArgumentCaptor.capture());
		verify(testObj).extractAccountingDocuments(now, false);

		final MiraklGetInvoicesRequest miraklGetInvoicesRequest = miraklGetInvoicesRequestArgumentCaptor.getValue();

		assertThat(miraklGetInvoicesRequest.getStartDate()).isEqualTo(now);
		assertThat(miraklGetInvoicesRequest.getStates()).isEqualTo(List.of(COMPLETE));
		assertThat(miraklGetInvoicesRequest.getPaymentStatus()).isEqualTo(PENDING);

		assertThat(creditNoteList).containsExactlyInAnyOrder(myAccountingDocumentModel1Mock,
				myAccountingDocumentModel2Mock);
	}

	@Test
	void extractAccountingDocuments_shouldReturnListOfAccountingDocuments_includingPaidInvoices() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 0, 55));
		final Date now = DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault());

		when(miraklMarketplacePlatformOperatorApiClient.getInvoices(any())).thenReturn(miraklInvoicesMock);
		when(miraklInvoicesMock.getInvoices()).thenReturn(List.of(miraklInvoice1Mock, miraklInvoice2Mock));
		when(invoiceConverterMock.convert(miraklInvoice1Mock)).thenReturn(myAccountingDocumentModel1Mock);
		when(invoiceConverterMock.convert(miraklInvoice2Mock)).thenReturn(myAccountingDocumentModel2Mock);

		final List<MyAccountingDocumentModel> creditNoteList = testObj.extractAccountingDocuments(now, true);

		verify(miraklMarketplacePlatformOperatorApiClient)
				.getInvoices(miraklGetInvoicesRequestArgumentCaptor.capture());

		final MiraklGetInvoicesRequest miraklGetInvoicesRequest = miraklGetInvoicesRequestArgumentCaptor.getValue();

		assertThat(miraklGetInvoicesRequest.getStartDate()).isEqualTo(now);
		assertThat(miraklGetInvoicesRequest.getStates()).isEqualTo(List.of(COMPLETE));
		assertThat(miraklGetInvoicesRequest.getPaymentStatus()).isNull();

		assertThat(creditNoteList).containsExactlyInAnyOrder(myAccountingDocumentModel1Mock,
				myAccountingDocumentModel2Mock);
	}

	@Test
	void createAccountingDocumentRequest_shouldReturnRequestWithTargetInvoiceType() {
		final Date date = new Date();

		final MiraklGetInvoicesRequest result = testObj.createAccountingDocumentRequest(date,
				InvoiceTypeEnum.MANUAL_CREDIT);

		assertThat(result.getMax()).isEqualTo(100);
		assertThat(result.getStartDate()).isEqualTo(date);
		assertThat(result.getType()).isEqualTo(MANUAL_CREDIT);
		assertThat(result.getPaymentStatus()).isEqualTo(PENDING);
		assertThat(result.getStates()).containsExactly(COMPLETE);
	}

	@Test
	void getAccountingDocuments_whenRequestNeedsPagination_shouldRepeatRequestAndReturnAllInvoices() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 0, 55));
		final Date now = DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault());

		final List<MiraklInvoice> firstPageResponseInvoices = getListOfmiraklInvoiceMocks(MIRAKL_MAX_RESULTS_PER_PAGE);
		final List<MiraklInvoice> secondPageResponseInvoices = getListOfmiraklInvoiceMocks(
				MIRAKL_MAX_RESULTS_PER_PAGE / 2);
		final long totalResponseInvoices = firstPageResponseInvoices.size() + secondPageResponseInvoices.size();

		when(miraklMarketplacePlatformOperatorApiClient
				.getInvoices(argThat(request -> request != null && request.getOffset() == 0)))
						.thenReturn(miraklInvoicesMock);
		when(miraklMarketplacePlatformOperatorApiClient
				.getInvoices(argThat(request -> request != null && request.getOffset() == MIRAKL_MAX_RESULTS_PER_PAGE)))
						.thenReturn(miraklInvoices2Mock);
		when(miraklInvoicesMock.getTotalCount()).thenReturn(totalResponseInvoices);
		when(miraklInvoicesMock.getInvoices()).thenReturn(firstPageResponseInvoices);
		when(miraklInvoices2Mock.getTotalCount()).thenReturn(totalResponseInvoices);
		when(miraklInvoices2Mock.getInvoices()).thenReturn(secondPageResponseInvoices);

		final List<MyAccountingDocumentModel> expectedAccountingDocuments = Stream
				.concat(firstPageResponseInvoices.stream(), secondPageResponseInvoices.stream())
				.map(invoice -> mockAndReturn(invoice)).collect(Collectors.toList());

		final List<MyAccountingDocumentModel> result = testObj.extractAccountingDocuments(now);

		assertThat(result).containsExactlyElementsOf(expectedAccountingDocuments);
	}

	private MyAccountingDocumentModel mockAndReturn(final MiraklInvoice invoice) {
		final MyAccountingDocumentModel myAccountingDocumentModel = mock(MyAccountingDocumentModel.class);
		when(invoiceConverterMock.convert(invoice)).thenReturn(myAccountingDocumentModel);
		return myAccountingDocumentModel;
	}

	private List<MiraklInvoice> getListOfmiraklInvoiceMocks(final int numberOfMocks) {
		return IntStream.range(0, numberOfMocks).mapToObj(i -> mock(MiraklInvoice.class)).collect(Collectors.toList());
	}

	private Date searchWindow() {
		return Date.from(TimeMachine.now().minusDays(maxNumberOfDaysForInvoiceIdSearch).toInstant(ZoneOffset.UTC));
	}

	static class MyAccountingDocumentsExtractServiceImplTest
			extends AbstractAccountingDocumentsExtractServiceImpl<MyAccountingDocumentModel> {

		private final Converter<MiraklInvoice, MyAccountingDocumentModel> miraklInvoiceToInvoiceModelConverter;

		protected MyAccountingDocumentsExtractServiceImplTest(
				final Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter,
				final MiraklClient miraklMarketplacePlatformOperatorApiClient,
				final AccountingDocumentsLinksService accountingDocumentsLinksService,
				final MailNotificationUtil invoicesMailNotificationUtil,
				final Converter<MiraklInvoice, MyAccountingDocumentModel> miraklInvoiceToInvoiceModelConverter) {
			super(miraklShopToAccountingModelConverter, miraklMarketplacePlatformOperatorApiClient,
					accountingDocumentsLinksService, invoicesMailNotificationUtil);
			this.miraklInvoiceToInvoiceModelConverter = miraklInvoiceToInvoiceModelConverter;
		}

		@Override
		protected InvoiceTypeEnum getInvoiceType() {
			return InvoiceTypeEnum.AUTO_INVOICE;
		}

		@Override
		protected Converter<MiraklInvoice, MyAccountingDocumentModel> getMiraklInvoiceToAccountingModelConverter() {
			return miraklInvoiceToInvoiceModelConverter;
		}

	}

	static class MyAccountingDocumentModel extends AccountingDocumentModel {

		protected MyAccountingDocumentModel(final Builder<?> builder) {
			super(builder);
		}

	}

}
