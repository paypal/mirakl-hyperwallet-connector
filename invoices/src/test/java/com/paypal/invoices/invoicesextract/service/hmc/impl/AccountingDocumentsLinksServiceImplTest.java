package com.paypal.invoices.invoicesextract.service.hmc.impl;

import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes;
import com.paypal.infrastructure.itemlinks.model.MiraklItemLinkLocator;
import com.paypal.infrastructure.itemlinks.services.ItemLinksService;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklInvoiceLinksService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

		Collection<HyperwalletItemLinkLocator> result = testObj.findRequiredLinks(accountingDocumentModel1Mock);

		assertThat(result).containsExactlyInAnyOrder(hyperwalletItemLink1LocatorMock, hyperwalletItemLink2LocatorMock);
	}

	static class ArgThatContainsAllShops implements ArgumentMatcher<Collection<MiraklItemLinkLocator>> {

		@Override
		public boolean matches(Collection<MiraklItemLinkLocator> argument) {
			// formatter:off
			return argument.stream().map(MiraklItemLinkLocator::getId).collect(Collectors.toSet())
					.containsAll(Set.of(SHOP_ID_1, SHOP_ID_2));
			// formatter:on
		}

	}

}
