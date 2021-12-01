package com.paypal.kyc.service.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.paypal.kyc.infrastructure.configuration.KYCHyperwalletApiConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KYCHyperwalletSDKEncryptedServiceImplTest {

	private static final String SERVER = "server";

	private static final String PASSWORD = "password";

	private static final String USER_NAME = "userName";

	private static final String PROGRAM_TOKEN = "programToken";

	@InjectMocks
	private KYCHyperwalletSDKEncryptedServiceImpl testObj;

	@Mock
	private KYCHyperwalletApiConfig kycHyperwalletApiConfigMock;

	@Mock
	private HyperwalletEncryption hyperwalletEncryptionMock;

	@Test
	void getHyperwalletInstance_shouldReturnAnHyperwalletInstanceWithEncryptedOption() {
		when(kycHyperwalletApiConfigMock.getUsername()).thenReturn(USER_NAME);
		when(kycHyperwalletApiConfigMock.getPassword()).thenReturn(PASSWORD);
		when(kycHyperwalletApiConfigMock.getServer()).thenReturn(SERVER);

		final Hyperwallet result = testObj.getHyperwalletInstance(PROGRAM_TOKEN);

		assertThat(result).hasFieldOrPropertyWithValue("apiClient.hyperwalletEncryption", hyperwalletEncryptionMock);
	}

}
