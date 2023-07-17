package com.paypal.observability.miraklfieldschecks;

import com.paypal.observability.AbstractObservabilityIntegrationTest;
import com.paypal.observability.miraklfieldschecks.services.MiraklFieldSchemaCheckerService;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryType;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReport;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReportSeverity;
import com.paypal.observability.testsupport.MiraklSchemaAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class MiraklFieldSchemaChecksITTest extends AbstractObservabilityIntegrationTest {

	@Value("${hmc.toggle-features.automated-kyc}")
	private boolean originalIsKycAutomated;

	@Autowired
	private MiraklFieldSchemaCheckerService miraklFieldSchemaCheckerService;

	@AfterEach
	void resetKycAutomatedStatus() {
		setKycAutomated(originalIsKycAutomated);
	}

	@Test
	void shouldCheckOnlyKycFields() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_nonkyc_correct();
		setKycAutomated(false);

		final MiraklSchemaDiffReport diffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();
		assertThat(diffReport.hasEntries()).isFalse();
	}

	@Test
	void shouldCheckAllFields() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_kyc_correct();
		setKycAutomated(true);

		final MiraklSchemaDiffReport diffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();
		assertThat(diffReport.hasEntries()).isFalse();
	}

	@Test
	void shouldDetectAdditionalField() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_nonkyc_additionalField();
		setKycAutomated(false);

		final MiraklSchemaDiffReport diffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();
		assertThat(diffReport.getDiffs()).hasSize(1);
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.UNEXPECTED_ITEM,
				"hw-unexpected-field-1");
	}

	@Test
	void shouldDetectNotFoundField() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_nonkyc_notFoundField();
		setKycAutomated(false);

		final MiraklSchemaDiffReport diffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();
		assertThat(diffReport.getDiffs()).hasSize(1);
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.ITEM_NOT_FOUND,
				"hw-terms-consent");
	}

	@Test
	void shouldDetectIncorrectLabel() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_nonkyc_incorrectLabel();
		setKycAutomated(false);

		final MiraklSchemaDiffReport diffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();
		assertThat(diffReport.getDiffs()).hasSize(1);
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-program", "label");
	}

	@Test
	void shouldDetectIncorrectDescription_IgnoringLastDotAndEndLine() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_nonkyc_incorrectDescription();
		setKycAutomated(false);

		final MiraklSchemaDiffReport diffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();
		assertThat(diffReport.getDiffs()).hasSize(1);
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-program", "description");
	}

	@Test
	void shouldDetectIncorrectType() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_nonkyc_incorrectType();
		setKycAutomated(false);

		final MiraklSchemaDiffReport diffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();
		assertThat(diffReport.getDiffs()).hasSize(1);
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-user-token", "type");
	}

	@Test
	void shouldDetectIncorrectAllowedValues() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_nonkyc_incorrectAllowedValues();
		setKycAutomated(true);

		final MiraklSchemaDiffReport diffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();
		assertThat(diffReport.getDiffs()).hasSize(1);
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-government-id-type", "allowedValues");
	}

	@Test
	void shouldDetectIncorrectPermissions() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_nonkyc_incorrectPermissions();
		setKycAutomated(false);

		final MiraklSchemaDiffReport diffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();
		assertThat(diffReport.getDiffs()).hasSize(2);
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-bankaccount-token", "permissions");
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-bankaccount-state", "permissions");
	}

	@Test
	void shouldDetectIncorrectRegex() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_nonkyc_incorrectRegex();
		setKycAutomated(false);

		final MiraklSchemaDiffReport diffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();

		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-terms-consent", "regexp");
	}

	@Test
	void shouldDetectMultipleDiffsOnSameField() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_nonkyc_multipleErrorsOnSameField();
		setKycAutomated(false);

		final MiraklSchemaDiffReport diffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();
		assertThat(diffReport.getDiffs()).hasSize(5);
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-terms-consent", "label");
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-terms-consent", "description");
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-terms-consent", "permissions");
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-terms-consent", "required");
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-terms-consent", "type");
	}

	@Test
	void shouldDetectMultipleDiffsOnDifferentField_AndIgnoreNonHwFields() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_nonkyc_multipleErrorsOnDifferentFields();
		setKycAutomated(false);

		final MiraklSchemaDiffReport diffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();
		assertThat(diffReport.getDiffs()).hasSize(7);
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-program", "description");
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-user-token", "label");
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-bankaccount-token");
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-bankaccount-state", "type");
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.UNEXPECTED_ITEM,
				"hw-terms-consent-CHANGED");
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.ITEM_NOT_FOUND,
				"hw-terms-consent");
		MiraklSchemaAssertions.assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-bankaccount-state", "regexp");
	}

	@Test
	void shouldProvideDifferentSeverityDependingOnSchemaDiffType() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_nonkyc_multipleErrorsOnDifferentFields();
		setKycAutomated(false);

		final MiraklSchemaDiffReport diffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();
		MiraklSchemaAssertions.assertSeverityForDiffType(diffReport, MiraklSchemaDiffReportSeverity.FAIL,
				MiraklSchemaDiffEntryType.ITEM_NOT_FOUND);
		MiraklSchemaAssertions.assertSeverityForDiffType(diffReport, MiraklSchemaDiffReportSeverity.WARN,
				MiraklSchemaDiffEntryType.UNEXPECTED_ITEM);
		MiraklSchemaAssertions.assertSeverityForDiffType(diffReport, MiraklSchemaDiffReportSeverity.FAIL, "type");
		MiraklSchemaAssertions.assertSeverityForDiffType(diffReport, MiraklSchemaDiffReportSeverity.WARN, "label");
		MiraklSchemaAssertions.assertSeverityForDiffType(diffReport, MiraklSchemaDiffReportSeverity.FAIL,
				"permissions");
		MiraklSchemaAssertions.assertSeverityForDiffType(diffReport, MiraklSchemaDiffReportSeverity.FAIL, "regexp");
		MiraklSchemaAssertions.assertSeverityForDiffType(diffReport, MiraklSchemaDiffReportSeverity.FAIL,
				"allowedValues");
		MiraklSchemaAssertions.assertSeverityForDiffType(diffReport, MiraklSchemaDiffReportSeverity.WARN,
				"description");
		MiraklSchemaAssertions.assertSeverityForDiffType(diffReport, MiraklSchemaDiffReportSeverity.WARN, "required");
	}

	private void setKycAutomated(final boolean isKycAutomated) {
		ReflectionTestUtils.setField(miraklFieldSchemaCheckerService, "isKycAutomated", isKycAutomated);
	}

}
