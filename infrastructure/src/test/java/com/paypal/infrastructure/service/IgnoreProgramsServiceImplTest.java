package com.paypal.infrastructure.service;

import com.paypal.infrastructure.hyperwallet.api.UserHyperwalletApiConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IgnoreProgramsServiceImplTest {

	@InjectMocks
	private IgnoreProgramsServiceImpl testObj;

	@Mock
	private UserHyperwalletApiConfig userHyperwalletApiConfigMock;

	@Test
	void ignorePrograms_shouldCallHyperwalletApiConfigMockWithPassedPrograms() {
		final List<String> programsToIgnore = List.of("PROGRAM_1", "PROGRAM_2");

		testObj.ignorePrograms(programsToIgnore);

		verify(userHyperwalletApiConfigMock).setIgnoredHyperwalletPrograms(programsToIgnore);
	}

	@Test
	void ignorePrograms_shouldNotFailWhenListIsEmpty() {
		final List<String> programsToIgnore = List.of();

		testObj.ignorePrograms(programsToIgnore);

		verify(userHyperwalletApiConfigMock).setIgnoredHyperwalletPrograms(programsToIgnore);
	}

	@Test
	void ignorePrograms_shouldPassAnEmtpyListWhenListIsNull() {
		final List<String> programsToIgnore = null;

		testObj.ignorePrograms(programsToIgnore);

		verify(userHyperwalletApiConfigMock).setIgnoredHyperwalletPrograms(List.of());
	}

	@Test
	void getIgnoredPrograms_shouldReturnTheListReturnedByUserHyperwalletConfig() {
		final List<String> programs = List.of("PROGRAM_1");
		when(userHyperwalletApiConfigMock.getIgnoredHyperwalletPrograms()).thenReturn(programs);

		final List<String> result = testObj.getIgnoredPrograms();

		assertThat(result).isSameAs(programs);

	}

	@Test
	void isIgnored_shouldReturnTrue_whenIgnoredProgramsContainsProgram() {
		final List<String> programs = List.of("PROGRAM_1", "PROGRAM_2");
		when(userHyperwalletApiConfigMock.getIgnoredHyperwalletPrograms()).thenReturn(programs);

		final boolean result = testObj.isIgnored("PROGRAM_1");

		assertThat(result).isTrue();
	}

	@Test
	void isIgnored_shouldReturnFalse_whenIgnoredProgramsNotContainsProgram() {
		final List<String> programs = List.of("PROGRAM_2", "PROGRAM_3");
		when(userHyperwalletApiConfigMock.getIgnoredHyperwalletPrograms()).thenReturn(programs);

		final boolean result = testObj.isIgnored("PROGRAM_1");

		assertThat(result).isFalse();
	}

	@Test
	void isIgnored_shouldReturnFalse_whenIgnoredProgramsListIsEmpty() {
		final List<String> programs = List.of();
		when(userHyperwalletApiConfigMock.getIgnoredHyperwalletPrograms()).thenReturn(programs);

		final boolean result = testObj.isIgnored("PROGRAM_1");

		assertThat(result).isFalse();
	}

	@Test
	void isIgnored_shouldReturnFalse_whenIgnoredProgramsListIsNull() {
		when(userHyperwalletApiConfigMock.getIgnoredHyperwalletPrograms()).thenReturn(null);

		final boolean result = testObj.isIgnored("PROGRAM_1");

		assertThat(result).isFalse();
	}

}
