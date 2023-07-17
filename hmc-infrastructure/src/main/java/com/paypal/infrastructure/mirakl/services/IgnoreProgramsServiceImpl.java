package com.paypal.infrastructure.mirakl.services;

import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Component
public class IgnoreProgramsServiceImpl implements IgnoreProgramsService {

	@Resource
	private HyperwalletProgramsConfiguration userHyperwalletApiConfig;

	@Override
	public void ignorePrograms(final List<String> programs) {
		final List<String> ignoredPrograms = CollectionUtils.isEmpty(programs) ? List.of() : programs;
		userHyperwalletApiConfig.setIgnoredHyperwalletPrograms(ignoredPrograms);
		log.info("Setting ignored program to: {}", String.join(",", ignoredPrograms));
	}

	@Override
	public List<String> getIgnoredPrograms() {
		return userHyperwalletApiConfig.getIgnoredHyperwalletPrograms();
	}

	@Override
	public boolean isIgnored(final String program) {
		return !CollectionUtils.isEmpty(getIgnoredPrograms()) && getIgnoredPrograms().contains(program);
	}

}
