package com.paypal.infrastructure.support.exceptions;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException.DEFAULT_MSG;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HMCHyperwalletAPIExceptionTest {

	private static final String HMC_HYPERWALLET_EXCEPTION_MESSAGE = "HMC Mirakl exception message";

	private static final String HYPERWALLET_EXCEPTION_FIELD = "hyperwalletException";

	private static final String DETAIL_MESSAGE_FIELD = "detailMessage";

	@Mock
	private HyperwalletException hyperwalletExceptionMock;

	@Test
	void hMCHyperwalletAPIException_ShouldPopulateMessageAndException() {

		final HMCHyperwalletAPIException hmcHyperwalletAPIException = new HMCHyperwalletAPIException(
				HMC_HYPERWALLET_EXCEPTION_MESSAGE, hyperwalletExceptionMock);

		assertThat(hmcHyperwalletAPIException)
				.hasFieldOrPropertyWithValue(HYPERWALLET_EXCEPTION_FIELD, hyperwalletExceptionMock)
				.hasFieldOrPropertyWithValue(DETAIL_MESSAGE_FIELD, HMC_HYPERWALLET_EXCEPTION_MESSAGE);
	}

	@Test
	void hMCHyperwalletAPIException_ShouldPopulateDefaultMessageAndException() {

		final HMCHyperwalletAPIException hmcHyperwalletAPIException = new HMCHyperwalletAPIException(
				hyperwalletExceptionMock);

		assertThat(hmcHyperwalletAPIException)
				.hasFieldOrPropertyWithValue(HYPERWALLET_EXCEPTION_FIELD, hyperwalletExceptionMock)
				.hasFieldOrPropertyWithValue(DETAIL_MESSAGE_FIELD, DEFAULT_MSG);
	}

	@Test
	void hMCHyperwalletAPIException_ShouldReturnException() {

		final HMCHyperwalletAPIException hmcHyperwalletAPIException = new HMCHyperwalletAPIException(
				hyperwalletExceptionMock);

		assertThat(hmcHyperwalletAPIException.getHyperwalletException()).isEqualTo(hyperwalletExceptionMock);
	}

}
