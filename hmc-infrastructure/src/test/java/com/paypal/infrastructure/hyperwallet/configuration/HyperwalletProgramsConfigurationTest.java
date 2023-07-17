package com.paypal.infrastructure.hyperwallet.configuration;

import com.paypal.infrastructure.support.exceptions.InvalidConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

//@formatter:off
class HyperwalletProgramsConfigurationTest {

	private HyperwalletProgramsConfiguration testObj;

	@BeforeEach
	void setUp() {
		testObj = new HyperwalletProgramsConfiguration();
	}

	@Test
	void getProgramConfiguration_shouldReturnConfiguration_whenSingleProgramIsConfigured() {
		setConfigurationVariables(List.of("DEFAULT"),
				List.of(),
				List.of("USER_PROGRAM_TOKEN_1"),
				List.of("PAYMENT_PROGRAM_TOKEN_1"),
				List.of("BANK_ACCOUNT_TOKEN_1"));

		final HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration result = testObj.getProgramConfiguration("DEFAULT");
		assertThat(result.getUsersProgramToken()).isEqualTo("USER_PROGRAM_TOKEN_1");
		assertThat(result.getPaymentProgramToken()).isEqualTo("PAYMENT_PROGRAM_TOKEN_1");
		assertThat(result.getBankAccountToken()).isEqualTo("BANK_ACCOUNT_TOKEN_1");
	}

	@Test
	void getProgramConfiguration_shouldReturnConfiguration_whenMultipleProgramIsConfigured() {
		configureMultipleProgramSetup();

		final HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration result1 = testObj.getProgramConfiguration("UK");
		assertThat(result1.getUsersProgramToken()).isEqualTo("USER_PROGRAM_TOKEN_1");
		assertThat(result1.getPaymentProgramToken()).isEqualTo("PAYMENT_PROGRAM_TOKEN_1");
		assertThat(result1.getBankAccountToken()).isEqualTo("BANK_ACCOUNT_TOKEN_1");

		final HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration result2 = testObj.getProgramConfiguration("EUROPE");
		assertThat(result2.getUsersProgramToken()).isEqualTo("USER_PROGRAM_TOKEN_2");
		assertThat(result2.getPaymentProgramToken()).isEqualTo("PAYMENT_PROGRAM_TOKEN_2");
		assertThat(result2.getBankAccountToken()).isEqualTo("BANK_ACCOUNT_TOKEN_2");
	}

	@Test
	void getProgramConfiguration_shouldReturnConfiguration_whenMultipleProgramIsConfigured_andOneIsIgnored() {
		setConfigurationVariables(List.of("UK", "IGNORED", "EUROPE"),
				List.of("IGNORED"),
				List.of("USER_PROGRAM_TOKEN_1", "", "USER_PROGRAM_TOKEN_2"),
				List.of("PAYMENT_PROGRAM_TOKEN_1", "", "PAYMENT_PROGRAM_TOKEN_2"),
				List.of("BANK_ACCOUNT_TOKEN_1", "", "BANK_ACCOUNT_TOKEN_2"));

		final HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration result1 = testObj.getProgramConfiguration("UK");
		assertThat(result1.getUsersProgramToken()).isEqualTo("USER_PROGRAM_TOKEN_1");
		assertThat(result1.getPaymentProgramToken()).isEqualTo("PAYMENT_PROGRAM_TOKEN_1");
		assertThat(result1.getBankAccountToken()).isEqualTo("BANK_ACCOUNT_TOKEN_1");
		assertThat(result1.isIgnored()).isFalse();

		final HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration result2 = testObj.getProgramConfiguration("EUROPE");
		assertThat(result2.getUsersProgramToken()).isEqualTo("USER_PROGRAM_TOKEN_2");
		assertThat(result2.getPaymentProgramToken()).isEqualTo("PAYMENT_PROGRAM_TOKEN_2");
		assertThat(result2.getBankAccountToken()).isEqualTo("BANK_ACCOUNT_TOKEN_2");
		assertThat(result2.isIgnored()).isFalse();

		final HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration result3 = testObj.getProgramConfiguration("IGNORED");
		assertThat(result3.isIgnored()).isTrue();

		final List<String> result4 = testObj.getIgnoredHyperwalletPrograms();
		assertThat(result4).containsExactly("IGNORED");
	}

	@Test
	void getProgramConfigurations_shouldReturnAllConfigurations() {
		configureMultipleProgramSetup();

		final List<HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration> result = testObj.getAllProgramConfigurations();
		assertThat(result).hasSize(2);
		assertThat(result.stream().map(it -> it.getProgramName()).collect(Collectors.toList())).contains("UK", "EUROPE");
	}

	@Test
	void getRootProgram_shouldReturnRootProgram_whenSet() {
		ReflectionTestUtils.setField(testObj, "hyperwalletRootProgramToken", "ROOT");
		configureMultipleProgramSetup();

		final String result = testObj.getRootProgramToken();
		assertThat(result).isEqualTo("ROOT");
	}

	@Test
	void getRootProgram_shouldReturnDefaultProgram_whenNotSet() {
		ReflectionTestUtils.setField(testObj, "hyperwalletRootProgramToken", "");
		configureMultipleProgramSetup();

		final String result = testObj.getRootProgramToken();
		assertThat(result).isEqualTo("USER_PROGRAM_TOKEN_1");
	}

	@Test
	void setIgnoredHyperwalletPrograms_shouldUpdateIgnoredPrograms_andRebuildMap() {
		configureMultipleProgramSetup();

		testObj.setIgnoredHyperwalletPrograms(List.of("EUROPE"));

		assertThat(testObj.getProgramConfiguration("UK").isIgnored()).isFalse();
		assertThat(testObj.getProgramConfiguration("EUROPE").isIgnored()).isTrue();
	}

	@Test
	void buildProgramTokensMap_throwsInvalidConfigurationException_whenNotAllTokensAreProvided() {
		setConfigurationVariablesWithoutInitializeConfiguration(List.of("UK", "EUROPE"),
				List.of(),
				List.of("USER_PROGRAM_TOKEN_2"),
				List.of("PAYMENT_PROGRAM_TOKEN_1", "PAYMENT_PROGRAM_TOKEN_2"),
				List.of("BANK_ACCOUNT_TOKEN_1", "BANK_ACCOUNT_TOKEN_2"));

		assertThatExceptionOfType(InvalidConfigurationException.class).isThrownBy(
				() -> ReflectionTestUtils.invokeMethod(testObj, "initializeConfiguration"));
	}

	void setConfigurationVariables(final List<String> hyperwalletPrograms, final List<String> ignoredHyperwalletPrograms,
									 final List<String> hyperwalletUserProgramTokens, final List<String> hyperwalletPaymentProgramTokens,
									 final List<String> hyperwalletBankAccountTokens) {
		setConfigurationVariablesWithoutInitializeConfiguration(hyperwalletPrograms, ignoredHyperwalletPrograms,
				hyperwalletUserProgramTokens, hyperwalletPaymentProgramTokens, hyperwalletBankAccountTokens);

		ReflectionTestUtils.invokeMethod(testObj, "initializeConfiguration");
	}

	private void setConfigurationVariablesWithoutInitializeConfiguration(final List<String> hyperwalletPrograms,
			 final List<String> ignoredHyperwalletPrograms, final List<String> hyperwalletUserProgramTokens,
			 final List<String> hyperwalletPaymentProgramTokens, final List<String> hyperwalletBankAccountTokens) {
		ReflectionTestUtils.setField(testObj, "hyperwalletProgramsNames",
				hyperwalletPrograms);
		ReflectionTestUtils.setField(testObj, "ignoredHyperwalletPrograms",
				ignoredHyperwalletPrograms);
		ReflectionTestUtils.setField(testObj, "hyperwalletUserProgramTokens",
				hyperwalletUserProgramTokens);
		ReflectionTestUtils.setField(testObj, "hyperwalletPaymentProgramTokens",
				hyperwalletPaymentProgramTokens);
		ReflectionTestUtils.setField(testObj, "hyperwalletBankAccountTokens",
				hyperwalletBankAccountTokens);
	}

	void configureMultipleProgramSetup() {
		setConfigurationVariables(List.of("UK", "EUROPE"),
				List.of(),
				List.of("USER_PROGRAM_TOKEN_1", "USER_PROGRAM_TOKEN_2"),
				List.of("PAYMENT_PROGRAM_TOKEN_1", "PAYMENT_PROGRAM_TOKEN_2"),
				List.of("BANK_ACCOUNT_TOKEN_1", "BANK_ACCOUNT_TOKEN_2"));
	}
}
