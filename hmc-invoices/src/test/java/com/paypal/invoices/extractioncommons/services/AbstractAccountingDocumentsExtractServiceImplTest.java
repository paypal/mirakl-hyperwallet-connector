package com.paypal.invoices.extractioncommons.services;

import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklPayOutState;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycle;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycles;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.operator.request.payment.sellerbillingcycle.MiraklGetSellerBillingCyclesRequest;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.infrastructure.support.date.TimeMachine;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import com.paypal.invoices.extractioncommons.model.InvoiceTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.paypal.infrastructure.hyperwallet.constants.HyperWalletConstants.MIRAKL_MAX_RESULTS_PER_PAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AbstractAccountingDocumentsExtractServiceImplTest {

	private static final String ACCOUNTING_DOCUMENT_ID_1 = UUID.randomUUID().toString();

	private static final String ACCOUNTING_DOCUMENT_ID_2 = UUID.randomUUID().toString();

	private static final String ACCOUNTING_DOCUMENT_ID_3 = UUID.randomUUID().toString();

	private static final String NEXT_TOKEN = "next-token";

	@Value("${hmc.jobs.settings.search-invoices-maxdays}")
	protected int maxNumberOfDaysForInvoiceIdSearch;

	@InjectMocks
	@Spy
	private MyAccountingDocumentsExtractServiceImplTest testObj;

	@Mock
	protected MiraklClient miraklMarketplacePlatformOperatorApiClient;

	@Mock
	private Converter<MiraklSellerBillingCycle, MyAccountingDocumentModel> invoiceConverterMock;

	@Mock
	private MyAccountingDocumentModel myAccountingDocumentModel1Mock, myAccountingDocumentModel2Mock;

	@Mock
	private MiraklSellerBillingCycle billingCycle1Mock, billingCycle2Mock, billingCycle3Mock;

	@Mock
	private MiraklSellerBillingCycles billingCyclesMock, billingCycles2Mock;

	@Captor
	private ArgumentCaptor<MiraklGetSellerBillingCyclesRequest> requestArgumentCaptor;

	@Test
	void extractAccountingDocumentsById_shouldReturnDocuments_whenTheyAreInsideSearchWindow() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());

		when(miraklMarketplacePlatformOperatorApiClient.getSellerBillingCycles(any())).thenReturn(billingCyclesMock);
		when(billingCyclesMock.getData()).thenReturn(List.of(billingCycle1Mock, billingCycle2Mock, billingCycle3Mock));
		when(billingCycle1Mock.getId()).thenReturn(UUID.fromString(ACCOUNTING_DOCUMENT_ID_1));
		when(billingCycle2Mock.getId()).thenReturn(UUID.fromString(ACCOUNTING_DOCUMENT_ID_2));
		when(billingCycle3Mock.getId()).thenReturn(UUID.fromString(ACCOUNTING_DOCUMENT_ID_3));
		when(invoiceConverterMock.convert(billingCycle1Mock)).thenReturn(myAccountingDocumentModel1Mock);
		when(invoiceConverterMock.convert(billingCycle2Mock)).thenReturn(myAccountingDocumentModel2Mock);

		final Collection<MyAccountingDocumentModel> result = testObj
			.extractAccountingDocuments(List.of(ACCOUNTING_DOCUMENT_ID_1, ACCOUNTING_DOCUMENT_ID_2));

		assertThat(result).containsExactlyInAnyOrder(myAccountingDocumentModel1Mock, myAccountingDocumentModel2Mock);
		verify(miraklMarketplacePlatformOperatorApiClient).getSellerBillingCycles(requestArgumentCaptor.capture());
		assertThat(requestArgumentCaptor.getValue().getStartDate()).isAfterOrEqualTo(searchWindow().toInstant());
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

		when(miraklMarketplacePlatformOperatorApiClient.getSellerBillingCycles(any())).thenReturn(billingCyclesMock);
		when(billingCyclesMock.getData()).thenReturn(List.of(billingCycle1Mock, billingCycle2Mock));
		when(invoiceConverterMock.convert(billingCycle1Mock)).thenReturn(myAccountingDocumentModel1Mock);
		when(invoiceConverterMock.convert(billingCycle2Mock)).thenReturn(myAccountingDocumentModel2Mock);

		final List<MyAccountingDocumentModel> creditNoteList = testObj.extractAccountingDocuments(now);

		verify(miraklMarketplacePlatformOperatorApiClient).getSellerBillingCycles(requestArgumentCaptor.capture());
		verify(testObj).extractAccountingDocuments(now, false);

		final MiraklGetSellerBillingCyclesRequest request = requestArgumentCaptor.getValue();

		assertThat(request.getStartDate()).isEqualTo(now.toInstant());
		assertThat(request.getPayOutStates()).containsExactly(MiraklPayOutState.TO_PAY);

		assertThat(creditNoteList).containsExactlyInAnyOrder(myAccountingDocumentModel1Mock,
				myAccountingDocumentModel2Mock);
	}

	@Test
	void extractAccountingDocuments_shouldReturnListOfAccountingDocuments_includingPaidInvoices() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 0, 55));
		final Date now = DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault());

		when(miraklMarketplacePlatformOperatorApiClient.getSellerBillingCycles(any())).thenReturn(billingCyclesMock);
		when(billingCyclesMock.getData()).thenReturn(List.of(billingCycle1Mock, billingCycle2Mock));
		when(invoiceConverterMock.convert(billingCycle1Mock)).thenReturn(myAccountingDocumentModel1Mock);
		when(invoiceConverterMock.convert(billingCycle2Mock)).thenReturn(myAccountingDocumentModel2Mock);

		final List<MyAccountingDocumentModel> creditNoteList = testObj.extractAccountingDocuments(now, true);

		verify(miraklMarketplacePlatformOperatorApiClient).getSellerBillingCycles(requestArgumentCaptor.capture());

		final MiraklGetSellerBillingCyclesRequest request = requestArgumentCaptor.getValue();

		assertThat(request.getStartDate()).isEqualTo(now.toInstant());
		assertThat(request.getPayOutStates()).isNull();

		assertThat(creditNoteList).containsExactlyInAnyOrder(myAccountingDocumentModel1Mock,
				myAccountingDocumentModel2Mock);
	}

	@Test
	void createAccountingDocumentRequest_shouldReturnRequestWithTargetInvoiceType() {
		final Date date = new Date();

		final MiraklGetSellerBillingCyclesRequest result = testObj.createAccountingDocumentRequest(date);

		assertThat(result.getLimit()).isEqualTo(100);
		assertThat(result.getStartDate()).isEqualTo(date.toInstant());
		assertThat(result.getPayOutStates()).containsExactly(MiraklPayOutState.TO_PAY);
	}

	@Test
	void getAccountingDocuments_whenRequestNeedsPagination_shouldRepeatRequestAndReturnAllInvoices() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 0, 55));
		final Date now = DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault());

		final List<MiraklSellerBillingCycle> firstPageResponse = getListOfMocks(MIRAKL_MAX_RESULTS_PER_PAGE);
		final List<MiraklSellerBillingCycle> secondPageResponse = getListOfMocks(MIRAKL_MAX_RESULTS_PER_PAGE / 2);

		when(billingCyclesMock.getData()).thenReturn(firstPageResponse);
		when(billingCyclesMock.getNextPageToken()).thenReturn(NEXT_TOKEN);
		when(billingCycles2Mock.getData()).thenReturn(secondPageResponse);
		when(billingCycles2Mock.getNextPageToken()).thenReturn(null);

		when(miraklMarketplacePlatformOperatorApiClient.getSellerBillingCycles(any())).thenReturn(billingCyclesMock)
			.thenReturn(billingCycles2Mock);

		final List<MyAccountingDocumentModel> expected = Stream
			.concat(firstPageResponse.stream(), secondPageResponse.stream())
			.map(this::mockAndReturn)
			.toList();

		final List<MyAccountingDocumentModel> result = testObj.extractAccountingDocuments(now);

		assertThat(result).containsExactlyElementsOf(expected);
	}

	private MyAccountingDocumentModel mockAndReturn(final MiraklSellerBillingCycle billingCycle) {
		final MyAccountingDocumentModel myAccountingDocumentModel = mock(MyAccountingDocumentModel.class);
		when(invoiceConverterMock.convert(billingCycle)).thenReturn(myAccountingDocumentModel);
		return myAccountingDocumentModel;
	}

	private List<MiraklSellerBillingCycle> getListOfMocks(final int numberOfMocks) {
		return IntStream.range(0, numberOfMocks).mapToObj(i -> mock(MiraklSellerBillingCycle.class)).toList();
	}

	private Date searchWindow() {
		return Date.from(TimeMachine.now().minusDays(maxNumberOfDaysForInvoiceIdSearch).toInstant(ZoneOffset.UTC));
	}

	static class MyAccountingDocumentsExtractServiceImplTest
			extends AbstractAccountingDocumentsExtractServiceImpl<MyAccountingDocumentModel> {

		private final Converter<MiraklSellerBillingCycle, MyAccountingDocumentModel> miraklInvoiceToInvoiceModelConverter;

		protected MyAccountingDocumentsExtractServiceImplTest(
				final Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter,
				final MiraklClient miraklMarketplacePlatformOperatorApiClient,
				final AccountingDocumentsLinksService accountingDocumentsLinksService,
				final MailNotificationUtil invoicesMailNotificationUtil,
				final Converter<MiraklSellerBillingCycle, MyAccountingDocumentModel> miraklInvoiceToInvoiceModelConverter) {
			super(miraklShopToAccountingModelConverter, miraklMarketplacePlatformOperatorApiClient,
					accountingDocumentsLinksService, invoicesMailNotificationUtil);
			this.miraklInvoiceToInvoiceModelConverter = miraklInvoiceToInvoiceModelConverter;
		}

		@Override
		protected InvoiceTypeEnum getInvoiceType() {
			return InvoiceTypeEnum.AUTO_INVOICE;
		}

		@Override
		protected Converter<MiraklSellerBillingCycle, MyAccountingDocumentModel> getMiraklInvoiceToAccountingModelConverter() {
			return miraklInvoiceToInvoiceModelConverter;
		}

	}

	static class MyAccountingDocumentModel extends AccountingDocumentModel {

		protected MyAccountingDocumentModel(final Builder<?> builder) {
			super(builder);
		}

	}

}
