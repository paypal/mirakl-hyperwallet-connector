package com.paypal.infrastructure.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HMCExceptionHandlerTest {

	private static final String EXCEPTION_MESSAGE = "Exception message";

	@InjectMocks
	private HMCExceptionHandler testObj;

	@Mock
	private MissingServletRequestParameterException missingServletRequestParameterExceptionMock;

	@Mock
	private MethodArgumentTypeMismatchException methodArgumentTypeMismatchExceptionMock;

	@Mock
	private HMCException hmcExceptionMock;

	@Test
	void handleHMCException_ShouldReturnABadRequestAndTheExceptionErrorMessage() {

		when(hmcExceptionMock.getMessage()).thenReturn(EXCEPTION_MESSAGE);

		final HMCErrorResponse result = testObj.handleHMCException(hmcExceptionMock);

		assertThat(result.getErrorMessage()).isEqualTo(EXCEPTION_MESSAGE);
	}

	@Test
	void handleMethodArgumentTypeMismatchException_ShouldReturnABadRequestAndABadRequestErrorMessage() {

		final HMCErrorResponse result = testObj
				.handleMethodArgumentTypeMismatchException(methodArgumentTypeMismatchExceptionMock);

		assertThat(result.getErrorMessage()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
	}

	@Test
	void handleMissingParametersException_ShouldReturnABadRequestAndABadRequestErrorMessage() {

		final HMCErrorResponse result = testObj
				.handleMissingRequestParametersException(missingServletRequestParameterExceptionMock);

		assertThat(result.getErrorMessage()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
	}

	@Test
	void handleUncontrolledException_ShouldReturnInternalServerErrorAndTheExceptionErrorMessage() {

		final HMCErrorResponse result = testObj.handleUncontrolledException(new Exception());

		assertThat(result.getErrorMessage()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
	}

}
