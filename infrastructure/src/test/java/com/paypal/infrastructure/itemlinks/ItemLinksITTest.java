package com.paypal.infrastructure.itemlinks;

import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes;
import com.paypal.infrastructure.itemlinks.model.MiraklItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.MiraklItemTypes;
import com.paypal.infrastructure.itemlinks.services.ItemLinksService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("IntegrationTest")
@SpringBootTest(classes = ItemLinksITTestContext.class)
@TestPropertySource(
		locations = { "classpath:infrastructure-test.properties", "classpath:infrastructure-test-db.properties" })
@ExtendWith(SpringExtension.class)
@Transactional
class ItemLinksITTest {

	@Autowired
	private ItemLinksService itemLinksService;

	private ItemLinksMother itemLinksMother = new ItemLinksMother();

	@Test
	void shouldCreateAndRecoverLinks() {
		MiraklItemLinkLocator miraklItemLinkLocator1 = itemLinksMother.aMiraklItemLinkLocator(1, MiraklItemTypes.SHOP);
		MiraklItemLinkLocator miraklItemLinkLocator2 = itemLinksMother.aMiraklItemLinkLocator(2, MiraklItemTypes.SHOP);
		MiraklItemLinkLocator miraklItemLinkLocator3 = itemLinksMother.aMiraklItemLinkLocator(3, MiraklItemTypes.SHOP);

		HyperwalletItemLinkLocator hyperwalletItemLinkLocator1 = itemLinksMother.aHyperwalletItemLinkLocator(1,
				HyperwalletItemTypes.PROGRAM);
		HyperwalletItemLinkLocator hyperwalletItemLinkLocator2 = itemLinksMother.aHyperwalletItemLinkLocator(1,
				HyperwalletItemTypes.BANK_ACCOUNT);
		HyperwalletItemLinkLocator hyperwalletItemLinkLocator3 = itemLinksMother.aHyperwalletItemLinkLocator(2,
				HyperwalletItemTypes.PROGRAM);

		itemLinksService.createLinks(miraklItemLinkLocator1,
				Set.of(hyperwalletItemLinkLocator1, hyperwalletItemLinkLocator2));
		itemLinksService.createLinks(miraklItemLinkLocator2, Set.of(hyperwalletItemLinkLocator3));
		itemLinksService.createLinks(miraklItemLinkLocator3, Set.of());

		Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> miraklItemLinkLocatorLinks = itemLinksService
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
		MiraklItemLinkLocator miraklItemLinkLocator1 = itemLinksMother.aMiraklItemLinkLocator(1, MiraklItemTypes.SHOP);
		MiraklItemLinkLocator miraklItemLinkLocator2 = itemLinksMother.aMiraklItemLinkLocator(2, MiraklItemTypes.SHOP);

		HyperwalletItemLinkLocator hyperwalletItemLinkLocator1 = itemLinksMother.aHyperwalletItemLinkLocator(1,
				HyperwalletItemTypes.PROGRAM);
		HyperwalletItemLinkLocator hyperwalletItemLinkLocator2 = itemLinksMother.aHyperwalletItemLinkLocator(1,
				HyperwalletItemTypes.BUSINESS_STAKEHOLDER);
		HyperwalletItemLinkLocator hyperwalletItemLinkLocator3 = itemLinksMother.aHyperwalletItemLinkLocator(2,
				HyperwalletItemTypes.PROGRAM);

		itemLinksService.createLinks(miraklItemLinkLocator1,
				Set.of(hyperwalletItemLinkLocator1, hyperwalletItemLinkLocator2));
		itemLinksService.createLinks(miraklItemLinkLocator2, Set.of(hyperwalletItemLinkLocator3));

		Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> miraklItemLinkLocatorLinks = itemLinksService
				.findLinks(Set.of(miraklItemLinkLocator1, miraklItemLinkLocator2),
						Set.of(HyperwalletItemTypes.BANK_ACCOUNT, HyperwalletItemTypes.PROGRAM));

		assertThat(miraklItemLinkLocatorLinks.entrySet()).hasSize(2);
		assertThat(miraklItemLinkLocatorLinks.get(miraklItemLinkLocator1))
				.containsExactlyInAnyOrder(hyperwalletItemLinkLocator1);
		assertThat(miraklItemLinkLocatorLinks.get(miraklItemLinkLocator2))
				.containsExactlyInAnyOrder(hyperwalletItemLinkLocator3);
	}

	class ItemLinksMother {

		MiraklItemLinkLocator aMiraklItemLinkLocator(int id, MiraklItemTypes type) {
			return new MiraklItemLinkLocator(String.format("M-%s-%d", type.toString(), id), type);
		}

		HyperwalletItemLinkLocator aHyperwalletItemLinkLocator(int id, HyperwalletItemTypes type) {
			return new HyperwalletItemLinkLocator(String.format("H-%s-%d", type.toString(), id), type);
		}

	}

}
