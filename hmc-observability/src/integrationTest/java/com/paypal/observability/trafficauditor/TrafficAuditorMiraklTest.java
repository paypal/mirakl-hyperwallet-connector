package com.paypal.observability.trafficauditor;

import com.mirakl.client.mmp.domain.shop.MiraklShopKyc;
import com.mirakl.client.mmp.domain.shop.MiraklShopKycStatus;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.mirakl.client.DirectMiraklClient;
import com.paypal.observability.AbstractObservabilityIntegrationTest;
import com.paypal.observability.trafficauditor.loggers.TrafficAuditorLogger;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTarget;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTrace;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

class TrafficAuditorMiraklTest extends AbstractObservabilityIntegrationTest {

	@Autowired
	private DirectMiraklClient miraklClient;

	@SpyBean
	private TrafficAuditorLogger trafficAuditorLogger;

	@Captor
	private ArgumentCaptor<TrafficAuditorTrace> traceArgumentCaptor;

	@Test
	void httpMiraklGetOperation_shouldBeAudited() {
		// given
		mockServerExpectationsLoader.loadExpectationsFromFolder("trafficauditor/expectations", "mirakl", Map.of());

		// when
		final MiraklGetShopsRequest miraklGetShopsRequest = new MiraklGetShopsRequest();
		miraklGetShopsRequest.setShopIds(List.of("10000"));
		final MiraklShops result = miraklClient.getShops(miraklGetShopsRequest);

		// then
		assertThat(result.getShops()).isNotEmpty();

		assertThat(result).isNotNull();
		verify(trafficAuditorLogger, atLeastOnce()).log(traceArgumentCaptor.capture());
		final List<TrafficAuditorTrace> capturedTraces = traceArgumentCaptor.getAllValues();
		final TrafficAuditorTrace capturedTrace = capturedTraces.get(capturedTraces.size() - 1);
		assertThat(capturedTrace.getTarget()).isEqualTo(TrafficAuditorTarget.MIRAKL);

		assertThat(capturedTrace.getRequest().getUrl()).contains("/api/shops");
		assertThat(capturedTrace.getRequest().getQueryParameters()).containsEntry("shop_ids", "10000");
		assertThat(capturedTrace.getRequest().getHeaders()).containsEntry("Accept",
				List.of("application/json; charset=UTF-8"));
		assertThat(capturedTrace.getRequest().getBody()).isNullOrEmpty();
		assertThat(capturedTrace.getRequest().getMethod()).contains("GET");

		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getBody())
				.contains("hw-user-token");
		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getResponseCode())
				.isEqualTo(200);
		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getHeaders())
				.containsEntry("Content-Type", List.of("application/json"));
	}

	@Test
	void httpMiraklPutOperation_shouldBeAudited() {
		// given
		mockServerExpectationsLoader.loadExpectationsFromFolder("trafficauditor/expectations", "mirakl", Map.of());

		// when
		final MiraklUpdateShop miraklUpdateShop = new MiraklUpdateShop();
		miraklUpdateShop.setShopId(10000L);
		miraklUpdateShop.setKyc(new MiraklShopKyc(MiraklShopKycStatus.APPROVED, ""));
		final MiraklUpdateShopsRequest miraklUpdatedShops = new MiraklUpdateShopsRequest(List.of(miraklUpdateShop));
		final MiraklUpdatedShops result = miraklClient.updateShops(miraklUpdatedShops);

		// then
		assertThat(result.getShopReturns()).isNotEmpty();

		assertThat(result).isNotNull();
		verify(trafficAuditorLogger, atLeastOnce()).log(traceArgumentCaptor.capture());
		final List<TrafficAuditorTrace> capturedTraces = traceArgumentCaptor.getAllValues();
		final TrafficAuditorTrace capturedTrace = capturedTraces.get(capturedTraces.size() - 1);
		assertThat(capturedTrace.getTarget()).isEqualTo(TrafficAuditorTarget.MIRAKL);

		assertThat(capturedTrace.getRequest().getUrl()).contains("/api/shops");
		assertThat(capturedTrace.getRequest().getBody()).contains("shop_id");
		assertThat(capturedTrace.getRequest().getHeaders()).containsEntry("Accept",
				List.of("application/json; charset=UTF-8"));
		assertThat(capturedTrace.getRequest().getMethod()).contains("PUT");
		assertThat(capturedTrace.getRequest().getQueryParameters()).containsEntry("sdk-module", "mmp-sdk-operator")
				.containsKey("sdk-version");

		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getBody()).contains("shop_id")
				.contains("10000");
		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getResponseCode())
				.isEqualTo(200);
		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getHeaders())
				.containsEntry("Content-Type", List.of("application/json"));
	}

}
