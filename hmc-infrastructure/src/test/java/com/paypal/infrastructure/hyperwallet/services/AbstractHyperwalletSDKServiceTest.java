package com.paypal.infrastructure.hyperwallet.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletConnectionConfiguration;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractHyperwalletSDKServiceTest {

	@InjectMocks
	private MyHyperwalletSDKService testObj;

	private static final String SERVER = "server";

	private static final String PASSWORD = "password";

	private static final String USER_NAME = "userName";

	private static final String PROGRAM_TOKEN = "programToken";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	@Mock
	private HyperwalletConnectionConfiguration hyperwalletConnectionConfigurationMock;

	@Mock
	private HyperwalletProgramsConfiguration hyperwalletProgramsConfigurationMock;

	@Mock
	private HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration hyperwalletProgramConfigurationMock;

	@Test
	void getHyperwalletInstance_shouldReturnAnHyperwalletInstance() {
		when(hyperwalletProgramsConfigurationMock.getProgramConfiguration(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletProgramConfigurationMock);
		when(hyperwalletProgramConfigurationMock.getUsersProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletConnectionConfigurationMock.getUsername()).thenReturn(USER_NAME);
		when(hyperwalletConnectionConfigurationMock.getPassword()).thenReturn(PASSWORD);
		when(hyperwalletConnectionConfigurationMock.getServer()).thenReturn(SERVER);

		final Hyperwallet result = testObj.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM);

		assertThat(result).hasFieldOrPropertyWithValue("programToken", PROGRAM_TOKEN)
				.hasFieldOrPropertyWithValue("apiClient.username", USER_NAME)
				.hasFieldOrPropertyWithValue("url", SERVER + "/rest/v4");
	}

	@Test
	void getHyperwalletInstance_shouldReturnAnHyperwalletInstanceWithNOTEncryptedOption() {
		when(hyperwalletConnectionConfigurationMock.getUsername()).thenReturn(USER_NAME);
		when(hyperwalletConnectionConfigurationMock.getPassword()).thenReturn(PASSWORD);
		when(hyperwalletConnectionConfigurationMock.getServer()).thenReturn(SERVER);

		final Hyperwallet result = testObj.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN);

		assertThat(result).hasFieldOrPropertyWithValue("apiClient.hyperwalletEncryption", null);
	}

	@Test
	void getHyperwalletInstanceWithProgramToken_shouldReturnAnHyperwalletInstance() {
		when(hyperwalletConnectionConfigurationMock.getUsername()).thenReturn(USER_NAME);
		when(hyperwalletConnectionConfigurationMock.getPassword()).thenReturn(PASSWORD);
		when(hyperwalletConnectionConfigurationMock.getServer()).thenReturn(SERVER);

		final Hyperwallet result = testObj.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN);

		assertThat(result).hasFieldOrPropertyWithValue("programToken", PROGRAM_TOKEN)
				.hasFieldOrPropertyWithValue("apiClient.username", USER_NAME)
				.hasFieldOrPropertyWithValue("url", SERVER + "/rest/v4");
	}

	@Test
	void getHyperwalletInstance_shouldReturnAnHyperwalletInstanceWithoutSettingToken() {
		when(hyperwalletConnectionConfigurationMock.getUsername()).thenReturn(USER_NAME);
		when(hyperwalletConnectionConfigurationMock.getPassword()).thenReturn(PASSWORD);
		when(hyperwalletConnectionConfigurationMock.getServer()).thenReturn(SERVER);

		final Hyperwallet result = testObj.getHyperwalletInstance();

		assertThat(result).hasFieldOrPropertyWithValue("programToken", null)
				.hasFieldOrPropertyWithValue("apiClient.username", USER_NAME)
				.hasFieldOrPropertyWithValue("url", SERVER + "/rest/v4");
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
