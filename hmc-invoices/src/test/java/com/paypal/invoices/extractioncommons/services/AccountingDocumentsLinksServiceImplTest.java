package com.paypal.invoices.extractioncommons.services;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes;
import com.paypal.infrastructure.itemlinks.model.MiraklItemLinkLocator;
import com.paypal.infrastructure.itemlinks.services.ItemLinksService;
import com.paypal.infrastructure.mirakl.support.MiraklShopUtils;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountingDocumentsLinksServiceImplTest {

	private static final String SHOP_ID_1 = "1";

	private static final String SHOP_ID_2 = "2";

	@InjectMocks
	private AccountingDocumentsLinksServiceImpl testObj;

	@Mock
	private ItemLinksService itemLinksServiceMock;

	@Mock
	private MiraklInvoiceLinksService miraklInvoiceLinksServiceMock;

	@Mock
	private AccountingDocumentModel accountingDocumentModel1Mock, accountingDocumentModel2Mock;

	@Mock
	private MiraklItemLinkLocator miraklItemLinkLocator1Mock, miraklItemLinkLocator2Mock;

	@Mock
	private HyperwalletItemLinkLocator hyperwalletItemLink1LocatorMock, hyperwalletItemLink2LocatorMock;

	@Test
	void storeRequiredLinks_shouldCreateNoneExistantLinksAndReturnAllLinks() {
		when(accountingDocumentModel1Mock.getShopId()).thenReturn(SHOP_ID_1);
		when(accountingDocumentModel2Mock.getShopId()).thenReturn(SHOP_ID_2);
		when(miraklItemLinkLocator1Mock.getId()).thenReturn(SHOP_ID_1);
		when(miraklItemLinkLocator2Mock.getId()).thenReturn(SHOP_ID_2);

		//@formatter:off
		when(itemLinksServiceMock.findLinks(argThat(new ArgThatContainsAllShops()),
					eq(Set.of(HyperwalletItemTypes.BANK_ACCOUNT, HyperwalletItemTypes.PROGRAM))))
				.thenReturn(Map.of(miraklItemLinkLocator1Mock, List.of(hyperwalletItemLink1LocatorMock),
						miraklItemLinkLocator2Mock, List.of()));

		when(miraklInvoiceLinksServiceMock.getInvoiceRelatedShopLinks(Set.of(SHOP_ID_1, SHOP_ID_2)))
				.thenReturn(Map.of(miraklItemLinkLocator1Mock, List.of(hyperwalletItemLink1LocatorMock, hyperwalletItemLink2LocatorMock),
						miraklItemLinkLocator2Mock, List.of(hyperwalletItemLink1LocatorMock, hyperwalletItemLink2LocatorMock)));
		//@formatter:on

		testObj.storeRequiredLinks(List.of(accountingDocumentModel1Mock, accountingDocumentModel2Mock));

		verify(itemLinksServiceMock, times(1)).createLinks(miraklItemLinkLocator1Mock,
				List.of(hyperwalletItemLink1LocatorMock, hyperwalletItemLink2LocatorMock));
		verify(itemLinksServiceMock, times(1)).createLinks(miraklItemLinkLocator2Mock,
				List.of(hyperwalletItemLink1LocatorMock, hyperwalletItemLink2LocatorMock));
	}

	@Test
	void findRequiredLinks_shouldRecoverAllLocalStoredLinks() {
		when(accountingDocumentModel1Mock.getShopId()).thenReturn("SHOP-1");
		when(itemLinksServiceMock.findLinks(
				(MiraklItemLinkLocator) argThat(x -> ((MiraklItemLinkLocator) x).getId().equals("SHOP-1")),
				eq(Set.of(HyperwalletItemTypes.BANK_ACCOUNT, HyperwalletItemTypes.PROGRAM))))
						.thenReturn(List.of(hyperwalletItemLink1LocatorMock, hyperwalletItemLink2LocatorMock));

		final Collection<HyperwalletItemLinkLocator> result = testObj.findRequiredLinks(accountingDocumentModel1Mock);

		assertThat(result).containsExactlyInAnyOrder(hyperwalletItemLink1LocatorMock, hyperwalletItemLink2LocatorMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	void storeRequiredLinks_shouldExtractRelevantLinksFromShop_andStoreThemUsingItemLinksService() {
		// given
		final MiraklShop miraklShop1 = new MiraklShop();
		miraklShop1.setId("ID-1");
		MiraklShopUtils.setProgram(miraklShop1, "PROGRAM-1");
		MiraklShopUtils.setBankAccountToken(miraklShop1, "BANK-ACCOUNT-1");

		final MiraklShop miraklShop2 = new MiraklShop();
		miraklShop2.setId("ID-2");
		MiraklShopUtils.setProgram(miraklShop2, "PROGRAM-2");
		MiraklShopUtils.setBankAccountToken(miraklShop2, "BANK-ACCOUNT-2");

		final List<MiraklShop> miraklShops = List.of(miraklShop1, miraklShop2);

		// when
		testObj.updateLinksFromShops(miraklShops);

		// then
		final ArgumentCaptor<MiraklItemLinkLocator> miraklItemLinkLocatorArgumentCaptor = ArgumentCaptor
				.forClass(MiraklItemLinkLocator.class);
		final ArgumentCaptor<Collection<HyperwalletItemLinkLocator>> hyperwalletItemLinkLocatorArgumentCaptor = ArgumentCaptor
				.forClass(Collection.class);

		verify(itemLinksServiceMock, times(2)).createLinks(miraklItemLinkLocatorArgumentCaptor.capture(),
				hyperwalletItemLinkLocatorArgumentCaptor.capture());

		final List<CreateItemLinksResult> itemLinks = getItemlLinksFromCreateLinksResult(
				miraklItemLinkLocatorArgumentCaptor.getAllValues(),
				hyperwalletItemLinkLocatorArgumentCaptor.getAllValues());

		assertThat(itemLinks.get(0).miraklItemLinkLocator.getId()).isEqualTo("ID-1");
		assertThat(itemLinks.get(0).hyperwalletItemLinkLocators).containsExactlyInAnyOrder(
				new HyperwalletItemLinkLocator("PROGRAM-1", HyperwalletItemTypes.PROGRAM),
				new HyperwalletItemLinkLocator("BANK-ACCOUNT-1", HyperwalletItemTypes.BANK_ACCOUNT));

		assertThat(itemLinks.get(1).miraklItemLinkLocator.getId()).isEqualTo("ID-2");
		assertThat(itemLinks.get(1).hyperwalletItemLinkLocators).containsExactlyInAnyOrder(
				new HyperwalletItemLinkLocator("PROGRAM-2", HyperwalletItemTypes.PROGRAM),
				new HyperwalletItemLinkLocator("BANK-ACCOUNT-2", HyperwalletItemTypes.BANK_ACCOUNT));
	}

	private List<CreateItemLinksResult> getItemlLinksFromCreateLinksResult(
			final List<MiraklItemLinkLocator> miraklItemLinkLocators,
			final List<Collection<HyperwalletItemLinkLocator>> hyperwalletItemLinkLocators) {
		return IntStream.range(0, miraklItemLinkLocators.size()).mapToObj(
				i -> new CreateItemLinksResult(miraklItemLinkLocators.get(i), hyperwalletItemLinkLocators.get(i)))
				.sorted(Comparator.comparing(x -> x.miraklItemLinkLocator.getId())).collect(Collectors.toList());
	}

	@Test
	void storeRequiredLinks_shouldDoNothing_forEmptyShops() {
		// given
		final List<MiraklShop> miraklShops = List.of();

		// when
		testObj.updateLinksFromShops(miraklShops);

		// then
		verify(itemLinksServiceMock, never()).createLinks(any(), any());
	}

	static class ArgThatContainsAllShops implements ArgumentMatcher<Collection<MiraklItemLinkLocator>> {

		@Override
		public boolean matches(final Collection<MiraklItemLinkLocator> argument) {
			// formatter:off
			return argument.stream().map(MiraklItemLinkLocator::getId).collect(Collectors.toSet())
					.containsAll(Set.of(SHOP_ID_1, SHOP_ID_2));
			// formatter:on
		}

	}

	static class CreateItemLinksResult {

		CreateItemLinksResult(final MiraklItemLinkLocator miraklItemLinkLocator,
				final Collection<HyperwalletItemLinkLocator> hyperwalletItemLinkLocators) {
			this.miraklItemLinkLocator = miraklItemLinkLocator;
			this.hyperwalletItemLinkLocators = hyperwalletItemLinkLocators;
		}

		final MiraklItemLinkLocator miraklItemLinkLocator;

		final Collection<HyperwalletItemLinkLocator> hyperwalletItemLinkLocators;

	}

}
