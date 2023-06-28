package com.paypal.infrastructure.itemlinks;

import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes;
import com.paypal.infrastructure.itemlinks.model.MiraklItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.MiraklItemTypes;
import com.paypal.infrastructure.itemlinks.services.ItemLinksService;
import com.paypal.testsupport.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ItemLinksITTest extends AbstractIntegrationTest {

	@Autowired
	private ItemLinksService itemLinksService;

	private final ItemLinksMother itemLinksMother = new ItemLinksMother();

	@Test
	void shouldCreateAndRecoverLinks() {
		final MiraklItemLinkLocator miraklItemLinkLocator1 = itemLinksMother.aMiraklItemLinkLocator(1,
				MiraklItemTypes.SHOP);
		final MiraklItemLinkLocator miraklItemLinkLocator2 = itemLinksMother.aMiraklItemLinkLocator(2,
				MiraklItemTypes.SHOP);
		final MiraklItemLinkLocator miraklItemLinkLocator3 = itemLinksMother.aMiraklItemLinkLocator(3,
				MiraklItemTypes.SHOP);

		final HyperwalletItemLinkLocator hyperwalletItemLinkLocator1 = itemLinksMother.aHyperwalletItemLinkLocator(1,
				HyperwalletItemTypes.PROGRAM);
		final HyperwalletItemLinkLocator hyperwalletItemLinkLocator2 = itemLinksMother.aHyperwalletItemLinkLocator(1,
				HyperwalletItemTypes.BANK_ACCOUNT);
		final HyperwalletItemLinkLocator hyperwalletItemLinkLocator3 = itemLinksMother.aHyperwalletItemLinkLocator(2,
				HyperwalletItemTypes.PROGRAM);

		itemLinksService.createLinks(miraklItemLinkLocator1,
				Set.of(hyperwalletItemLinkLocator1, hyperwalletItemLinkLocator2));
		itemLinksService.createLinks(miraklItemLinkLocator2, Set.of(hyperwalletItemLinkLocator3));
		itemLinksService.createLinks(miraklItemLinkLocator3, Set.of());

		final Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> miraklItemLinkLocatorLinks = itemLinksService
				.findLinks(Set.of(miraklItemLinkLocator1, miraklItemLinkLocator2, miraklItemLinkLocator3),
						Set.of(HyperwalletItemTypes.BANK_ACCOUNT, HyperwalletItemTypes.PROGRAM));

		assertThat(miraklItemLinkLocatorLinks.entrySet()).hasSize(3);
		assertThat(miraklItemLinkLocatorLinks.get(miraklItemLinkLocator1))
				.containsExactlyInAnyOrder(hyperwalletItemLinkLocator1, hyperwalletItemLinkLocator2);
		assertThat(miraklItemLinkLocatorLinks.get(miraklItemLinkLocator2))
				.containsExactlyInAnyOrder(hyperwalletItemLinkLocator3);
		assertThat(miraklItemLinkLocatorLinks.get(miraklItemLinkLocator3)).isEmpty();
	}

	@Test
	void shouldFilterLinksAccordingToTypes() {
		final MiraklItemLinkLocator miraklItemLinkLocator1 = itemLinksMother.aMiraklItemLinkLocator(1,
				MiraklItemTypes.SHOP);
		final MiraklItemLinkLocator miraklItemLinkLocator2 = itemLinksMother.aMiraklItemLinkLocator(2,
				MiraklItemTypes.SHOP);

		final HyperwalletItemLinkLocator hyperwalletItemLinkLocator1 = itemLinksMother.aHyperwalletItemLinkLocator(1,
				HyperwalletItemTypes.PROGRAM);
		final HyperwalletItemLinkLocator hyperwalletItemLinkLocator2 = itemLinksMother.aHyperwalletItemLinkLocator(1,
				HyperwalletItemTypes.BUSINESS_STAKEHOLDER);
		final HyperwalletItemLinkLocator hyperwalletItemLinkLocator3 = itemLinksMother.aHyperwalletItemLinkLocator(2,
				HyperwalletItemTypes.PROGRAM);

		itemLinksService.createLinks(miraklItemLinkLocator1,
				Set.of(hyperwalletItemLinkLocator1, hyperwalletItemLinkLocator2));
		itemLinksService.createLinks(miraklItemLinkLocator2, Set.of(hyperwalletItemLinkLocator3));

		final Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> miraklItemLinkLocatorLinks = itemLinksService
				.findLinks(Set.of(miraklItemLinkLocator1, miraklItemLinkLocator2),
						Set.of(HyperwalletItemTypes.BANK_ACCOUNT, HyperwalletItemTypes.PROGRAM));

		assertThat(miraklItemLinkLocatorLinks.entrySet()).hasSize(2);
		assertThat(miraklItemLinkLocatorLinks.get(miraklItemLinkLocator1))
				.containsExactlyInAnyOrder(hyperwalletItemLinkLocator1);
		assertThat(miraklItemLinkLocatorLinks.get(miraklItemLinkLocator2))
				.containsExactlyInAnyOrder(hyperwalletItemLinkLocator3);
	}

	class ItemLinksMother {

		MiraklItemLinkLocator aMiraklItemLinkLocator(final int id, final MiraklItemTypes type) {
			return new MiraklItemLinkLocator("M-%s-%d".formatted(type.toString(), id), type);
		}

		HyperwalletItemLinkLocator aHyperwalletItemLinkLocator(final int id, final HyperwalletItemTypes type) {
			return new HyperwalletItemLinkLocator("H-%s-%d".formatted(type.toString(), id), type);
		}

	}

}
