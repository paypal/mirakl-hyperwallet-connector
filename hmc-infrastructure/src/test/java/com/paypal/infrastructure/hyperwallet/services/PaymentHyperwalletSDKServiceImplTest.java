package com.paypal.infrastructure.hyperwallet.services;

import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentHyperwalletSDKServiceImplTest {

	public static final String PROGRAM_TOKEN = "TOKEN";

	@InjectMocks
	private PaymentHyperwalletSDKServiceImpl testObj;

	@Mock
	private HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration hyperwalletProgramConfigurationMock;

	@Test
	void getProgramToken_shouldReturnPaymentProgramToken() {
		when(hyperwalletProgramConfigurationMock.getPaymentProgramToken()).thenReturn(PROGRAM_TOKEN);

		final String result = testObj.getProgramToken(hyperwalletProgramConfigurationMock);

		assertThat(result).isEqualTo(PROGRAM_TOKEN);
	}

}
