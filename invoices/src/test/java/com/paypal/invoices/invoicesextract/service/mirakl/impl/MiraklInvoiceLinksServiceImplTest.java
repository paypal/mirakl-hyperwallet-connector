package com.paypal.invoices.invoicesextract.service.mirakl.impl;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes;
import com.paypal.infrastructure.itemlinks.model.MiraklItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.MiraklItemTypes;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklInvoiceLinksServiceImplTest {

	private static final String SHOP_ID_1 = "1";

	private static final String SHOP_ID_2 = "2";

	private static final String PROGRAM_TOKEN_1 = "PROG-1";

	private static final String BANK_TOKEN_1 = "BANK-1";

	private static final String BANK_TOKEN_2 = "BANK-2";

	@InjectMocks
	private MiraklInvoiceLinksServiceImpl testObj;

	@Mock
	private MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverterMock;

	@Mock
	private MiraklShop miraklShop1Mock, miraklShop2Mock;

	@Mock
	private AccountingDocumentModel accountingDocumentModel1Mock, accountingDocumentModel2Mock;

	@Mock
	private MiraklShops miraklShopsMock, miraklShops2Mock;

	@Captor
	private ArgumentCaptor<MiraklGetShopsRequest> miraklGetShopsRequestArgumentCaptor;

	@Test
	void getShopLinks_shouldReturnProgramAndBankAccountLinks() {
		when(miraklShop1Mock.getId()).thenReturn(SHOP_ID_1);
		when(miraklShop2Mock.getId()).thenReturn(SHOP_ID_2);

		when(miraklShopToAccountingModelConverterMock.convert(miraklShop1Mock))
				.thenReturn(accountingDocumentModel1Mock);
		when(miraklShopToAccountingModelConverterMock.convert(miraklShop2Mock))
				.thenReturn(accountingDocumentModel2Mock);

		when(accountingDocumentModel1Mock.getHyperwalletProgram()).thenReturn(PROGRAM_TOKEN_1);
		when(accountingDocumentModel1Mock.getDestinationToken()).thenReturn(BANK_TOKEN_1);
		when(accountingDocumentModel2Mock.getHyperwalletProgram()).thenReturn(PROGRAM_TOKEN_1);
		when(accountingDocumentModel2Mock.getDestinationToken()).thenReturn(BANK_TOKEN_2);

		when(miraklMarketplacePlatformOperatorApiClientMock
				.getShops(argThat(req -> req.getShopIds().containsAll(Set.of(SHOP_ID_1, SHOP_ID_2)))))
						.thenReturn(miraklShopsMock);
		when(miraklShopsMock.getShops()).thenReturn(List.of(miraklShop1Mock, miraklShop2Mock));

		Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> result = testObj
				.getInvoiceRelatedShopLinks(Set.of("1", "2"));

		//@formatter:off
		MiraklItemLinkLocator miraklItemLinkLocator1 = new MiraklItemLinkLocator(SHOP_ID_1, MiraklItemTypes.SHOP);
		MiraklItemLinkLocator miraklItemLinkLocator2 = new MiraklItemLinkLocator(SHOP_ID_2, MiraklItemTypes.SHOP);
		assertThat(result.entrySet()).hasSize(2);
		assertThat(result.keySet()).containsExactlyInAnyOrder(miraklItemLinkLocator1,miraklItemLinkLocator2);
		assertThat(result.get(miraklItemLinkLocator1)).containsExactlyInAnyOrder(
				new HyperwalletItemLinkLocator(PROGRAM_TOKEN_1, HyperwalletItemTypes.PROGRAM),
				new HyperwalletItemLinkLocator(BANK_TOKEN_1, HyperwalletItemTypes.BANK_ACCOUNT));
		assertThat(result.get(miraklItemLinkLocator2)).containsExactlyInAnyOrder(
				new HyperwalletItemLinkLocator(PROGRAM_TOKEN_1, HyperwalletItemTypes.PROGRAM),
				new HyperwalletItemLinkLocator(BANK_TOKEN_2, HyperwalletItemTypes.BANK_ACCOUNT));
		//@formatter:on
	}

	@Test
	void getShopLinks_shouldNotReturnProgramAndBankAccountLinks_WhenShopFieldsAreEmpty() {
		when(miraklShop1Mock.getId()).thenReturn(SHOP_ID_1);
		when(miraklShop2Mock.getId()).thenReturn(SHOP_ID_2);

		when(miraklShopToAccountingModelConverterMock.convert(miraklShop1Mock))
				.thenReturn(accountingDocumentModel1Mock);
		when(miraklShopToAccountingModelConverterMock.convert(miraklShop2Mock))
				.thenReturn(accountingDocumentModel2Mock);

		when(accountingDocumentModel1Mock.getHyperwalletProgram()).thenReturn(PROGRAM_TOKEN_1);
		when(accountingDocumentModel1Mock.getDestinationToken()).thenReturn(null);
		when(accountingDocumentModel2Mock.getHyperwalletProgram()).thenReturn(null);
		when(accountingDocumentModel2Mock.getDestinationToken()).thenReturn(BANK_TOKEN_2);

		when(miraklMarketplacePlatformOperatorApiClientMock
				.getShops(argThat(req -> req.getShopIds().containsAll(Set.of(SHOP_ID_1, SHOP_ID_2)))))
						.thenReturn(miraklShopsMock);
		when(miraklShopsMock.getShops()).thenReturn(List.of(miraklShop1Mock, miraklShop2Mock));

		Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> result = testObj
				.getInvoiceRelatedShopLinks(Set.of("1", "2"));

		//@formatter:off
		MiraklItemLinkLocator miraklItemLinkLocator1 = new MiraklItemLinkLocator(SHOP_ID_1, MiraklItemTypes.SHOP);
		MiraklItemLinkLocator miraklItemLinkLocator2 = new MiraklItemLinkLocator(SHOP_ID_2, MiraklItemTypes.SHOP);
		assertThat(result.entrySet()).hasSize(2);
		assertThat(result.keySet()).containsExactlyInAnyOrder(miraklItemLinkLocator1,miraklItemLinkLocator2);
		assertThat(result.get(miraklItemLinkLocator1)).containsExactlyInAnyOrder(
				new HyperwalletItemLinkLocator(PROGRAM_TOKEN_1, HyperwalletItemTypes.PROGRAM));
		assertThat(result.get(miraklItemLinkLocator2)).containsExactlyInAnyOrder(
				new HyperwalletItemLinkLocator(BANK_TOKEN_2, HyperwalletItemTypes.BANK_ACCOUNT));
		//@formatter:on
	}

	@Test
	void getShopLinks_shouldIgnoreWhenMiraklHttpRequestFailAndReturnEmptyShopList() {
		when(miraklMarketplacePlatformOperatorApiClientMock
				.getShops(argThat(req -> req.getShopIds().containsAll(Set.of(SHOP_ID_1, SHOP_ID_2)))))
						.thenThrow(new MiraklApiException(new MiraklErrorResponseBean(1, "Error")));

		Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> result = testObj
				.getInvoiceRelatedShopLinks(Set.of("1", "2"));

		MiraklItemLinkLocator miraklItemLinkLocator1 = new MiraklItemLinkLocator(SHOP_ID_1, MiraklItemTypes.SHOP);
		MiraklItemLinkLocator miraklItemLinkLocator2 = new MiraklItemLinkLocator(SHOP_ID_2, MiraklItemTypes.SHOP);
		assertThat(result.entrySet()).hasSize(2);
		assertThat(result.keySet()).containsExactlyInAnyOrder(miraklItemLinkLocator1, miraklItemLinkLocator2);
		assertThat(result.get(miraklItemLinkLocator1)).isEmpty();
		assertThat(result.get(miraklItemLinkLocator2)).isEmpty();
	}

	@Test
	void getAllShops_shouldSplitRequestInBatchesOfFixedSize_AndContinueOnErrorsInIndividualBatches() {
		final MiraklApiException miraklApiException = new MiraklApiException(
				new MiraklErrorResponseBean(1, "Something went wrong"));
		//@formatter:on
		when(miraklMarketplacePlatformOperatorApiClientMock
				.getShops(argThat(request -> request.getShopIds().size() <= 100))).thenReturn(miraklShopsMock)
						.thenThrow(miraklApiException).thenReturn(miraklShops2Mock);
		//@formatter:off
		when(miraklShopsMock.getShops()).thenReturn(List.of(miraklShop1Mock));
		when(miraklShops2Mock.getShops()).thenReturn(List.of(miraklShop2Mock));
		when(miraklShop1Mock.getId()).thenReturn("1");
		when(miraklShop2Mock.getId()).thenReturn("2");

		Set<String> invoiceIds = Stream.iterate(1, n -> n + 1)
				.limit(250)
				.map(String::valueOf)
				.collect(Collectors.toSet());

		Map<String, MiraklShop> result = testObj.getAllShops(invoiceIds);

		assertThat(result).hasSize(2);
		assertThat(result.values()).containsExactlyInAnyOrder(miraklShop1Mock, miraklShop2Mock);

		verify(miraklMarketplacePlatformOperatorApiClientMock, times(3))
				.getShops(miraklGetShopsRequestArgumentCaptor.capture());
		MiraklGetShopsRequest failedRequest = miraklGetShopsRequestArgumentCaptor.getAllValues().get(1);
	}

}
