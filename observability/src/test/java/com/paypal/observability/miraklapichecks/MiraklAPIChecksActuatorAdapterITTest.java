package com.paypal.observability.miraklapichecks;

import com.paypal.observability.testsupport.AbstractMockServerITTest;
import com.paypal.observability.testsupport.ObservabilityWebIntegrationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ObservabilityWebIntegrationContext
class MiraklAPIChecksActuatorAdapterITTest extends AbstractMockServerITTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void miraklAPICheckShouldReturnHealthUp() throws Exception {
		hyperwalletHealthMockServerFixtures.mockGetHealth_up();
		healthMockServerFixtures.mockGetVersion_up();

		//@formatter:off
		mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.components.miraklAPIHealthCheck.status").value("UP"))
				.andExpect(jsonPath("$.components.miraklAPIHealthCheck.details.version").value("3.210"))
				.andExpect(jsonPath("$.components.miraklAPIHealthCheck.details.location").value("http://localhost"));
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
				.andExpect(jsonPath("$.components.miraklAPIHealthCheck.details.error").value("[500] Internal Server Error"))
				.andExpect(jsonPath("$.components.miraklAPIHealthCheck.details.location").value("http://localhost"));
		//@formatter:on
	}

}
