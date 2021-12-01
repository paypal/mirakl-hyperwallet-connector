package com.paypal.kyc.service.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.paypal.kyc.infrastructure.configuration.KYCHyperwalletApiConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KYCHyperwalletSDKServiceImplTest {

	private static final String PROGRAM_TOKEN = "programToken";

	private static final String USER_NAME = "userName";

	private static final String PASSWORD = "password";

	private static final String SERVER = "server";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	@Spy
	@InjectMocks
	private KYCHyperwalletSDKServiceImpl testObj;

	@Mock
	private KYCHyperwalletApiConfig kycHyperwalletApiConfigMock;

	@Test
	void getHyperwalletInstance_shouldReturnAnHyperwalletInstance() {
		when(kycHyperwalletApiConfigMock.getUserStoreTokens()).thenReturn(Map.of(HYPERWALLET_PROGRAM, PROGRAM_TOKEN));
		when(kycHyperwalletApiConfigMock.getUsername()).thenReturn(USER_NAME);
		when(kycHyperwalletApiConfigMock.getPassword()).thenReturn(PASSWORD);
		when(kycHyperwalletApiConfigMock.getServer()).thenReturn(SERVER);

		final Hyperwallet result = testObj.getHyperwalletInstance(HYPERWALLET_PROGRAM);

		assertThat(result).hasFieldOrPropertyWithValue("programToken", PROGRAM_TOKEN)
				.hasFieldOrPropertyWithValue("apiClient.username", USER_NAME)
				.hasFieldOrPropertyWithValue("url", SERVER + "/rest/v4");
	}

	@Test
	void getHyperwalletInstance_shouldReturnAnHyperwalletInstanceWithNOTEncryptedOption() {
		when(kycHyperwalletApiConfigMock.getUsername()).thenReturn(USER_NAME);
		when(kycHyperwalletApiConfigMock.getPassword()).thenReturn(PASSWORD);
		when(kycHyperwalletApiConfigMock.getServer()).thenReturn(SERVER);

		final Hyperwallet result = testObj.getHyperwalletInstance(PROGRAM_TOKEN);

		assertThat(result).hasFieldOrPropertyWithValue("apiClient.hyperwalletEncryption", null);
	}

}
