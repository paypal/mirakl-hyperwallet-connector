package com.paypal.observability.testsupport.fixtures;

import org.mockserver.client.MockServerClient;

import java.util.Map;

public class HyperwalletHealthMockServerFixtures extends AbstractMockServerFixtures {

	public HyperwalletHealthMockServerFixtures(final MockServerClient mockServerClient) {
		super(mockServerClient);
	}

	@Override
	protected String getFolder() {
		return "hyperwallet/health";
	}

	private void mockGetProgram(final String responseFile, final int statusCode, final String programToken) {
		mockGet("/api/rest/v4/programs/{programToken}", responseFile, statusCode, Map.of("programToken", programToken));
	}

	private void mockGetProgramStatusOK(final String responseFile, final String programToken) {
		mockGetProgram(responseFile, 200, programToken);
	}

	private void mockGetProgramStatusError(final String responseFile, final String programToken) {
		mockGetProgram(responseFile, 500, programToken);
	}

	public void mockGetHealth_up() {
		mockGetProgramStatusOK("health-00.json", "prg-1fb3df0d-787b-4bbd-9eb7-1d9fe8ed6c8e");
	}

	public void mockGetHealth_down() {
		mockGetProgramStatusError("health-error500.json", "prg-1fb3df0d-787b-4bbd-9eb7-1d9fe8ed6c8e");
	}

}
