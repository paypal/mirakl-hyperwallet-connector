package com.paypal.invoices;

import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes;
import com.paypal.infrastructure.itemlinks.model.MiraklItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.MiraklItemTypes;
import com.paypal.infrastructure.itemlinks.services.ItemLinksService;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.testsupport.AbstractMockEnabledIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class MiraklClientItemLinksUpdateTest extends AbstractMockEnabledIntegrationTest {

	@Autowired
	private MiraklClient miraklClient;

	@Autowired
	private ItemLinksService itemLinksService;

	@Test
	void itemLinks_shouldBeUpdated_afterReceivingShops_whenNoPreviousDataExists() {
		// given
		mockServerExpectationsLoader.loadExpectationsFromFolder("expectations/miraklclientitemlinksupdate", "mirakl",
				Map.of());

		// when
		final MiraklGetShopsRequest request = new MiraklGetShopsRequest();
		request.setUpdatedSince(new Date());
		miraklClient.getShops(request);

		// then
		final Set<HyperwalletItemTypes> types = Set.of(HyperwalletItemTypes.BANK_ACCOUNT, HyperwalletItemTypes.PROGRAM);
		MiraklItemLinkLocator miraklItemLinkLocator = new MiraklItemLinkLocator("10001", MiraklItemTypes.SHOP);
		Collection<HyperwalletItemLinkLocator> links = itemLinksService.findLinks(miraklItemLinkLocator, types);
		assertThat(links).hasSize(2).containsExactlyInAnyOrder(
				new HyperwalletItemLinkLocator("trm-00000000-0000-0000-0000-0000000000",
						HyperwalletItemTypes.BANK_ACCOUNT),
				new HyperwalletItemLinkLocator("PROGRAM-1", HyperwalletItemTypes.PROGRAM));

		miraklItemLinkLocator = new MiraklItemLinkLocator("10002", MiraklItemTypes.SHOP);
		links = itemLinksService.findLinks(miraklItemLinkLocator, types);
		assertThat(links).hasSize(1)
				.containsExactlyInAnyOrder(new HyperwalletItemLinkLocator("PROGRAM-2", HyperwalletItemTypes.PROGRAM));
	}

	@Test
	void itemLinks_shouldBeUpdated_afterReceivingShops_whenPreviousDataExists() {
		// given
		mockServerExpectationsLoader.loadExpectationsFromFolder("expectations/miraklclientitemlinksupdate", "mirakl",
				Map.of());

		// when
		final MiraklGetShopsRequest request = new MiraklGetShopsRequest();
		request.setUpdatedSince(new Date());
		miraklClient.getShops(request);
		miraklClient.getShops(request);

		// then
		final Set<HyperwalletItemTypes> types = Set.of(HyperwalletItemTypes.BANK_ACCOUNT, HyperwalletItemTypes.PROGRAM);
		MiraklItemLinkLocator miraklItemLinkLocator = new MiraklItemLinkLocator("10001", MiraklItemTypes.SHOP);
		Collection<HyperwalletItemLinkLocator> links = itemLinksService.findLinks(miraklItemLinkLocator, types);
		assertThat(links).hasSize(2).containsExactlyInAnyOrder(
				new HyperwalletItemLinkLocator("trm-00000000-0000-0000-0000-0000000001",
						HyperwalletItemTypes.BANK_ACCOUNT),
				new HyperwalletItemLinkLocator("PROGRAM-2", HyperwalletItemTypes.PROGRAM));

		miraklItemLinkLocator = new MiraklItemLinkLocator("10002", MiraklItemTypes.SHOP);
		links = itemLinksService.findLinks(miraklItemLinkLocator, types);
		assertThat(links).hasSize(2).containsExactlyInAnyOrder(
				new HyperwalletItemLinkLocator("trm-00000000-0000-0000-0000-0000000002",
						HyperwalletItemTypes.BANK_ACCOUNT),
				new HyperwalletItemLinkLocator("PROGRAM-1", HyperwalletItemTypes.PROGRAM));
	}

}
