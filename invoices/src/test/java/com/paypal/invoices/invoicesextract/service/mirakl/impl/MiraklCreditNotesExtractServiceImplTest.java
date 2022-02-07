package com.paypal.invoices.invoicesextract.service.mirakl.impl;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoice;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoices;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceTypeEnum;
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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.mirakl.client.mmp.domain.accounting.document.MiraklAccountingDocumentPaymentStatus.PENDING;
import static com.mirakl.client.mmp.domain.accounting.document.MiraklAccountingDocumentType.MANUAL_CREDIT;
import static com.mirakl.client.mmp.request.payment.invoice.MiraklAccountingDocumentState.COMPLETE;
import static com.paypal.infrastructure.constants.HyperWalletConstants.MIRAKL_MAX_RESULTS_PER_PAGE;
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
	private MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClientMock;

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
	private HMCMiraklInvoices miraklInvoicesOneMock, miraklInvoicesTwoMock;

	@Mock
	private HMCMiraklInvoice miraklInvoiceOneMock, miraklInvoiceTwoMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private Converter<MiraklInvoice, CreditNoteModel> miraklInvoiceToCreditNoteModelConverter;

	@Mock
	private CreditNoteModel creditNoteOneMock, creditNoteTwoMock;

	@Mock
	private MiraklGetInvoicesRequest miraklGetInvoicesRequestMock;

	@Captor
	private ArgumentCaptor<MiraklGetInvoicesRequest> miraklGetInvoicesRequestArgumentCaptor;

	@BeforeEach
	void setUp() {
		testObj = new MiraklCreditNotesExtractServiceImpl(miraklMarketplacePlatformOperatorApiClientMock,
				miraklShopToAccountingModelConverter, miraklInvoiceToCreditNoteModelConverter,
				mailNotificationUtilMock);

		testObj = spy(testObj);
	}

	@Test
	void extractAccountingDocument_whenRequestRequiresPagination_shouldRequestAllShops_AndPopulateCreditNotesModelWithTheTokensStoredInMirakl() {
		final LocalDateTime now = LocalDateTime.now();
		TimeMachine.useFixedClockAt(now);
		final Date nowAsDate = DateUtil.convertToDate(now, ZoneId.systemDefault());

		final CreditNoteModel creditNoteOne = CreditNoteModel.builder().shopId(SHOP_ID_ONE).destinationToken(TOKEN_1)
				.build();
		final CreditNoteModel creditNoteTwo = CreditNoteModel.builder().shopId(SHOP_ID_TWO).destinationToken(TOKEN_2)
				.build();
		doReturn(List.of(creditNoteOne, creditNoteTwo)).when(testObj).getAccountingDocuments(nowAsDate);

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
	void extractAccountingDocument_whenNoInvoicesAreReturned_shouldReturnEmptyList() {
		final LocalDateTime now = LocalDateTime.now();
		TimeMachine.useFixedClockAt(now);
		final Date nowAsDate = DateUtil.convertToDate(now, ZoneId.systemDefault());

		doReturn(Collections.emptyList()).when(testObj).getAccountingDocuments(nowAsDate);

		final List<CreditNoteModel> result = testObj.extractAccountingDocument(nowAsDate);

		assertThat(result).isEmpty();
	}

	@Test
	void extractAccountingDocument_whenSeveralCreditNotesPerShopAreExtractedFromMirakl_shouldPopulateTheCreditNotes() {
		final LocalDateTime now = LocalDateTime.now();
		TimeMachine.useFixedClockAt(now);
		final Date nowAsDate = DateUtil.convertToDate(now, ZoneId.systemDefault());

		final CreditNoteModel creditNoteOne = CreditNoteModel.builder().shopId(SHOP_ID_ONE).destinationToken(TOKEN_1)
				.build();
		final CreditNoteModel creditNoteTwo = CreditNoteModel.builder().shopId(SHOP_ID_TWO).destinationToken(TOKEN_2)
				.build();
		final CreditNoteModel creditNoteThree = CreditNoteModel.builder().shopId(SHOP_ID_ONE).destinationToken(TOKEN_1)
				.build();

		doReturn(List.of(creditNoteOne, creditNoteTwo, creditNoteThree)).when(testObj)
				.getAccountingDocuments(nowAsDate);

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
	void getAccountingDocuments_whenMiraklExceptionIsThrown_shouldSendEmailNotification() {
		final LocalDateTime now = LocalDateTime.now();
		TimeMachine.useFixedClockAt(now);
		final Date nowAsDate = DateUtil.convertToDate(now, ZoneId.systemDefault());

		final CreditNoteModel creditNoteOne = CreditNoteModel.builder().shopId(SHOP_ID_ONE).destinationToken(TOKEN_1)
				.hyperwalletProgram(HYPERWALLET_PROGRAM).build();

		final List<CreditNoteModel> creditNoteList = List.of(creditNoteOne);
		doReturn(creditNoteList).when(testObj).getAccountingDocuments(nowAsDate);

		final MiraklApiException miraklApiException = new MiraklApiException(
				new MiraklErrorResponseBean(1, "Something went wrong"));
		doThrow(miraklApiException).when(miraklMarketplacePlatformOperatorApiClientMock)
				.getShops(any(MiraklGetShopsRequest.class));

		testObj.extractAccountingDocument(nowAsDate);

		verify(mailNotificationUtilMock).sendPlainTextEmail("Issue detected getting shops in Mirakl",
				String.format("Something went wrong getting information of " + "shops" + " [2000]%n%s",
						MiraklLoggingErrorsUtil.stringify(miraklApiException)));
	}

	@Test
	void getAccountingDocuments_shouldReturnListOfCreditNoteModels() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 0, 55));
		final Date now = DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault());

		when(miraklMarketplacePlatformOperatorApiClientMock.getInvoices(any())).thenReturn(miraklInvoicesOneMock);
		when(miraklInvoicesOneMock.getHmcInvoices()).thenReturn(List.of(miraklInvoiceOneMock, miraklInvoiceTwoMock));
		when(miraklInvoiceToCreditNoteModelConverter.convert(miraklInvoiceOneMock)).thenReturn(creditNoteOneMock);
		when(miraklInvoiceToCreditNoteModelConverter.convert(miraklInvoiceTwoMock)).thenReturn(creditNoteTwoMock);

		final List<CreditNoteModel> creditNoteList = testObj.getAccountingDocuments(now);

		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.getInvoices(miraklGetInvoicesRequestArgumentCaptor.capture());

		final MiraklGetInvoicesRequest miraklGetInvoicesRequest = miraklGetInvoicesRequestArgumentCaptor.getValue();

		assertThat(miraklGetInvoicesRequest.getStartDate()).isEqualTo(now);
		assertThat(miraklGetInvoicesRequest.getStates()).isEqualTo(List.of(COMPLETE));
		assertThat(miraklGetInvoicesRequest.getPaymentStatus()).isEqualTo(PENDING);

		assertThat(creditNoteList).containsExactlyInAnyOrder(creditNoteOneMock, creditNoteTwoMock);
	}

	@Test
	void getAccountingDocuments_whenNoMiraklCreditNotesAreReceived_shouldReturnEmptyList() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 0, 55));
		final Date now = DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault());

		when(miraklMarketplacePlatformOperatorApiClientMock.getInvoices(any())).thenReturn(miraklInvoicesOneMock);

		final List<CreditNoteModel> creditNoteList = testObj.getAccountingDocuments(now);

		assertThat(creditNoteList).isEqualTo(Collections.emptyList());
	}

	@Test
	void createAccountingDocumentRequest_shouldReturnRequestWithInvoiceType() {
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

		final List<HMCMiraklInvoice> firstPageResponseInvoices = getListOfHMCMiraklInvoiceMocks(
				MIRAKL_MAX_RESULTS_PER_PAGE);
		final List<HMCMiraklInvoice> secondPageResponseInvoices = getListOfHMCMiraklInvoiceMocks(
				MIRAKL_MAX_RESULTS_PER_PAGE / 2);
		final long totalResponseInvoices = firstPageResponseInvoices.size() + secondPageResponseInvoices.size();

		when(miraklMarketplacePlatformOperatorApiClientMock
				.getInvoices(argThat(request -> request != null && request.getOffset() == 0)))
						.thenReturn(miraklInvoicesOneMock);
		when(miraklMarketplacePlatformOperatorApiClientMock
				.getInvoices(argThat(request -> request != null && request.getOffset() == MIRAKL_MAX_RESULTS_PER_PAGE)))
						.thenReturn(miraklInvoicesTwoMock);
		when(miraklInvoicesOneMock.getTotalCount()).thenReturn(totalResponseInvoices);
		when(miraklInvoicesOneMock.getHmcInvoices()).thenReturn(firstPageResponseInvoices);
		when(miraklInvoicesTwoMock.getTotalCount()).thenReturn(totalResponseInvoices);
		when(miraklInvoicesTwoMock.getHmcInvoices()).thenReturn(secondPageResponseInvoices);

		final List<CreditNoteModel> expectedCreditNotes = Stream
				.concat(firstPageResponseInvoices.stream(), secondPageResponseInvoices.stream())
				.map(invoice -> mockAndReturn(invoice)).collect(Collectors.toList());

		final List<CreditNoteModel> result = testObj.getAccountingDocuments(now);

		assertThat(result).containsExactlyElementsOf(expectedCreditNotes);
	}

	private CreditNoteModel mockAndReturn(final HMCMiraklInvoice invoice) {
		final CreditNoteModel creditNoteModelMock = mock(CreditNoteModel.class);
		when(miraklInvoiceToCreditNoteModelConverter.convert(invoice)).thenReturn(creditNoteModelMock);
		return creditNoteModelMock;
	}

	private List<HMCMiraklInvoice> getListOfHMCMiraklInvoiceMocks(final int numberOfMocks) {
		return IntStream.range(0, numberOfMocks).mapToObj(i -> mock(HMCMiraklInvoice.class))
				.collect(Collectors.toList());
	}

	@Test
	void createShopRequest_shouldCreateShopRequestWithGivenIds() {
		final Set<String> shopIds = Set.of(SHOP_ID_ONE, SHOP_ID_TWO);

		final MiraklGetShopsRequest result = testObj.createShopRequest(shopIds);

		assertThat(result.getShopIds()).isEqualTo(shopIds);
		assertThat(result.isPaginate()).isFalse();
	}

}
