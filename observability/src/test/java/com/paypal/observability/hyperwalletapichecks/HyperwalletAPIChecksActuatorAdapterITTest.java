package com.paypal.observability.hyperwalletapichecks;

import com.paypal.observability.testsupport.AbstractMockServerITTest;
import com.paypal.observability.testsupport.ObservabilityWebIntegrationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ObservabilityWebIntegrationContext
class HyperwalletAPIChecksActuatorAdapterITTest extends AbstractMockServerITTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void hyperwalletAPICheckShouldReturnHealthUp() throws Exception {
		healthMockServerFixtures.mockGetVersion_up();
		hyperwalletHealthMockServerFixtures.mockGetHealth_up();

		//@formatter:off
		mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.components.hyperwalletAPIHealthCheck.status").value("UP"))
				.andExpect(jsonPath("$.components.hyperwalletAPIHealthCheck.details.location").value("http://localhost"));
		//@formatter:on
	}

	@Test
	void hyperwalletAPICheckShouldReturnHealthDown() throws Exception {
		healthMockServerFixtures.mockGetVersion_up();
		hyperwalletHealthMockServerFixtures.mockGetHealth_down();

		//@formatter:off
		mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health"))
				.andExpect(status().is5xxServerError())
				.andExpect(jsonPath("$.components.hyperwalletAPIHealthCheck.status").value("DOWN"))
				.andExpect(jsonPath("$.components.hyperwalletAPIHealthCheck.details.error").value("Error message"))
				.andExpect(jsonPath("$.components.hyperwalletAPIHealthCheck.details.location").value("http://localhost"));
		//@formatter:on
	}

}
