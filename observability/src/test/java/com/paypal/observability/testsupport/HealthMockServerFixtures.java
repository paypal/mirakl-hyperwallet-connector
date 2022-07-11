package com.paypal.observability.testsupport;

import org.mockserver.client.MockServerClient;

public class HealthMockServerFixtures extends AbstractMockServerFixtures {

	public HealthMockServerFixtures(MockServerClient mockServerClient) {
		super(mockServerClient);
	}

	@Override
	protected String getFolder() {
		return "health";
	}

	private void mockGetVersion(String responseFile, int statusCode) {
		mockGet("/api/version", responseFile, statusCode);
	}

	private void mockGetVersionStatusOK(String responseFile) {
		mockGetVersion(responseFile, 200);
	}

	private void mockGetVersionStatusError(String responseFile) {
		mockGetVersion(responseFile, 500);
	}

	public void mockGetVersion_up() {
		mockGetVersionStatusOK("health-00.json");
	}

	public void mockGetVersion_down() {
		mockGetVersionStatusError("health-error500.json");
	}

}
