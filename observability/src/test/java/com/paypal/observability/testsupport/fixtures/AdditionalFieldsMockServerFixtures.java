package com.paypal.observability.testsupport.fixtures;

import com.paypal.observability.testsupport.fixtures.AbstractMockServerFixtures;
import org.mockserver.client.MockServerClient;

public class AdditionalFieldsMockServerFixtures extends AbstractMockServerFixtures {

	public AdditionalFieldsMockServerFixtures(final MockServerClient mockServerClient) {
		super(mockServerClient);
	}

	@Override
	protected String getFolder() {
		return "mirakl/fields";
	}

	private void mockGetAdditionalFields(final String responseFile, final int statusCode) {
		mockGet("/api/additional_fields", responseFile, statusCode);
	}

	private void mockGetAdditionalFieldsStatusOK(final String responseFile) {
		mockGetAdditionalFields(responseFile, 200);
	}

	private void mockGetAdditionalFieldsStatusServerError(final String responseFile) {
		mockGetAdditionalFields(responseFile, 500);
	}

	public void mockGetAdditionalFields_emptySchema() {
		mockGetAdditionalFieldsStatusOK("custom-fields-00.json");
	}

	public void mockGetAdditionalFields_kyc_correct() {
		mockGetAdditionalFieldsStatusOK("custom-fields-01.json");
	}

	public void mockGetAdditionalFields_kyc_correctWithWarnings() {
		mockGetAdditionalFieldsStatusOK("custom-fields-13.json");
	}

	public void mockGetAdditionalFields_kyc_incorrectWithFails() {
		mockGetAdditionalFieldsStatusOK("custom-fields-14.json");
	}

	public void mockGetAdditionalFields_internalServerError() {
		mockGetAdditionalFieldsStatusServerError("internal-server-error.json");
	}

	public void mockGetAdditionalFields_nonkyc_correct() {
		mockGetAdditionalFieldsStatusOK("custom-fields-02.json");
	}

	public void mockGetAdditionalFields_nonkyc_additionalField() {
		mockGetAdditionalFieldsStatusOK("custom-fields-03.json");
	}

	public void mockGetAdditionalFields_nonkyc_notFoundField() {
		mockGetAdditionalFieldsStatusOK("custom-fields-04.json");
	}

	public void mockGetAdditionalFields_nonkyc_incorrectLabel() {
		mockGetAdditionalFieldsStatusOK("custom-fields-05.json");
	}

	public void mockGetAdditionalFields_nonkyc_incorrectDescription() {
		mockGetAdditionalFieldsStatusOK("custom-fields-06.json");
	}

	public void mockGetAdditionalFields_nonkyc_incorrectType() {
		mockGetAdditionalFieldsStatusOK("custom-fields-07.json");
	}

	public void mockGetAdditionalFields_nonkyc_incorrectAllowedValues() {
		mockGetAdditionalFieldsStatusOK("custom-fields-08.json");
	}

	public void mockGetAdditionalFields_nonkyc_incorrectPermissions() {
		mockGetAdditionalFieldsStatusOK("custom-fields-09.json");
	}

	public void mockGetAdditionalFields_nonkyc_incorrectRegex() {
		mockGetAdditionalFieldsStatusOK("custom-fields-12.json");
	}

	public void mockGetAdditionalFields_nonkyc_multipleErrorsOnSameField() {
		mockGetAdditionalFieldsStatusOK("custom-fields-10.json");
	}

	public void mockGetAdditionalFields_nonkyc_multipleErrorsOnDifferentFields() {
		mockGetAdditionalFieldsStatusOK("custom-fields-11.json");
	}

}
