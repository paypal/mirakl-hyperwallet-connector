package com.paypal.invoices.invoicesextract.service.mirakl.impl;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoice;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoices;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.InvoiceTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractAccountingDocumentsExtractServiceImplTest {

	private static final String ACCOUNTDOCUMENT_ID_1 = "ACCOUNTDOCUMENT_ID_1";

	private static final String ACCOUNTDOCUMENT_ID_2 = "ACCOUNTDOCUMENT_ID_2";

	private static final String ACCOUNTDOCUMENT_ID_3 = "ACCOUNTDOCUMENT_ID_3";

	@Value("${invoices.searchinvoices.maxdays}")
	protected int maxNumberOfDaysForInvoiceIdSearch;

	@InjectMocks
	private MyAccountingDocumentsExtractServiceImplTest testObj;

	@Mock
	protected MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClient;

	@Mock
	private Converter<HMCMiraklInvoice, MyAccountingDocumentModel> invoiceConverterMock;

	@Mock
	private MyAccountingDocumentModel myAccountingDocumentModel1Mock, myAccountingDocumentModel2Mock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private HMCMiraklInvoice hmcMiraklInvoice1Mock, hmcMiraklInvoice2Mock, hmcMiraklInvoice3Mock;

	@Mock
	private HMCMiraklInvoices hmcMiraklInvoicesMock;

	@Mock
	private MiraklShops miraklShops1Mock, miraklShops2Mock, miraklShops3Mock;

	@Mock
	private MiraklShop miraklShop1Mock, miraklShop2Mock, miraklShop3Mock;

	@Captor
	private ArgumentCaptor<MiraklGetInvoicesRequest> miraklGetInvoicesRequestArgumentCaptor;

	@Captor
	private ArgumentCaptor<MiraklGetShopsRequest> miraklGetShopsRequestArgumentCaptor;

	@Test
	void extractAccountingDocumentsById_shouldReturnDocuments_whenTheyAreInsideSearchWindow() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());

		when(miraklMarketplacePlatformOperatorApiClient.getInvoices(any())).thenReturn(hmcMiraklInvoicesMock);
		when(hmcMiraklInvoicesMock.getHmcInvoices())
				.thenReturn(List.of(hmcMiraklInvoice1Mock, hmcMiraklInvoice2Mock, hmcMiraklInvoice3Mock));
		when(hmcMiraklInvoice1Mock.getId()).thenReturn(ACCOUNTDOCUMENT_ID_1);
		when(hmcMiraklInvoice2Mock.getId()).thenReturn(ACCOUNTDOCUMENT_ID_2);
		when(hmcMiraklInvoice3Mock.getId()).thenReturn(ACCOUNTDOCUMENT_ID_3);
		when(invoiceConverterMock.convert(hmcMiraklInvoice1Mock)).thenReturn(myAccountingDocumentModel1Mock);
		when(invoiceConverterMock.convert(hmcMiraklInvoice2Mock)).thenReturn(myAccountingDocumentModel2Mock);

		Collection<MyAccountingDocumentModel> result = testObj
				.extractAccountingDocuments(List.of(ACCOUNTDOCUMENT_ID_1, ACCOUNTDOCUMENT_ID_2));

		assertThat(result).containsExactlyInAnyOrder(myAccountingDocumentModel1Mock, myAccountingDocumentModel2Mock);
		verify(miraklMarketplacePlatformOperatorApiClient)
				.getInvoices(miraklGetInvoicesRequestArgumentCaptor.capture());
		assertThat(miraklGetInvoicesRequestArgumentCaptor.getValue().getStartDate()).isAfterOrEqualTo(searchWindow());
		assertThat(miraklGetInvoicesRequestArgumentCaptor.getValue().getType())
				.hasToString(testObj.getInvoiceType().toString());
	}

	@Test
	void getMiraklShops_shouldSplitRequestInBatchesOfFixedSize() {
		//@formatter:off
		List<MyAccountingDocumentModel> invoicesMocks = Stream.iterate(1, n -> n + 1)
				.limit(250)
				.map(this::createMyAccountingDocumentModelMock)
				.collect(Collectors.toList());

		when(miraklMarketplacePlatformOperatorApiClient.getShops(argThat(request -> request.getShopIds().size() <= 100)))
				.thenReturn(miraklShops1Mock)
				.thenReturn(miraklShops2Mock)
				.thenReturn(miraklShops3Mock);
		//@formatter:on
		when(miraklShops1Mock.getShops()).thenReturn(List.of(miraklShop1Mock));
		when(miraklShops2Mock.getShops()).thenReturn(List.of(miraklShop2Mock));
		when(miraklShops3Mock.getShops()).thenReturn(List.of(miraklShop3Mock));

		List<MiraklShop> result = testObj.getMiraklShops(invoicesMocks);

		assertThat(result).containsExactlyInAnyOrder(miraklShop1Mock, miraklShop2Mock, miraklShop3Mock);

		verify(miraklMarketplacePlatformOperatorApiClient, times(2))
				.getShops(argThat(request -> request.getShopIds().size() == 100));
		verify(miraklMarketplacePlatformOperatorApiClient, times(1))
				.getShops(argThat(request -> request.getShopIds().size() == 50));
	}

	@Test
	void getMiraklShops_shouldSplitRequestInBatchesOfFixedSize_AndContinueOnErrorsInIndividualBatches() {
		//@formatter:off
		List<MyAccountingDocumentModel> invoicesMocks = Stream.iterate(1, n -> n + 1)
				.limit(250)
				.map(this::createMyAccountingDocumentModelMock)
				.collect(Collectors.toList());

		final MiraklApiException miraklApiException = new MiraklApiException(
				new MiraklErrorResponseBean(1, "Something went wrong"));
		when(miraklMarketplacePlatformOperatorApiClient.getShops(argThat(request -> request.getShopIds().size() <= 100)))
				.thenReturn(miraklShops1Mock)
				.thenThrow(miraklApiException)
				.thenReturn(miraklShops3Mock);
		//@formatter:on
		when(miraklShops1Mock.getShops()).thenReturn(List.of(miraklShop1Mock));
		when(miraklShops3Mock.getShops()).thenReturn(List.of(miraklShop3Mock));

		List<MiraklShop> result = testObj.getMiraklShops(invoicesMocks);

		assertThat(result).containsExactlyInAnyOrder(miraklShop1Mock, miraklShop3Mock);

		verify(miraklMarketplacePlatformOperatorApiClient, times(3))
				.getShops(miraklGetShopsRequestArgumentCaptor.capture());
		MiraklGetShopsRequest failedRequest = miraklGetShopsRequestArgumentCaptor.getAllValues().get(1);
		verify(mailNotificationUtilMock).sendPlainTextEmail("Issue detected getting shops in Mirakl",
				String.format("Something went wrong getting information of " + "shops" + " [%s]%n%s",
						failedRequest.getShopIds().stream().sorted().collect(Collectors.joining(",")),
						MiraklLoggingErrorsUtil.stringify(miraklApiException)));
	}

	private MyAccountingDocumentModel createMyAccountingDocumentModelMock(int id) {
		MyAccountingDocumentModel myAccountingDocumentModelMock = Mockito.mock(MyAccountingDocumentModel.class);
		when(myAccountingDocumentModelMock.getShopId()).thenReturn(String.valueOf(id));

		return myAccountingDocumentModelMock;
	}

	private Date searchWindow() {
		return Date.from(TimeMachine.now().minusDays(maxNumberOfDaysForInvoiceIdSearch).toInstant(ZoneOffset.UTC));
	}

	static class MyAccountingDocumentsExtractServiceImplTest
			extends AbstractAccountingDocumentsExtractServiceImpl<MyAccountingDocumentModel> {

		private final Converter<HMCMiraklInvoice, MyAccountingDocumentModel> miraklInvoiceToInvoiceModelConverter;

		protected MyAccountingDocumentsExtractServiceImplTest(
				Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter,
				MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClient,
				MailNotificationUtil invoicesMailNotificationUtil,
				Converter<HMCMiraklInvoice, MyAccountingDocumentModel> miraklInvoiceToInvoiceModelConverter) {
			super(miraklShopToAccountingModelConverter, miraklMarketplacePlatformOperatorApiClient,
					invoicesMailNotificationUtil);
			this.miraklInvoiceToInvoiceModelConverter = miraklInvoiceToInvoiceModelConverter;
		}

		@Override
		protected InvoiceTypeEnum getInvoiceType() {
			return InvoiceTypeEnum.AUTO_INVOICE;
		}

		@Override
		protected Converter<HMCMiraklInvoice, MyAccountingDocumentModel> getMiraklInvoiceToAccountingModelConverter() {
			return miraklInvoiceToInvoiceModelConverter;
		}

	}

	static class MyAccountingDocumentModel extends AccountingDocumentModel {

		protected MyAccountingDocumentModel(Builder<?> builder) {
			super(builder);
		}

	}

}
