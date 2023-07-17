package com.paypal.observability.trafficauditor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.observability.trafficauditor.configuration.TrafficAuditorConfiguration;
import com.paypal.observability.trafficauditor.controllers.dtos.TrafficAuditorConfigurationDto;
import com.paypal.testsupport.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
class TrafficAuditorManagementControllerTest extends AbstractIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TrafficAuditorConfiguration trafficAuditorConfiguration;

	private boolean trafficAuditorEnabled;

	@BeforeEach
	void storeTrafficAuditorEnabled() {
		trafficAuditorEnabled = trafficAuditorConfiguration.isTrafficAuditorEnabled();
	}

	@AfterEach
	void restoreTrafficAuditorEnabled() {
		trafficAuditorConfiguration.setTrafficAuditorEnabled(trafficAuditorEnabled);
	}

	@Test
	void shouldGetTrafficAuditorConfiguration_whenIsTrue() throws Exception {
		trafficAuditorConfiguration.setTrafficAuditorEnabled(true);

		final TrafficAuditorConfigurationDto response = doGetTrafficAuditorConfiguration();

		assertThat(response.isTrafficAuditorEnabled()).isTrue();
	}

	@Test
	void shouldGetTrafficAuditorConfiguration_whenIsFalse() throws Exception {
		trafficAuditorConfiguration.setTrafficAuditorEnabled(false);

		final TrafficAuditorConfigurationDto response = doGetTrafficAuditorConfiguration();

		assertThat(response.isTrafficAuditorEnabled()).isFalse();
	}

	@Test
	void shouldSetTrafficAuditorConfiguration_whenIsFalse() throws Exception {
		trafficAuditorConfiguration.setTrafficAuditorEnabled(false);

		final TrafficAuditorConfigurationDto requestBody = new TrafficAuditorConfigurationDto(true);

		this.mockMvc
				.perform(put("/management/traffic-auditor/configuration").contentType(MediaType.APPLICATION_JSON)
						.content(serializeCommissionsConfigurationDto(requestBody)))
				.andDo(print()).andExpect(status().isOk());

		assertThat(trafficAuditorConfiguration.isTrafficAuditorEnabled()).isTrue();
	}

	private TrafficAuditorConfigurationDto doGetTrafficAuditorConfiguration() throws Exception {
		final ResultActions resultActions = this.mockMvc.perform(get("/management/traffic-auditor/configuration"))
				.andDo(print()).andExpect(status().isOk());

		final TrafficAuditorConfigurationDto response = getCommissionsConfigurationDto(resultActions);

		return response;
	}

	private TrafficAuditorConfigurationDto getCommissionsConfigurationDto(final ResultActions resultActions)
			throws Exception {
		final MvcResult result = resultActions.andReturn();
		final String contentAsString = result.getResponse().getContentAsString();

		final ObjectMapper objectMapper = new ObjectMapper();
		final TrafficAuditorConfigurationDto response = objectMapper.readValue(contentAsString,
				TrafficAuditorConfigurationDto.class);

		return response;
	}

	private static String serializeCommissionsConfigurationDto(
			final TrafficAuditorConfigurationDto trafficAuditorConfigurationDto) throws Exception {
		final ObjectMapper objectMapper = new ObjectMapper();
		final StringWriter stringWriter = new StringWriter();
		objectMapper.writeValue(stringWriter, trafficAuditorConfigurationDto);

		return stringWriter.toString();
	}

}
