package com.paypal.observability.ignoredprogramschecks.startup;

import com.paypal.observability.ignoredprogramschecks.model.HmcIgnoredProgramsCheck;
import com.paypal.observability.ignoredprogramschecks.service.HmcIgnoredProgramsStartUpCheckService;
import com.paypal.observability.ignoredprogramschecks.startup.converters.HmcIgnoredProgramsCheckConverter;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckProvider;
import org.springframework.stereotype.Component;

@Component
public class HmcIgnoredProgramsStartupCheckProvider implements StartupCheckProvider {

	private final HmcIgnoredProgramsStartUpCheckService hmcIgnoredProgramsStartUpCheckService;

	private final HmcIgnoredProgramsCheckConverter hmcIgnoredProgramsCheckConverter;

	public HmcIgnoredProgramsStartupCheckProvider(
			final HmcIgnoredProgramsStartUpCheckService hmcIgnoredProgramsStartUpCheckService,
			final HmcIgnoredProgramsCheckConverter hmcIgnoredProgramsCheckConverter) {
		this.hmcIgnoredProgramsStartUpCheckService = hmcIgnoredProgramsStartUpCheckService;
		this.hmcIgnoredProgramsCheckConverter = hmcIgnoredProgramsCheckConverter;
	}

	@Override
	public StartupCheck check() {
		final HmcIgnoredProgramsCheck hmcIgnoredProgramsCheck = hmcIgnoredProgramsStartUpCheckService
				.checkIgnoredPrograms();

		return hmcIgnoredProgramsCheckConverter.from(hmcIgnoredProgramsCheck);
	}

	@Override
	public String getName() {
		return "HmcIgnoredProgramsCheck";
	}

}
