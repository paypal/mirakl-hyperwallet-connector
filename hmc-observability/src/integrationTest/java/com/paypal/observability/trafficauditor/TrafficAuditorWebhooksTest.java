package com.paypal.observability.trafficauditor;

import com.paypal.observability.AbstractObservabilityIntegrationTest;
import com.paypal.observability.trafficauditor.interceptors.webhooks.WebhookLoggingRequestFilter;
import com.paypal.observability.trafficauditor.loggers.TrafficAuditorLogger;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTarget;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTrace;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
class TrafficAuditorWebhooksTest extends AbstractObservabilityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private DefaultMockMvcBuilder mockMvcBuilder;

	@Autowired
	private WebhookLoggingRequestFilter webhookLoggingRequestFilter;

	@SpyBean
	private TrafficAuditorLogger trafficAuditorLogger;

	@Captor
	private ArgumentCaptor<TrafficAuditorTrace> traceArgumentCaptor;

	@Test
	void webhooks_shouldBeAudited() throws Exception {
		mockMvc = mockMvcBuilder.addFilter(webhookLoggingRequestFilter).build();
		mockMvc.perform(
				post("/webhooks/notifications").contentType(MediaType.APPLICATION_JSON).content(getWebhookBody()))
				.andDo(print()).andExpect(status().isOk());

		verify(trafficAuditorLogger, atLeastOnce()).log(traceArgumentCaptor.capture());
		final List<TrafficAuditorTrace> capturedTraces = traceArgumentCaptor.getAllValues();
		final TrafficAuditorTrace capturedTrace = capturedTraces.get(capturedTraces.size() - 1);

		assertThat(capturedTrace.getTarget()).isEqualTo(TrafficAuditorTarget.HMC);

		assertThat(capturedTrace.getRequest().getUrl()).contains("/webhooks/notifications");
		assertThat(capturedTrace.getRequest().getBody()).contains("clientUserId");
		assertThat(capturedTrace.getRequest().getHeaders()).containsKey("Content-Type");
		assertThat(capturedTrace.getRequest().getHeaders().get("Content-Type").get(0)).contains("application/json");
		assertThat(capturedTrace.getRequest().getMethod()).contains("POST");
		assertThat(capturedTrace.getRequest().getQueryParameters()).isEmpty();

		assertThat(capturedTrace.getResponse()).isEmpty();
	}

	@Test
	void webhooks_ignoredUrls_shouldNotBeAudited() throws Exception {
		mockMvc = mockMvcBuilder.addFilter(webhookLoggingRequestFilter).build();
		mockMvc.perform(post("/unknown").contentType(MediaType.APPLICATION_JSON).content(getWebhookBody()))
				.andDo(print());

		verify(trafficAuditorLogger, never())
				.log(argThat(argument -> argument.getRequest().getUrl().contains("/unknown")));
	}

	private String getWebhookBody() throws IOException {
		final File file = ResourceUtils.getFile("classpath:" + "trafficauditor/webhooks/webhook-00.json");
		return new String(Files.readAllBytes(file.toPath()));
	}

}
