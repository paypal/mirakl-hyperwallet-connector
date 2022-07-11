package com.paypal.observability.miraklfieldschecks;

import com.paypal.observability.miraklfieldschecks.startup.MiraklFieldSchemaStartupCheckPrinter;
import com.paypal.observability.miraklfieldschecks.startup.MiraklFieldSchemaStartupCheckProvider;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckStatus;
import com.paypal.observability.testsupport.AbstractSchemaStartupAdapterITTest;
import com.paypal.observability.testsupport.ObservabilityIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@ObservabilityIntegrationTest
class MiraklFieldSchemaStartupAdapterITTest extends AbstractSchemaStartupAdapterITTest {

	@Autowired
	private MiraklFieldSchemaStartupCheckProvider miraklFieldSchemaStartupCheckProvider;

	@Autowired
	private MiraklFieldSchemaStartupCheckPrinter miraklFieldSchemaStartupCheckPrinter;

	@Test
	void shouldCheckAndReturnReadyStatusWhenAllItsOK() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_kyc_correct();

		final StartupCheck startupCheck = miraklFieldSchemaStartupCheckProvider.check();
		assertThat(startupCheck.getStatus()).isEqualTo(StartupCheckStatus.READY);
		assertThat(startupCheck.getStatusMessage()).contains("The current schema in Mirakl is the expected one.");

		final String[] report = miraklFieldSchemaStartupCheckPrinter.print(startupCheck);
		assertThat(report).isEmpty();
	}

	@Test
	void shouldCheckAndReturnReadyWithWarningsStatusWhenThereAreWarnings() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_kyc_correctWithWarnings();

		final StartupCheck startupCheck = miraklFieldSchemaStartupCheckProvider.check();
		assertThat(startupCheck.getStatus()).isEqualTo(StartupCheckStatus.READY_WITH_WARNINGS);
		assertThat(startupCheck.getStatusMessage()).contains(
				"The current schema in Mirakl server is compatible with this version of HMC although there are some differences that are recommended to be solved.");

		final String[] report = miraklFieldSchemaStartupCheckPrinter.print(startupCheck);
		assertThat(report).hasSize(2);
		assertThatContainsMessage(report, "Property 'label' doesn't have the correct value");
		assertThatContainsMessage(report, "Property 'description' doesn't have the correct value");
	}

	@Test
	void shouldCheckAndReturnNotReadyWhenThereAreFails() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_kyc_incorrectWithFails();

		final StartupCheck startupCheck = miraklFieldSchemaStartupCheckProvider.check();
		assertThat(startupCheck.getStatus()).isEqualTo(StartupCheckStatus.NOT_READY);
		assertThat(startupCheck.getStatusMessage())
				.contains("The current schema in Mirakl server is not compatible with this version of HMC.");

		final String[] report = miraklFieldSchemaStartupCheckPrinter.print(startupCheck);
		assertThat(report).hasSize(7);
		assertThatContainsMessage(report, "Property 'label' doesn't have the correct value");
		assertThatContainsMessage(report, "Property 'description' doesn't have the correct value");
		assertThatContainsMessage(report, "Property 'type' doesn't have the correct value");
		assertThatContainsMessage(report, "Property 'permissions' doesn't have the correct value");
		assertThatContainsMessage(report, "Property 'required' doesn't have the correct value");
		assertThatContainsSetMessage(report,
				"Expected field 'hw-stakeholder-proof-identity-type-5' has not been found");
		assertThatContainsSetMessage(report,
				"An unexpected field named 'hw-stakeholder-proof-identity-type-MODIFIED' has been found");
	}

}
