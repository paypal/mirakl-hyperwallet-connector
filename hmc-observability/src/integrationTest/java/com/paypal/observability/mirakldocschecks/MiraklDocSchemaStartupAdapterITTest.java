package com.paypal.observability.mirakldocschecks;

import com.paypal.observability.AbstractObservabilityIntegrationTest;
import com.paypal.observability.mirakldocschecks.startup.MiraklDocSchemaStartupCheckPrinter;
import com.paypal.observability.mirakldocschecks.startup.MiraklDocSchemaStartupCheckProvider;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckStatus;
import com.paypal.observability.testsupport.MiraklReportAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class MiraklDocSchemaStartupAdapterITTest extends AbstractObservabilityIntegrationTest {

	@Autowired
	private MiraklDocSchemaStartupCheckProvider miraklDocSchemaStartupCheckProvider;

	@Autowired
	private MiraklDocSchemaStartupCheckPrinter miraklDocSchemaStartupCheckPrinter;

	@Test
	void shouldCheckAndReturnReadyStatusWhenAllItsOK() {
		docsMockServerFixtures.mockGetDocsConfiguration_correctSchemaResponse();

		final StartupCheck startupCheck = miraklDocSchemaStartupCheckProvider.check();
		assertThat(startupCheck.getStatus()).isEqualTo(StartupCheckStatus.READY);
		assertThat(startupCheck.getStatusMessage()).contains("The current schema in Mirakl is the expected one.");

		final String[] report = miraklDocSchemaStartupCheckPrinter.print(startupCheck);
		assertThat(report).isEmpty();
	}

	@Test
	void shouldCheckAndReturnReadyWithWarningsStatusWhenThereAreWarnings() {
		docsMockServerFixtures.mockGetDocsConfiguration_withWarnings();

		final StartupCheck startupCheck = miraklDocSchemaStartupCheckProvider.check();
		assertThat(startupCheck.getStatus()).isEqualTo(StartupCheckStatus.READY_WITH_WARNINGS);
		assertThat(startupCheck.getStatusMessage()).contains(
				"The current schema in Mirakl server is compatible with this version of HMC although there are some differences that are recommended to be solved.");

		final String[] report = miraklDocSchemaStartupCheckPrinter.print(startupCheck);
		assertThat(report).hasSize(3);
		MiraklReportAssertions.assertThatContainsMessage(report, "Property 'label' doesn't have the correct value");
		MiraklReportAssertions.assertThatContainsMessage(report,
				"Property 'description' doesn't have the correct value");
		MiraklReportAssertions.assertThatContainsSetMessage(report,
				"An unexpected field named 'hw-bsh4-proof-identity-back-unexpected' has been found");
	}

	@Test
	void shouldCheckAndReturnNotReadyWhenThereAreFails() {
		docsMockServerFixtures.mockGetDocsConfiguration_mutipleDiffs();

		final StartupCheck startupCheck = miraklDocSchemaStartupCheckProvider.check();
		assertThat(startupCheck.getStatus()).isEqualTo(StartupCheckStatus.NOT_READY);
		assertThat(startupCheck.getStatusMessage())
				.contains("The current schema in Mirakl server is not compatible with this version of HMC.");

		final String[] report = miraklDocSchemaStartupCheckPrinter.print(startupCheck);
		assertThat(report).hasSize(4);
		MiraklReportAssertions.assertThatContainsMessage(report, "Property 'label' doesn't have the correct value");
		MiraklReportAssertions.assertThatContainsMessage(report,
				"Property 'description' doesn't have the correct value");
		MiraklReportAssertions.assertThatContainsSetMessage(report,
				"Expected field 'hw-bsh5-proof-identity-back' has not been found");
		MiraklReportAssertions.assertThatContainsSetMessage(report,
				"An unexpected field named 'hw-bsh5-proof-identity-back-CHANGED' has been found");
	}

}
