package com.paypal.observability.miraklapichecks;

import com.paypal.infrastructure.sdk.mirakl.impl.MiraklApiClientConfig;
import com.paypal.observability.ObservabilityIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MiraklAPIChecksActuatorAdapterITTest extends ObservabilityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MiraklApiClientConfig miraklApiClientConfig;

	@Test
	void miraklAPICheckShouldReturnHealthUp() throws Exception {
		this.hyperwalletHealthMockServerFixtures.mockGetHealth_up();
		this.healthMockServerFixtures.mockGetVersion_up();

		//@formatter:off
		final ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health"));
		perform
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.components.miraklAPIHealthCheck.status").value("UP"))
			.andExpect(jsonPath("$.components.miraklAPIHealthCheck.details.version").value("3.210"))
			.andExpect(jsonPath("$.components.miraklAPIHealthCheck.details.location")
				.value(this.miraklApiClientConfig.getEnvironment()));
		//@formatter:on
	}

	@Test
	void miraklAPICheckShouldReturnHealthDown() throws Exception {
		hyperwalletHealthMockServerFixtures.mockGetHealth_up();
		healthMockServerFixtures.mockGetVersion_down();

		//@formatter:off
		mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health"))
			.andExpect(status().is5xxServerError())
			.andExpect(jsonPath("$.components.miraklAPIHealthCheck.status").value("DOWN"))
			.andExpect(jsonPath("$.components.miraklAPIHealthCheck.details.error")
				.value("[500] Internal Server Error"))
			.andExpect(jsonPath("$.components.miraklAPIHealthCheck.details.location")
				.value(miraklApiClientConfig.getEnvironment()));
		//@formatter:on
	}

}
