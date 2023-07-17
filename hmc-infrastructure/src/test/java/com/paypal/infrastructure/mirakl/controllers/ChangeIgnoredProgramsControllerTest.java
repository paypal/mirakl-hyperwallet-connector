package com.paypal.infrastructure.mirakl.controllers;

import com.paypal.infrastructure.mirakl.controllers.dto.IgnoredProgramsDTO;
import com.paypal.infrastructure.mirakl.services.IgnoreProgramsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeIgnoredProgramsControllerTest {

	@InjectMocks
	private ChangeIgnoredProgramsController testObj;

	@Mock
	private IgnoreProgramsService ignoreProgramsServiceMock;

	@Mock
	private IgnoredProgramsDTO ignoredProgramsDTO;

	@Test
	void ignore_shouldReplacedIgnoredProgramList_andReturnOk() {
		when(ignoredProgramsDTO.getIgnoredPrograms()).thenReturn(List.of("A", "B"));
		final ResponseEntity<String> result = testObj.ignore(ignoredProgramsDTO);

		verify(ignoreProgramsServiceMock).ignorePrograms(List.of("A", "B"));
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void get_shouldReturnIgnoredPrograms() {
		when(ignoreProgramsServiceMock.getIgnoredPrograms()).thenReturn(List.of("A", "B"));
		final ResponseEntity<Object> result = testObj.get();

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(List.of("A", "B"), result.getBody());
	}

}
