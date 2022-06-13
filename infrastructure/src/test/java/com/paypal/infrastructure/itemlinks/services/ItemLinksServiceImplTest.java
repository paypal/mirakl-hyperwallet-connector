package com.paypal.infrastructure.itemlinks.services;

import com.paypal.infrastructure.itemlinks.converters.ItemLinksModelEntityConverter;
import com.paypal.infrastructure.itemlinks.entities.ItemLinkEntity;
import com.paypal.infrastructure.itemlinks.model.*;
import com.paypal.infrastructure.itemlinks.repository.ItemLinkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.webservices.server.AutoConfigureMockWebServiceClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemLinksServiceImplTest {

	@InjectMocks
	private ItemLinksServiceImpl testObj;

	@Mock
	private ItemLinkRepository itemLinkRepositoryMock;

	@Mock
	private ItemLinksModelEntityConverter itemLinksModelEntityConverterMock;

	@Mock
	private HyperwalletItemLinkLocator hyperwalletItemLinkLocator1Mock, hyperwalletItemLinkLocator2Mock;

	@Mock
	private MiraklItemLinkLocator miraklItemLinkLocator1Mock, miraklItemLinkLocator2Mock;

	@Mock
	private ItemLinkEntity itemLinkEntity1Mock, itemLinkEntity2Mock;

	@Test
	void createLinks() {
		when(itemLinksModelEntityConverterMock.from(miraklItemLinkLocator1Mock, hyperwalletItemLinkLocator1Mock))
				.thenReturn(itemLinkEntity1Mock);
		when(itemLinksModelEntityConverterMock.from(miraklItemLinkLocator1Mock, hyperwalletItemLinkLocator2Mock))
				.thenReturn(itemLinkEntity2Mock);

		testObj.createLinks(miraklItemLinkLocator1Mock,
				List.of(hyperwalletItemLinkLocator1Mock, hyperwalletItemLinkLocator2Mock));

		verify(itemLinkRepositoryMock).saveAll(Set.of(itemLinkEntity1Mock, itemLinkEntity2Mock));
	}

	@Test
	void findLinks() {
		when(miraklItemLinkLocator1Mock.getId()).thenReturn("H1");
		when(miraklItemLinkLocator1Mock.getType()).thenReturn(MiraklItemTypes.SHOP);
		when(miraklItemLinkLocator1Mock.getSystem()).thenReturn(ItemLinkExternalSystem.MIRAKL);
		when(miraklItemLinkLocator2Mock.getId()).thenReturn("H2");
		when(miraklItemLinkLocator2Mock.getType()).thenReturn(MiraklItemTypes.SHOP);
		when(miraklItemLinkLocator2Mock.getSystem()).thenReturn(ItemLinkExternalSystem.MIRAKL);

		Set<String> targetItemTypes = Set.of(HyperwalletItemTypes.BANK_ACCOUNT.toString(),
				HyperwalletItemTypes.PROGRAM.toString());
		// formatter:off
		when(itemLinkRepositoryMock.findBySourceSystemAndSourceIdAndSourceTypeAndTargetSystemAndTargetTypeIn(
				eq("MIRAKL"), eq("H1"), eq("SHOP"), eq("HYPERWALLET"), argThat(x -> x.containsAll(targetItemTypes))))
						.thenReturn(List.of(itemLinkEntity1Mock, itemLinkEntity2Mock));

		when(itemLinkRepositoryMock.findBySourceSystemAndSourceIdAndSourceTypeAndTargetSystemAndTargetTypeIn(
				eq("MIRAKL"), eq("H2"), eq("SHOP"), eq("HYPERWALLET"), argThat(x -> x.containsAll(targetItemTypes))))
						.thenReturn(List.of());
		// formatter:on

		when(itemLinksModelEntityConverterMock.hyperwalletLocatorFromLinkTarget(itemLinkEntity1Mock))
				.thenReturn(hyperwalletItemLinkLocator1Mock);
		when(itemLinksModelEntityConverterMock.hyperwalletLocatorFromLinkTarget(itemLinkEntity2Mock))
				.thenReturn(hyperwalletItemLinkLocator2Mock);

		Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> result = testObj.findLinks(
				Set.of(miraklItemLinkLocator1Mock, miraklItemLinkLocator2Mock),
				Set.of(HyperwalletItemTypes.BANK_ACCOUNT, HyperwalletItemTypes.PROGRAM));

		assertThat(result).containsKeys(miraklItemLinkLocator1Mock, miraklItemLinkLocator1Mock);
		assertThat(result.get(miraklItemLinkLocator1Mock)).containsExactlyInAnyOrder(hyperwalletItemLinkLocator1Mock,
				hyperwalletItemLinkLocator2Mock);
		assertThat(result.get(miraklItemLinkLocator2Mock)).isEmpty();
	}

}
