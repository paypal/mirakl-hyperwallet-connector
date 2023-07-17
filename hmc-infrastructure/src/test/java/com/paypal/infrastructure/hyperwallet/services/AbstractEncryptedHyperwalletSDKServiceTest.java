package com.paypal.infrastructure.hyperwallet.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletConnectionConfiguration;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractEncryptedHyperwalletSDKServiceTest {

	@InjectMocks
	private MyHyperwalletSDKService testObj;

	private static final String SERVER = "server";

	private static final String PASSWORD = "password";

	private static final String USER_NAME = "userName";

	private static final String PROGRAM_TOKEN = "programToken";

	@Mock
	private HyperwalletEncryption hyperwalletEncryptionMock;

	@Mock
	private HyperwalletConnectionConfiguration hyperwalletConnectionConfigurationMock;

	@Test
	void getHyperwalletInstance_shouldReturnAnHyperwalletInstanceWithEncryptedOption() {
		when(hyperwalletConnectionConfigurationMock.getUsername()).thenReturn(USER_NAME);
		when(hyperwalletConnectionConfigurationMock.getPassword()).thenReturn(PASSWORD);
		when(hyperwalletConnectionConfigurationMock.getServer()).thenReturn(SERVER);

		final Hyperwallet result = testObj.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN);

		assertThat(result).hasFieldOrPropertyWithValue("apiClient.hyperwalletEncryption", hyperwalletEncryptionMock);
	}

	static class MyHyperwalletSDKService extends AbstractHyperwalletSDKService {

		protected MyHyperwalletSDKService(final HyperwalletProgramsConfiguration programsConfiguration,
				final HyperwalletConnectionConfiguration connectionConfiguration,
				final HyperwalletEncryption encryption) {
			super(programsConfiguration, connectionConfiguration, encryption);
		}

		@Override
		protected String getProgramToken(
				final HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration programConfiguration) {
			return programConfiguration.getUsersProgramToken();
		}

	}

}
