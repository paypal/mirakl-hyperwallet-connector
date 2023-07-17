package com.paypal.observability.trafficauditor;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.model.HyperwalletUsersListPaginationOptions;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.observability.AbstractObservabilityIntegrationTest;
import com.paypal.observability.trafficauditor.loggers.TrafficAuditorLogger;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTarget;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTrace;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

class TrafficAuditorHyperwalletTest extends AbstractObservabilityIntegrationTest {

	@Autowired
	private UserHyperwalletSDKService hyperwalletSDKService;

	@SpyBean
	private TrafficAuditorLogger trafficAuditorLogger;

	@Captor
	private ArgumentCaptor<TrafficAuditorTrace> traceArgumentCaptor;

	@Test
	void httpGetOperation_shouldBeAudited() {
		// given
		final Hyperwallet hyperwallet = hyperwalletSDKService.getHyperwalletInstance();
		mockServerExpectationsLoader.loadExpectationsFromFolder("trafficauditor/expectations", "hyperwallet", Map.of());

		// when
		final HyperwalletUsersListPaginationOptions options = new HyperwalletUsersListPaginationOptions();
		options.setCreatedAfter(new Date());
		options.setCreatedBefore(new Date());
		final HyperwalletList<HyperwalletUser> result = hyperwallet.listUsers(options);

		// then
		assertThat(result).isNotNull();
		verify(trafficAuditorLogger, atLeastOnce()).log(traceArgumentCaptor.capture());
		final List<TrafficAuditorTrace> capturedTraces = traceArgumentCaptor.getAllValues();
		final TrafficAuditorTrace capturedTrace = capturedTraces.get(capturedTraces.size() - 1);
		assertThat(capturedTrace.getTarget()).isEqualTo(TrafficAuditorTarget.HYPERWALLET);
		assertThat(capturedTrace.getRequest().getUrl()).contains("/api/rest/v4/users");
		assertThat(capturedTrace.getRequest().getQueryParameters()).containsKey("createdAfter")
				.containsKey("createdBefore");
		assertThat(capturedTrace.getRequest().getHeaders()).containsEntry("Accept", List.of("application/json"));
		assertThat(capturedTrace.getRequest().getBody()).isNullOrEmpty();
		assertThat(capturedTrace.getRequest().getMethod()).contains("GET");
		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getBody())
				.contains("usr-31a60e4c-dc9d-4061-899b-46575d2508d7");
		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getResponseCode())
				.isEqualTo(200);
		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getHeaders())
				.containsEntry("Content-Type", List.of("application/json"));
	}

	@Test
	void httpPostOperation_shouldBeAudited() {
		// given
		final Hyperwallet hyperwallet = hyperwalletSDKService.getHyperwalletInstance();
		mockServerExpectationsLoader.loadExpectationsFromFolder("trafficauditor/expectations", "hyperwallet", Map.of());
		final HyperwalletUser hyperwalletUser = new HyperwalletUser();
		hyperwalletUser.setClientUserId("mockedClientUserId");

		// when
		final HyperwalletUser result = hyperwallet.createUser(hyperwalletUser);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getClientUserId()).isEqualTo("mockedClientUserId");

		verify(trafficAuditorLogger, atLeastOnce()).log(traceArgumentCaptor.capture());
		final List<TrafficAuditorTrace> capturedTraces = traceArgumentCaptor.getAllValues();
		final TrafficAuditorTrace capturedTrace = capturedTraces.get(capturedTraces.size() - 1);
		assertThat(capturedTrace.getTarget()).isEqualTo(TrafficAuditorTarget.HYPERWALLET);
		assertThat(capturedTrace.getRequest().getUrl()).contains("/api/rest/v4/users");
		assertThat(capturedTrace.getRequest().getBody()).contains("mockedClientUserId");
		assertThat(capturedTrace.getRequest().getHeaders()).containsKey("Content-Type");
		assertThat(capturedTrace.getRequest().getMethod()).contains("POST");
		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getBody())
				.contains("mockedToken");
		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getResponseCode())
				.isEqualTo(200);
		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getHeaders())
				.containsEntry("Content-Type", List.of("application/json"));
	}

	@Test
	void httpPutOperation_shouldBeAudited() {
		// given
		final Hyperwallet hyperwallet = hyperwalletSDKService.getHyperwalletInstance();
		mockServerExpectationsLoader.loadExpectationsFromFolder("trafficauditor/expectations", "hyperwallet", Map.of());
		final HyperwalletUser hyperwalletUser = new HyperwalletUser();
		hyperwalletUser.setClientUserId("mockedClientUserId");
		hyperwalletUser.setToken("mockedToken");

		// when
		final HyperwalletUser result = hyperwallet.updateUser(hyperwalletUser);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getClientUserId()).isEqualTo("mockedClientUserId");

		verify(trafficAuditorLogger, atLeastOnce()).log(traceArgumentCaptor.capture());
		final List<TrafficAuditorTrace> capturedTraces = traceArgumentCaptor.getAllValues();
		final TrafficAuditorTrace capturedTrace = capturedTraces.get(capturedTraces.size() - 1);
		assertThat(capturedTrace.getTarget()).isEqualTo(TrafficAuditorTarget.HYPERWALLET);
		assertThat(capturedTrace.getRequest().getUrl()).contains("/api/rest/v4/users/mockedToken");
		assertThat(capturedTrace.getRequest().getBody()).contains("mockedClientUserId");
		assertThat(capturedTrace.getRequest().getHeaders()).containsKey("Content-Type");
		assertThat(capturedTrace.getRequest().getMethod()).contains("PUT");
		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getBody())
				.contains("mockedToken");
		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getResponseCode())
				.isEqualTo(200);
		assertThat(capturedTrace.getResponse().orElseThrow(IllegalStateException::new).getHeaders())
				.containsEntry("Content-Type", List.of("application/json"));
	}

}
