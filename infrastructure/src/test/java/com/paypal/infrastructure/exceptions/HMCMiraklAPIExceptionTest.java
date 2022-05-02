package com.paypal.infrastructure.exceptions;

import com.mirakl.client.core.exception.MiraklException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.paypal.infrastructure.exceptions.HMCMiraklAPIException.DEFAULT_MSG;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HMCMiraklAPIExceptionTest {

	private static final String HMC_MIRAKL_EXCEPTION_MESSAGE = "HMC Mirakl exception message";

	private static final String MIRAKL_EXCEPTION_FIELD = "miraklException";

	private static final String DETAIL_MESSAGE_FIELD = "detailMessage";

	@Mock
	private MiraklException miraklExceptionMock;

	@Test
	void hMCMiraklAPIException_ShouldPopulateMessageAndException() {

		final HMCMiraklAPIException hmcMiraklAPIException = new HMCMiraklAPIException(HMC_MIRAKL_EXCEPTION_MESSAGE,
				miraklExceptionMock);

		assertThat(hmcMiraklAPIException).hasFieldOrPropertyWithValue(MIRAKL_EXCEPTION_FIELD, miraklExceptionMock)
				.hasFieldOrPropertyWithValue(DETAIL_MESSAGE_FIELD, HMC_MIRAKL_EXCEPTION_MESSAGE);
	}

	@Test
	void hMCMiraklAPIException_ShouldPopulateDefaultMessageAndException() {

		final HMCMiraklAPIException hmcMiraklAPIException = new HMCMiraklAPIException(miraklExceptionMock);

		assertThat(hmcMiraklAPIException).hasFieldOrPropertyWithValue(MIRAKL_EXCEPTION_FIELD, miraklExceptionMock)
				.hasFieldOrPropertyWithValue(DETAIL_MESSAGE_FIELD, DEFAULT_MSG);
	}

	@Test
	void hMCMiraklAPIException_ShouldReturnException() {

		final HMCMiraklAPIException hmcMiraklAPIException = new HMCMiraklAPIException(miraklExceptionMock);

		assertThat(hmcMiraklAPIException.getMiraklException()).isEqualTo(miraklExceptionMock);
	}

}
