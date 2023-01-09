package com.paypal.observability.ignoredprogramschecks.service;

import com.paypal.infrastructure.hyperwallet.api.UserHyperwalletApiConfig;
import com.paypal.observability.ignoredprogramschecks.model.HmcIgnoredProgramsCheck;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;

@Component
public class HmcIgnoredProgramsStartUpCheckServiceImpl implements HmcIgnoredProgramsStartUpCheckService {

	private final UserHyperwalletApiConfig userHyperwalletApiConfig;

	public HmcIgnoredProgramsStartUpCheckServiceImpl(final UserHyperwalletApiConfig userHyperwalletApiConfig) {
		this.userHyperwalletApiConfig = userHyperwalletApiConfig;
	}

	@Override
	public HmcIgnoredProgramsCheck checkIgnoredPrograms() {
		final List<String> ignoredHyperwalletPrograms = userHyperwalletApiConfig.getIgnoredHyperwalletPrograms();
		final List<String> hyperwalletPrograms = userHyperwalletApiConfig.getHyperwalletPrograms();
		final boolean subset = CollectionUtils.isEmpty(ignoredHyperwalletPrograms)
				|| new HashSet<>(hyperwalletPrograms).containsAll(ignoredHyperwalletPrograms);

		return HmcIgnoredProgramsCheck.builder().isSubset(subset).programs(hyperwalletPrograms)
				.ignoredPrograms(ignoredHyperwalletPrograms).build();
	}

}
