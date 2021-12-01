package com.paypal.infrastructure.util;

import com.mirakl.client.core.exception.MiraklException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiraklLoggingErrorsUtilTest {

	private static final String EXCEPTION_MESSAGE = "ExceptionMessage";

	@Mock
	private MiraklException miraklExceptionMock;

	@Test
	void stringify_shouldAddExceptionMessageAsJSON() {
		when(miraklExceptionMock.getMessage()).thenReturn(EXCEPTION_MESSAGE);

		final String result = MiraklLoggingErrorsUtil.stringify(miraklExceptionMock);

		assertThat(result).isEqualTo("{exceptionMessage=" + EXCEPTION_MESSAGE + ",}");
	}

}
