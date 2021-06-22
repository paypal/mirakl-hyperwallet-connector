package com.paypal.sellers.service.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.paypal.sellers.infrastructure.configuration.SellersHyperwalletApiConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerHyperwalletSDKEncryptedServiceImplTest {

	private static final String PROGRAM_TOKEN = "programToken";

	private static final String USER_NAME = "userName";

	private static final String PASSWORD = "password";

	private static final String SERVER = "server";

	@InjectMocks
	private SellerHyperwalletSDKEncryptedServiceImpl testObj;

	@Mock
	private HyperwalletEncryption hyperwalletEncryptionMock;

	@Mock
	private SellersHyperwalletApiConfig sellersHyperwalletApiConfigMock;

	@Test
	void getHyperwalletInstance_shouldReturnAnHyperwalletInstanceWithEncryptedOption() {
		when(this.sellersHyperwalletApiConfigMock.getUsername()).thenReturn(USER_NAME);
		when(this.sellersHyperwalletApiConfigMock.getPassword()).thenReturn(PASSWORD);
		when(this.sellersHyperwalletApiConfigMock.getServer()).thenReturn(SERVER);

		final Hyperwallet result = this.testObj.getHyperwalletInstance(PROGRAM_TOKEN);

		assertThat(result).hasFieldOrPropertyWithValue("apiClient.hyperwalletEncryption",
				this.hyperwalletEncryptionMock);

	}

}