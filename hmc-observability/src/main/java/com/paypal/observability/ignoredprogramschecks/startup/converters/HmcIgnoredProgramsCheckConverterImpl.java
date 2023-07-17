package com.paypal.observability.ignoredprogramschecks.startup.converters;

import com.paypal.observability.ignoredprogramschecks.model.HmcIgnoredProgramsCheck;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HmcIgnoredProgramsCheckConverterImpl implements HmcIgnoredProgramsCheckConverter {

	@Override
	public StartupCheck from(final HmcIgnoredProgramsCheck ignoredProgramsCheck) {
		return StartupCheck.builder()
				.status(ignoredProgramsCheck.isSubset() ? StartupCheckStatus.READY
						: StartupCheckStatus.READY_WITH_WARNINGS)
				.statusMessage(getStatusMessage(ignoredProgramsCheck)).build();
	}

	private Optional<String> getStatusMessage(final HmcIgnoredProgramsCheck ignoredProgramsCheck) {
		if (!ignoredProgramsCheck.isSubset()) {
			return Optional.of(String.format("The [%s] list of ignored programs is not a subset of [%s] programs",
					String.join(",", ignoredProgramsCheck.getIgnoredPrograms()),
					String.join(",", ignoredProgramsCheck.getPrograms())));
		}
		else {
			return Optional.empty();
		}
	}

}
