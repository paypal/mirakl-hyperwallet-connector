package com.paypal.observability.ignoredprogramschecks.startup.converters;

import com.paypal.observability.ignoredprogramschecks.model.HmcIgnoredProgramsCheck;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HmcIgnoredProgramsCheckConverterImplTest {

	@InjectMocks
	private HmcIgnoredProgramsCheckConverterImpl testObj;

	@Test
	void from_shouldConvertIgnoredProgramsCheckToStartUpCheckAndEnsureTheErrorMessageIsShownWhenIgnoredProgramsAreNotSubsetOfPrograms() {
		final HmcIgnoredProgramsCheck ignoredProgramsCheck = HmcIgnoredProgramsCheck.builder()
				.ignoredPrograms(List.of("PROGRAM1", "PROGRAM2")).programs(List.of("PROGRAM3", "PROGRAM4"))
				.isSubset(false).build();

		final StartupCheck result = testObj.from(ignoredProgramsCheck);

		assertThat(result).hasFieldOrPropertyWithValue("status", StartupCheckStatus.READY_WITH_WARNINGS)
				.hasFieldOrPropertyWithValue("statusMessage",
						Optional.of(String.format("The [%s] list of ignored programs is not a subset of [%s] programs",
								"PROGRAM1,PROGRAM2", "PROGRAM3,PROGRAM4")));
	}

	@Test
	void from_shouldConvertIgnoredProgramsCheckToStartUpCheckAndEnsureTheErrorMessageIsNotShownWhenIgnoredProgramsAreSubsetOfPrograms() {
		final HmcIgnoredProgramsCheck ignoredProgramsCheck = HmcIgnoredProgramsCheck.builder()
				.ignoredPrograms(List.of("PROGRAM1")).programs(List.of("PROGRAM1", "PROGRAM2")).isSubset(true).build();

		final StartupCheck result = testObj.from(ignoredProgramsCheck);

		assertThat(result).hasFieldOrPropertyWithValue("status", StartupCheckStatus.READY)
				.hasFieldOrPropertyWithValue("statusMessage", Optional.empty());
	}

}
