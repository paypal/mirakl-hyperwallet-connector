package com.paypal.invoices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.invoices.extractioninvoices.configuration.InvoicesOperatorCommissionsConfig;
import com.paypal.invoices.management.controllers.dto.CommissionsConfigurationDto;
import com.paypal.testsupport.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
class InvoicesConfigurationManagementITTest extends AbstractIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig;

	private static CommissionsConfigurationDto getCommissionsConfigurationDto(final ResultActions resultActions)
			throws UnsupportedEncodingException, JsonProcessingException {
		final MvcResult result = resultActions.andReturn();
		final String contentAsString = result.getResponse().getContentAsString();

		final ObjectMapper objectMapper = new ObjectMapper();
		final CommissionsConfigurationDto response = objectMapper.readValue(contentAsString,
				CommissionsConfigurationDto.class);
		return response;
	}

	private static String serializeCommissionsConfigurationDto(
			final CommissionsConfigurationDto commissionsConfigurationDto) throws Exception {
		final ObjectMapper objectMapper = new ObjectMapper();
		final StringWriter stringWriter = new StringWriter();
		objectMapper.writeValue(stringWriter, commissionsConfigurationDto);

		return stringWriter.toString();
	}

	@Test
	void shouldGetOperatorCommissionsEnabledSetting_whenIsTrue() throws Exception {
		invoicesOperatorCommissionsConfig.setEnabled(true);

		final CommissionsConfigurationDto response = doGetOperatorCommissionsEnabled();

		assertThat(response.isOperatorCommissionsEnabled()).isTrue();
	}

	@Test
	void shouldGetOperatorCommissionsEnabledSetting_whenIsFalse() throws Exception {
		invoicesOperatorCommissionsConfig.setEnabled(false);

		final CommissionsConfigurationDto response = doGetOperatorCommissionsEnabled();

		assertThat(response.isOperatorCommissionsEnabled()).isFalse();
	}

	@Test
	void shouldSetOperatorCommissionsEnabledSetting() throws Exception {
		invoicesOperatorCommissionsConfig.setEnabled(false);

		final CommissionsConfigurationDto requestBody = new CommissionsConfigurationDto(true);

		this.mockMvc
				.perform(put("/configuration/invoices/commissions").contentType(MediaType.APPLICATION_JSON)
						.content(serializeCommissionsConfigurationDto(requestBody)))
				.andDo(print()).andExpect(status().isOk());

		assertThat(invoicesOperatorCommissionsConfig.isEnabled()).isTrue();
	}

	private CommissionsConfigurationDto doGetOperatorCommissionsEnabled() throws Exception {
		final ResultActions resultActions = this.mockMvc.perform(get("/configuration/invoices/commissions"))
				.andDo(print()).andExpect(status().isOk());

		final CommissionsConfigurationDto response = getCommissionsConfigurationDto(resultActions);
		return response;
	}

}
