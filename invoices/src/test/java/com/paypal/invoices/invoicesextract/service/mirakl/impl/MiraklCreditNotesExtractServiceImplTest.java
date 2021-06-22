package com.paypal.invoices.invoicesextract.service.mirakl.impl;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.accounting.document.MiraklAccountingDocumentPaymentStatus;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoices;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.mirakl.client.mmp.request.payment.invoice.MiraklAccountingDocumentState;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklCreditNotesExtractServiceImplTest {

	private static final String SHOP_ID_ONE = "2000";

	private static final String SHOP_ID_TWO = "2001";

	private static final String TOKEN_1 = "token1";

	private static final String TOKEN_2 = "token2";

	private static final String SHOP_ID_ATTRIBUTE = "shopId";

	private static final String DESTINATION_TOKEN_ATTRIBUTE = "destinationToken";

	private static final String HYPERWALLET_PROGRAM = "hwProgram";

	private MiraklCreditNotesExtractServiceImpl testObj;

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private MiraklShops miraklShopsMock;

	@Mock
	private MiraklShop miraklShopOneMock, miraklShopTwoMock;

	@Mock
	private Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter;

	@Mock
	private CreditNoteModel creditNoteModelModelConvertedFromShopOneMock, creditNoteModelConvertedFromShopTwoMock,
			creditNoteModelConvertedFromShopThreeMock;

	@Captor
	private ArgumentCaptor<MiraklGetShopsRequest> miraklGetShopsRequestArgumentCaptor;

	@Mock
	private MiraklShop miraklShopThreeMock;

	@Mock
	private MiraklInvoices miraklInvoicesMock;

	@Mock
	private MiraklInvoice miraklInvoiceOneMock, miraklInvoiceTwoMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private Converter<MiraklInvoice, CreditNoteModel> miraklInvoiceCreditNoteModelConverter;

	@Mock
	private CreditNoteModel creditNoteOneMock, creditNoteTwoMock;

	@Captor
	private ArgumentCaptor<MiraklGetInvoicesRequest> miraklGetInvoicesRequestArgumentCaptor;

	@BeforeEach
	void setUp() {
		testObj = new MiraklCreditNotesExtractServiceImpl(miraklMarketplacePlatformOperatorApiClientMock,
				miraklShopToAccountingModelConverter, miraklInvoiceCreditNoteModelConverter, mailNotificationUtilMock);

		testObj = spy(testObj);
	}

	@Test
	void extractCreditNotes_shouldPopulateCreditNotesModelWithTheTokensStoredInMirakl() {
		final LocalDateTime now = LocalDateTime.now();
		TimeMachine.useFixedClockAt(now);
		final Date nowAsDate = DateUtil.convertToDate(now, ZoneId.systemDefault());

		final CreditNoteModel creditNoteOne = CreditNoteModel.builder().shopId(SHOP_ID_ONE).destinationToken(TOKEN_1)
				.build();
		final CreditNoteModel creditNoteTwo = CreditNoteModel.builder().shopId(SHOP_ID_TWO).destinationToken(TOKEN_2)
				.build();
		doReturn(List.of(creditNoteOne, creditNoteTwo)).when(testObj).getAccountingDocument(nowAsDate);

		when(miraklShopsMock.getShops()).thenReturn(List.of(miraklShopOneMock, miraklShopTwoMock));
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(any(MiraklGetShopsRequest.class)))
				.thenReturn(miraklShopsMock);
		when(miraklShopToAccountingModelConverter.convert(miraklShopOneMock))
				.thenReturn(creditNoteModelModelConvertedFromShopOneMock);
		when(miraklShopToAccountingModelConverter.convert(miraklShopTwoMock))
				.thenReturn(creditNoteModelConvertedFromShopTwoMock);
		when(creditNoteModelModelConvertedFromShopOneMock.getShopId()).thenReturn(SHOP_ID_ONE);
		when(creditNoteModelConvertedFromShopTwoMock.getShopId()).thenReturn(SHOP_ID_TWO);
		when(creditNoteModelModelConvertedFromShopOneMock.getDestinationToken()).thenReturn(TOKEN_1);
		when(creditNoteModelConvertedFromShopTwoMock.getDestinationToken()).thenReturn(TOKEN_2);
		when(creditNoteModelModelConvertedFromShopOneMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);
		when(creditNoteModelConvertedFromShopTwoMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);

		final List<CreditNoteModel> result = testObj.extractAccountingDocument(nowAsDate);

		verify(miraklMarketplacePlatformOperatorApiClientMock).getShops(miraklGetShopsRequestArgumentCaptor.capture());

		assertThat(miraklGetShopsRequestArgumentCaptor.getValue().getShopIds()).containsExactlyInAnyOrder(SHOP_ID_ONE,
				SHOP_ID_TWO);

		assertThat(result.get(0)).hasFieldOrPropertyWithValue(SHOP_ID_ATTRIBUTE, SHOP_ID_ONE)
				.hasFieldOrPropertyWithValue(DESTINATION_TOKEN_ATTRIBUTE, TOKEN_1);
		assertThat(result.get(1)).hasFieldOrPropertyWithValue(SHOP_ID_ATTRIBUTE, SHOP_ID_TWO)
				.hasFieldOrPropertyWithValue(DESTINATION_TOKEN_ATTRIBUTE, TOKEN_2);
	}

	@Test
	void extractCreditNotes_shouldPopulateTheCreditNotesWhenSeveralCreditNotesPerShopAreExtractedFromMirakl() {
		final LocalDateTime now = LocalDateTime.now();
		TimeMachine.useFixedClockAt(now);
		final Date nowAsDate = DateUtil.convertToDate(now, ZoneId.systemDefault());

		final CreditNoteModel creditNoteOne = CreditNoteModel.builder().shopId(SHOP_ID_ONE).destinationToken(TOKEN_1)
				.build();
		final CreditNoteModel creditNoteTwo = CreditNoteModel.builder().shopId(SHOP_ID_TWO).destinationToken(TOKEN_2)
				.build();
		final CreditNoteModel creditNoteThree = CreditNoteModel.builder().shopId(SHOP_ID_ONE).destinationToken(TOKEN_1)
				.build();

		doReturn(List.of(creditNoteOne, creditNoteTwo, creditNoteThree)).when(testObj).getAccountingDocument(nowAsDate);

		when(miraklShopsMock.getShops()).thenReturn(List.of(miraklShopOneMock, miraklShopTwoMock, miraklShopThreeMock));
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(any(MiraklGetShopsRequest.class)))
				.thenReturn(miraklShopsMock);

		when(miraklShopToAccountingModelConverter.convert(miraklShopOneMock))
				.thenReturn(creditNoteModelModelConvertedFromShopOneMock);
		when(miraklShopToAccountingModelConverter.convert(miraklShopTwoMock))
				.thenReturn(creditNoteModelConvertedFromShopTwoMock);
		when(miraklShopToAccountingModelConverter.convert(miraklShopThreeMock))
				.thenReturn(creditNoteModelConvertedFromShopThreeMock);
		when(creditNoteModelModelConvertedFromShopOneMock.getShopId()).thenReturn(SHOP_ID_ONE);
		when(creditNoteModelConvertedFromShopTwoMock.getShopId()).thenReturn(SHOP_ID_TWO);
		when(creditNoteModelConvertedFromShopThreeMock.getShopId()).thenReturn(SHOP_ID_ONE);
		when(creditNoteModelModelConvertedFromShopOneMock.getDestinationToken()).thenReturn(TOKEN_1);
		when(creditNoteModelConvertedFromShopTwoMock.getDestinationToken()).thenReturn(TOKEN_2);
		when(creditNoteModelConvertedFromShopThreeMock.getDestinationToken()).thenReturn(TOKEN_1);
		when(creditNoteModelModelConvertedFromShopOneMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);
		when(creditNoteModelConvertedFromShopTwoMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);
		when(creditNoteModelConvertedFromShopThreeMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);

		final List<CreditNoteModel> result = testObj.extractAccountingDocument(nowAsDate);

		verify(miraklMarketplacePlatformOperatorApiClientMock).getShops(miraklGetShopsRequestArgumentCaptor.capture());

		assertThat(miraklGetShopsRequestArgumentCaptor.getValue().getShopIds()).containsExactlyInAnyOrder(SHOP_ID_ONE,
				SHOP_ID_TWO);

		assertThat(result.get(0)).hasFieldOrPropertyWithValue(SHOP_ID_ATTRIBUTE, SHOP_ID_ONE)
				.hasFieldOrPropertyWithValue(DESTINATION_TOKEN_ATTRIBUTE, TOKEN_1);
		assertThat(result.get(1)).hasFieldOrPropertyWithValue(SHOP_ID_ATTRIBUTE, SHOP_ID_TWO)
				.hasFieldOrPropertyWithValue(DESTINATION_TOKEN_ATTRIBUTE, TOKEN_2);
		assertThat(result.get(2)).hasFieldOrPropertyWithValue(SHOP_ID_ATTRIBUTE, SHOP_ID_ONE)
				.hasFieldOrPropertyWithValue(DESTINATION_TOKEN_ATTRIBUTE, TOKEN_1);
	}

	@Test
	void extractCreditNotes_shouldSendEmailNotification_whenMiraklExceptionIsThrown() {
		final LocalDateTime now = LocalDateTime.now();
		TimeMachine.useFixedClockAt(now);
		final Date nowAsDate = DateUtil.convertToDate(now, ZoneId.systemDefault());

		final CreditNoteModel creditNoteOne = CreditNoteModel.builder().shopId(SHOP_ID_ONE).destinationToken(TOKEN_1)
				.hyperwalletProgram(HYPERWALLET_PROGRAM).build();

		final List<CreditNoteModel> creditNoteList = List.of(creditNoteOne);
		doReturn(creditNoteList).when(testObj).getAccountingDocument(nowAsDate);

		final var miraklApiException = new MiraklApiException(new MiraklErrorResponseBean(1, "Something went wrong"));
		doThrow(miraklApiException).when(miraklMarketplacePlatformOperatorApiClientMock)
				.getShops(any(MiraklGetShopsRequest.class));

		testObj.extractAccountingDocument(nowAsDate);

		verify(mailNotificationUtilMock).sendPlainTextEmail("Issue detected getting shops in Mirakl",
				String.format("Something went wrong getting information of " + "shops" + " [2000]%n%s",
						MiraklLoggingErrorsUtil.stringify(miraklApiException)));
	}

	@Test
	void getCreditNotes_shouldReturnListOfCreditNoteModels() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 0, 55));
		final Date now = DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault());

		when(miraklMarketplacePlatformOperatorApiClientMock.getInvoices(any())).thenReturn(miraklInvoicesMock);
		when(miraklInvoicesMock.getInvoices()).thenReturn(List.of(miraklInvoiceOneMock, miraklInvoiceTwoMock));
		when(miraklInvoiceCreditNoteModelConverter.convert(miraklInvoiceOneMock)).thenReturn(creditNoteOneMock);
		when(miraklInvoiceCreditNoteModelConverter.convert(miraklInvoiceTwoMock)).thenReturn(creditNoteTwoMock);

		final List<CreditNoteModel> creditNoteList = testObj.getAccountingDocument(now);

		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.getInvoices(miraklGetInvoicesRequestArgumentCaptor.capture());

		final MiraklGetInvoicesRequest miraklGetInvoicesRequest = miraklGetInvoicesRequestArgumentCaptor.getValue();

		assertThat(miraklGetInvoicesRequest.getStartDate()).isEqualTo(now);
		assertThat(miraklGetInvoicesRequest.getStates()).isEqualTo(List.of(MiraklAccountingDocumentState.COMPLETE));
		assertThat(miraklGetInvoicesRequest.getPaymentStatus())
				.isEqualTo(MiraklAccountingDocumentPaymentStatus.PENDING);

		assertThat(creditNoteList).containsExactlyInAnyOrder(creditNoteOneMock, creditNoteTwoMock);
	}

	@Test
	void getCreditNotes_shouldReturnEmptyListWhenNoMiraklCreditNotesAreReceived() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 0, 55));
		final Date now = DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault());

		when(miraklMarketplacePlatformOperatorApiClientMock.getInvoices(any())).thenReturn(null);

		final List<CreditNoteModel> creditNoteList = testObj.getAccountingDocument(now);

		assertThat(creditNoteList).isEqualTo(Collections.emptyList());
	}

}
