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
class UserHyperwalletSDKServiceImplTest {

	public static final String PROGRAM_TOKEN = "TOKEN";

	@InjectMocks
	private UserHyperwalletSDKServiceImpl testObj;

	@Mock
	private HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration hyperwalletProgramConfigurationMock;

	@Test
	void getProgramToken_shouldReturnUserProgramToken() {
		when(hyperwalletProgramConfigurationMock.getUsersProgramToken()).thenReturn(PROGRAM_TOKEN);

		final String result = testObj.getProgramToken(hyperwalletProgramConfigurationMock);

		assertThat(result).isEqualTo(PROGRAM_TOKEN);
	}

}
