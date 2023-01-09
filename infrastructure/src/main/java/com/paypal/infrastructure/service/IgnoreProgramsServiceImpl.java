package com.paypal.infrastructure.service;

import com.paypal.infrastructure.hyperwallet.api.UserHyperwalletApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class IgnoreProgramsServiceImpl implements IgnoreProgramsService {

	@Resource
	private UserHyperwalletApiConfig userHyperwalletApiConfig;

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
	public boolean isIgnored(String program) {
		return !CollectionUtils.isEmpty(getIgnoredPrograms()) && getIgnoredPrograms().contains(program);
	}

}
