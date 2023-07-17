package com.paypal.observability.ignoredprogramschecks;

import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import com.paypal.observability.AbstractObservabilityIntegrationTest;
import com.paypal.observability.ignoredprogramschecks.startup.HmcIgnoredProgramsStartupCheckProvider;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HmcIgnoredProgramsStartupCheckProviderTestTest extends AbstractObservabilityIntegrationTest {

	@Autowired
	private HmcIgnoredProgramsStartupCheckProvider testObj;

	@Autowired
	private HyperwalletProgramsConfiguration hyperwalletProgramsConfiguration;

	private List<String> hyperwalletPrograms;

	private List<String> ignoredHyperwalletPrograms;

	private List<String> hyperwalletUserProgramTokens;

	private List<String> hyperwalletPaymentProgramTokens;

	private List<String> hyperwalletBankAccountTokens;

	@BeforeEach
	void setUp() {
		saveProgramsConfiguration();
	}

	@AfterEach
	void tearDown() {
		reloadSavedProgramsConfiguration();
	}

	@Test
	void check_shouldReturnReadyWhenIgnoredProgramsAreSubsetOfProgramTokens() {
		updateProgramsConfiguration(List.of("UK", "EUROPE"), List.of("EUROPE"), List.of("UT1", ""), List.of("PT1", ""),
				List.of("BT1", ""));

		final StartupCheck check = testObj.check();

		assertThat(check.getStatus()).isEqualTo(StartupCheckStatus.READY);
	}

	@Test
	void check_shouldReturnReadyWithWarningsWhenIgnoredProgramsAreNotSubsetOfProgramTokens() {
		updateProgramsConfiguration(List.of("UK", "EUROPE"), List.of("DEFAULT"), List.of("UT1", ""), List.of("PT1", ""),
				List.of("BT1", ""));

		final StartupCheck check = testObj.check();

		assertThat(check.getStatus()).isEqualTo(StartupCheckStatus.READY_WITH_WARNINGS);
	}

	@Test
	void check_shouldReturnReadyWhenIgnoredProgramsIsEmpty() {
		updateProgramsConfiguration(List.of("UK", "EUROPE"), List.of(), List.of("UT1", "UT2"), List.of("PT1", "PT2"),
				List.of("BT1", "BT2"));

		final StartupCheck check = testObj.check();

		assertThat(check.getStatus()).isEqualTo(StartupCheckStatus.READY);
	}

	@Test
	void check_shouldReturnReadyWhenIgnoredProgramsIsNull() {
		ReflectionTestUtils.setField(hyperwalletProgramsConfiguration, "hyperwalletProgramsNames",
				List.of("UK", "EUROPE"));
		ReflectionTestUtils.setField(hyperwalletProgramsConfiguration, "ignoredHyperwalletPrograms", null);

		final StartupCheck check = testObj.check();

		assertThat(check.getStatus()).isEqualTo(StartupCheckStatus.READY);
	}

	void updateProgramsConfiguration(final List<String> hyperwalletPrograms,
			final List<String> ignoredHyperwalletPrograms, final List<String> hyperwalletUserProgramTokens,
			final List<String> hyperwalletPaymentProgramTokens, final List<String> hyperwalletBankAccountTokens) {
		ReflectionTestUtils.setField(hyperwalletProgramsConfiguration, "hyperwalletProgramsNames", hyperwalletPrograms);
		ReflectionTestUtils.setField(hyperwalletProgramsConfiguration, "ignoredHyperwalletPrograms",
				ignoredHyperwalletPrograms);
		ReflectionTestUtils.setField(hyperwalletProgramsConfiguration, "hyperwalletUserProgramTokens",
				hyperwalletUserProgramTokens);
		ReflectionTestUtils.setField(hyperwalletProgramsConfiguration, "hyperwalletPaymentProgramTokens",
				hyperwalletPaymentProgramTokens);
		ReflectionTestUtils.setField(hyperwalletProgramsConfiguration, "hyperwalletBankAccountTokens",
				hyperwalletBankAccountTokens);
		ReflectionTestUtils.invokeMethod(hyperwalletProgramsConfiguration, "buildProgramTokensMap");
	}

	@SuppressWarnings("unchecked")
	void saveProgramsConfiguration() {
		hyperwalletPrograms = new ArrayList<>((List<String>) ReflectionTestUtils
				.getField(hyperwalletProgramsConfiguration, "hyperwalletProgramsNames"));
		ignoredHyperwalletPrograms = new ArrayList<>((List<String>) ReflectionTestUtils
				.getField(hyperwalletProgramsConfiguration, "ignoredHyperwalletPrograms"));
		hyperwalletUserProgramTokens = new ArrayList<>((List<String>) ReflectionTestUtils
				.getField(hyperwalletProgramsConfiguration, "hyperwalletUserProgramTokens"));
		hyperwalletPaymentProgramTokens = new ArrayList<>((List<String>) ReflectionTestUtils
				.getField(hyperwalletProgramsConfiguration, "hyperwalletPaymentProgramTokens"));
		hyperwalletBankAccountTokens = new ArrayList<>((List<String>) ReflectionTestUtils
				.getField(hyperwalletProgramsConfiguration, "hyperwalletBankAccountTokens"));
	}

	void reloadSavedProgramsConfiguration() {
		updateProgramsConfiguration(hyperwalletPrograms, ignoredHyperwalletPrograms, hyperwalletUserProgramTokens,
				hyperwalletPaymentProgramTokens, hyperwalletBankAccountTokens);
	}

}
