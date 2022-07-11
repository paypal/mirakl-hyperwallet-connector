package com.paypal.observability.testsupport;

import org.mockserver.client.MockServerClient;

public class DocsMockServerFixtures extends AbstractMockServerFixtures {

	public DocsMockServerFixtures(MockServerClient mockServerClient) {
		super(mockServerClient);
	}

	@Override
	protected String getFolder() {
		return "docs";
	}

	private void mockGetDocsConfiguration(String responseFile, int statusCode) {
		mockGet("/api/documents", responseFile, statusCode);
	}

	private void mockGetDocsConfigurationStatusOK(String responseFile) {
		mockGetDocsConfiguration(responseFile, 200);
	}

	public void mockGetDocsConfiguration_emptyResponse() {
		mockGetDocsConfigurationStatusOK("docs-00.json");
	}

	public void mockGetDocsConfiguration_correctSchemaResponse() {
		mockGetDocsConfigurationStatusOK("docs-01.json");
	}

	public void mockGetDocsConfiguration_additionalDoc() {
		mockGetDocsConfigurationStatusOK("docs-02.json");
	}

	public void mockGetDocsConfiguration_notFoundDoc() {
		mockGetDocsConfigurationStatusOK("docs-03.json");
	}

	public void mockGetDocsConfiguration_incorrectLabel() {
		mockGetDocsConfigurationStatusOK("docs-04.json");
	}

	public void mockGetDocsConfiguration_incorrectDescription() {
		mockGetDocsConfigurationStatusOK("docs-05.json");
	}

	public void mockGetDocsConfiguration_mutipleDiffs() {
		mockGetDocsConfigurationStatusOK("docs-06.json");
	}

	public void mockGetDocsConfiguration_withWarnings() {
		mockGetDocsConfigurationStatusOK("docs-07.json");
	}

}
