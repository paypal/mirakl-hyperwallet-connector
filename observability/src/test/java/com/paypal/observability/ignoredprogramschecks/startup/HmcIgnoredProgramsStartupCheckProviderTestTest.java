package com.paypal.observability.ignoredprogramschecks.startup;

import com.paypal.infrastructure.hyperwallet.api.UserHyperwalletApiConfig;
import com.paypal.observability.ObservabilityIntegrationTest;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HmcIgnoredProgramsStartupCheckProviderTestTest extends ObservabilityIntegrationTest {

	@Autowired
	private HmcIgnoredProgramsStartupCheckProvider testObj;

	@Autowired
	private UserHyperwalletApiConfig userHyperwalletApiConfig;

	private List<String> hyperwalletPrograms;

	private List<String> ignoredHyperwalletPrograms;

	@BeforeEach
	void setUp() {
		hyperwalletPrograms = userHyperwalletApiConfig.getHyperwalletPrograms();
		ignoredHyperwalletPrograms = userHyperwalletApiConfig.getIgnoredHyperwalletPrograms();
	}

	@AfterEach
	void tearDown() {
		ReflectionTestUtils.setField(userHyperwalletApiConfig, "hyperwalletPrograms", hyperwalletPrograms);
		ReflectionTestUtils.setField(userHyperwalletApiConfig, "ignoredHyperwalletPrograms",
				ignoredHyperwalletPrograms);
	}

	@Test
	void check_shouldReturnReadyWhenIgnoredProgramsAreSubsetOfProgramTokens() {
		ReflectionTestUtils.setField(userHyperwalletApiConfig, "hyperwalletPrograms", List.of("UK", "EUROPE"));
		ReflectionTestUtils.setField(userHyperwalletApiConfig, "ignoredHyperwalletPrograms", List.of("EUROPE"));

		final StartupCheck check = testObj.check();

		assertThat(check.getStatus()).isEqualTo(StartupCheckStatus.READY);
	}

	@Test
	void check_shouldReturnReadyWithWarningsWhenIgnoredProgramsAreNotSubsetOfProgramTokens() {
		ReflectionTestUtils.setField(userHyperwalletApiConfig, "hyperwalletPrograms", List.of("UK", "EUROPE"));
		ReflectionTestUtils.setField(userHyperwalletApiConfig, "ignoredHyperwalletPrograms", List.of("DEFAULT"));

		final StartupCheck check = testObj.check();

		assertThat(check.getStatus()).isEqualTo(StartupCheckStatus.READY_WITH_WARNINGS);
	}

	@Test
	void check_shouldReturnReadyWhenIgnoredProgramsIsEmpty() {
		ReflectionTestUtils.setField(userHyperwalletApiConfig, "hyperwalletPrograms", List.of("UK", "EUROPE"));
		ReflectionTestUtils.setField(userHyperwalletApiConfig, "ignoredHyperwalletPrograms", List.of());

		final StartupCheck check = testObj.check();

		assertThat(check.getStatus()).isEqualTo(StartupCheckStatus.READY);
	}

	@Test
	void check_shouldReturnReadyWhenIgnoredProgramsIsNull() {
		ReflectionTestUtils.setField(userHyperwalletApiConfig, "hyperwalletPrograms", List.of("UK", "EUROPE"));
		ReflectionTestUtils.setField(userHyperwalletApiConfig, "ignoredHyperwalletPrograms", null);

		final StartupCheck check = testObj.check();

		assertThat(check.getStatus()).isEqualTo(StartupCheckStatus.READY);
	}

}
