package com.paypal.infrastructure.hyperwallet.api;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultHyperwalletSDKEncryptedServiceTest {

	private static final String SERVER = "server";

	private static final String PASSWORD = "password";

	private static final String USER_NAME = "userName";

	private static final String PROGRAM_TOKEN = "programToken";

	@InjectMocks
	private DefaultHyperwalletSDKUserService testObj;

	@Mock
	private HyperwalletEncryption hyperwalletEncryptionMock;

	@Mock
	private UserHyperwalletApiConfig notificationsHyperwalletApiConfigMock;

	@Test
	void getHyperwalletInstance_shouldReturnAnHyperwalletInstanceWithEncryptedOption() {
		when(notificationsHyperwalletApiConfigMock.getUsername()).thenReturn(USER_NAME);
		when(notificationsHyperwalletApiConfigMock.getPassword()).thenReturn(PASSWORD);
		when(notificationsHyperwalletApiConfigMock.getServer()).thenReturn(SERVER);

		final Hyperwallet result = testObj.getHyperwalletInstanceByHyperwalletProgram(PROGRAM_TOKEN);

		assertThat(result).hasFieldOrPropertyWithValue("apiClient.hyperwalletEncryption", hyperwalletEncryptionMock);
	}

}
