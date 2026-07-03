package com.paypal.infrastructure.mirakl.client;

import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklPayOutState;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycle;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycles;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycleSeekSort;
import com.mirakl.client.mmp.operator.request.payment.sellerbillingcycle.MiraklGetSellerBillingCyclesRequest;
import com.paypal.testsupport.AbstractMockEnabledIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MiraklSellerBillingCyclesITTest extends AbstractMockEnabledIntegrationTest {

	private static final String NEXT_PAGE_TOKEN_XYZ = "next-page-token-xyz";

	private static final String SBC_11_MULTIPLE_RESPONSE_JSON = "sbc11-multiple-response.json";

	private static final String SBC_11_PAGE_2_RESPONSE_JSON = "sbc11-page2-response.json";

	private static final String SBC_11_PAGE_1_RESPONSE_JSON = "sbc11-page1-response.json";

	@Autowired
	private MiraklClient miraklClient;

	private static final UUID EXPECTED_ID_1 = UUID.fromString("a5b77ee5-f5c2-4efc-ab94-8366ee781598");

	private static final UUID EXPECTED_ID_2 = UUID.fromString("98665be1-ce69-4ab6-8d93-b8dfd3e67404");

	private static final UUID EXPECTED_ID_3 = UUID.fromString("81a36c97-6cc1-44ec-93e7-f55e06e6a6c6");

	@Test
	void shouldReturnSellerBillingCycles_whenCallingGetSellerBillingCycles() {
		miraklSellerBillingCyclesEndpointMock.getSellerBillingCycles(SBC_11_MULTIPLE_RESPONSE_JSON);

		final MiraklGetSellerBillingCyclesRequest request = new MiraklGetSellerBillingCyclesRequest();
		final MiraklSellerBillingCycles result = miraklClient.getSellerBillingCycles(request);

		assertThat(result).isNotNull();
		assertThat(result.getData()).hasSize(3);

		final MiraklSellerBillingCycle cycle1 = result.getData().getFirst();
		assertThat(cycle1.getId()).isEqualTo(EXPECTED_ID_1);
		assertThat(cycle1.getCurrencyIsoCode().name()).isEqualTo("EUR");
		assertThat(cycle1.getAmountTransferredToSeller().doubleValue()).isEqualTo(4545.2);
		assertThat(cycle1.getAmountTransferredToOperator().doubleValue()).isEqualTo(1046.59);
		assertThat(cycle1.getShop().getShopId()).isEqualTo(12L);
		assertThat(cycle1.getShop().getShopName()).isEqualTo("The Shop");
		assertThat(cycle1.getShop().getShopCorporateName()).isEqualTo("Ceasar Shop");
		assertThat(cycle1.getShop().getShopOperatorInternalId()).isEqualTo("op1234");
		assertThat(cycle1.getPayOut().getState()).isEqualTo(MiraklPayOutState.PAID);
		assertThat(cycle1.getPayOut().getReference()).isEqualTo("bank-ref");
		assertThat(cycle1.getSummary().getTotalSubscriptionIT().doubleValue()).isEqualTo(-67.37);
		assertThat(cycle1.getSummary().getTotalCommissionsIT().doubleValue()).isEqualTo(-889.24);

		final MiraklSellerBillingCycle cycle2 = result.getData().get(1);
		assertThat(cycle2.getId()).isEqualTo(EXPECTED_ID_2);
		assertThat(cycle2.getCurrencyIsoCode().name()).isEqualTo("NGN");
		assertThat(cycle2.getPayOut().getState()).isEqualTo(MiraklPayOutState.TO_PAY);
		assertThat(cycle2.getShop().getShopId()).isEqualTo(3432L);

		final MiraklSellerBillingCycle cycle3 = result.getData().get(2);
		assertThat(cycle3.getId()).isEqualTo(EXPECTED_ID_3);
		assertThat(cycle3.getAmountTransferredToSeller().doubleValue()).isEqualTo(0.0);
		assertThat(cycle3.getShop().getShopId()).isEqualTo(2L);
		assertThat(cycle3.getShop().getShopName()).isEqualTo("SuperShop");
	}

	@Test
	void shouldFilterByPayOutState() {
		miraklSellerBillingCyclesEndpointMock.getSellerBillingCyclesWithPayOutState(MiraklPayOutState.TO_PAY.toString(),
				SBC_11_MULTIPLE_RESPONSE_JSON);

		final MiraklGetSellerBillingCyclesRequest request = new MiraklGetSellerBillingCyclesRequest();
		request.setPayOutStates(Collections.singleton(MiraklPayOutState.TO_PAY));
		final MiraklSellerBillingCycles result = miraklClient.getSellerBillingCycles(request);

		assertThat(result).isNotNull();
		assertThat(result.getData()).isNotEmpty();
	}

	@Test
	void shouldReturnSellerBillingCyclesWithStartDateAndPayOutState() {
		final Instant startDate = Instant.parse("2014-01-01T00:00:00Z");
		miraklSellerBillingCyclesEndpointMock.getSellerBillingCyclesWithStartDate("2014-01-01T00:00:00Z",
				MiraklPayOutState.TO_PAY.toString(), SBC_11_MULTIPLE_RESPONSE_JSON);

		final MiraklGetSellerBillingCyclesRequest request = new MiraklGetSellerBillingCyclesRequest();
		request.setStartDate(startDate);
		request.setPayOutStates(Collections.singleton(MiraklPayOutState.TO_PAY));
		request.setLimit(10);
		request.setOrderBy(MiraklSellerBillingCycleSeekSort.DATE_CREATED.asc());
		final MiraklSellerBillingCycles result = miraklClient.getSellerBillingCycles(request);

		assertThat(result).isNotNull();
		assertThat(result.getData()).isNotEmpty();
		assertThat(request.getStartDate()).isEqualTo(startDate);
		assertThat(request.getPayOutStates()).containsExactly(MiraklPayOutState.TO_PAY);
		assertThat(request.getLimit()).isEqualTo(10);
	}

	@Test
	void shouldHandlePagination_whenNextPageTokenIsPresent() {
		miraklSellerBillingCyclesEndpointMock.getSellerBillingCycles(SBC_11_PAGE_1_RESPONSE_JSON);

		final MiraklGetSellerBillingCyclesRequest request = new MiraklGetSellerBillingCyclesRequest();
		request.setPageToken(null);
		final MiraklSellerBillingCycles page1 = miraklClient.getSellerBillingCycles(request);
		assertThat(page1.getData()).hasSize(1);
		assertThat(page1.getNextPageToken()).isEqualTo(NEXT_PAGE_TOKEN_XYZ);

		mockServerClient.reset();

		miraklSellerBillingCyclesEndpointMock.getSellerBillingCyclesWithPageToken(NEXT_PAGE_TOKEN_XYZ,
				SBC_11_PAGE_2_RESPONSE_JSON);

		request.setPageToken(NEXT_PAGE_TOKEN_XYZ);
		final MiraklSellerBillingCycles page2 = miraklClient.getSellerBillingCycles(request);
		assertThat(page2.getData()).hasSize(1);
		assertThat(page2.getNextPageToken()).isNull();
	}

}
