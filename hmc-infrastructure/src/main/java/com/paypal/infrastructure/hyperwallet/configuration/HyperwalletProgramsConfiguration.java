package com.paypal.infrastructure.hyperwallet.configuration;

import com.paypal.infrastructure.support.exceptions.InvalidConfigurationException;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class HyperwalletProgramsConfiguration {

	@Value("#{'${hmc.hyperwallet.programs.names}'.split(',')}")
	private List<String> hyperwalletProgramsNames;

	@Value("#{'${hmc.hyperwallet.programs.ignored}'.split(',')}")
	private List<String> ignoredHyperwalletPrograms;

	@Value("#{'${hmc.hyperwallet.programs.userTokens}'.split(',')}")
	private List<String> hyperwalletUserProgramTokens;

	@Value("#{'${hmc.hyperwallet.programs.paymentTokens}'.split(',')}")
	private List<String> hyperwalletPaymentProgramTokens;

	@Value("#{'${hmc.hyperwallet.programs.bankAccountTokens}'.split(',')}")
	private List<String> hyperwalletBankAccountTokens;

	@Value("#{'${hmc.hyperwallet.programs.rootToken}'}")
	private String hyperwalletRootProgramToken;

	private Map<String, HyperwalletProgramConfiguration> programTokensMap = new HashMap<>();

	public HyperwalletProgramConfiguration getProgramConfiguration(final String programName) {
		return programTokensMap.get(programName);
	}

	public List<HyperwalletProgramConfiguration> getAllProgramConfigurations() {
		return new ArrayList<>(programTokensMap.values());
	}

	public String getRootProgramToken() {
		return !hyperwalletRootProgramToken.isBlank() ? hyperwalletRootProgramToken
				: hyperwalletUserProgramTokens.get(0);
	}

	public void setIgnoredHyperwalletPrograms(final List<String> ignoredPrograms) {
		ignoredHyperwalletPrograms = new ArrayList<>(ignoredPrograms);
		buildProgramTokensMap();
	}

	public List<String> getIgnoredHyperwalletPrograms() {
		return ignoredHyperwalletPrograms;
	}

	public List<String> getHyperwalletProgramsNames() {
		return hyperwalletProgramsNames;
	}

	@PostConstruct
	private void initializeConfiguration() {
		cleanConfigurationVariables();
		buildProgramTokensMap();
	}

	private void buildProgramTokensMap() {
		//@formatter:off
		try {
			programTokensMap = IntStream.range(0, hyperwalletProgramsNames.size())
					.mapToObj(i -> new HyperwalletProgramConfiguration(hyperwalletProgramsNames.get(i),
							ignoredHyperwalletPrograms.contains(hyperwalletProgramsNames.get(i)),
							hyperwalletUserProgramTokens.get(i),
							hyperwalletPaymentProgramTokens.get(i),
							hyperwalletBankAccountTokens.get(i)))
					.collect(Collectors.toMap(HyperwalletProgramConfiguration::getProgramName, Function.identity()));
		}
		catch (final Exception e) {
			throw new InvalidConfigurationException("Property files configuration error. Check hyperwallet program tokens configuration.", e);
		}
		//@formatter:on
	}

	private void cleanConfigurationVariables() {
		ignoredHyperwalletPrograms = removeEmptyStringValues(ignoredHyperwalletPrograms);
		hyperwalletProgramsNames = removeEmptyStringValues(hyperwalletProgramsNames);
	}

	private List<String> removeEmptyStringValues(final List<String> ignoredHyperwalletPrograms) {
		return ignoredHyperwalletPrograms.stream().filter(program -> !program.equals("")).collect(Collectors.toList());
	}

	@Data
	@AllArgsConstructor
	public static class HyperwalletProgramConfiguration {

		private String programName;

		private boolean ignored;

		private String usersProgramToken;

		private String paymentProgramToken;

		private String bankAccountToken;

	}

}
