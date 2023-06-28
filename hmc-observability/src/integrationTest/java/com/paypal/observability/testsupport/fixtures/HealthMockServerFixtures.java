package com.paypal.observability.testsupport.fixtures;

import com.paypal.testsupport.mocks.AbstractMockServerFixtures;
import org.mockserver.client.MockServerClient;

public class HealthMockServerFixtures extends AbstractMockServerFixtures {

	public HealthMockServerFixtures(final MockServerClient mockServerClient) {
		super(mockServerClient);
	}

	@Override
	protected String getFolder() {
		return "mirakl/health";
	}

	private void mockGetVersion(final String responseFile, final int statusCode) {
		mockGet("/api/version", responseFile, statusCode);
	}

	private void mockGetVersionStatusOK(final String responseFile) {
		mockGetVersion(responseFile, 200);
	}

	private void mockGetVersionStatusError(final String responseFile) {
		mockGetVersion(responseFile, 500);
	}

	public void mockGetVersion_up() {
		mockGetVersionStatusOK("health-00.json");
	}

	public void mockGetVersion_down() {
		mockGetVersionStatusError("health-error500.json");
	}

}
