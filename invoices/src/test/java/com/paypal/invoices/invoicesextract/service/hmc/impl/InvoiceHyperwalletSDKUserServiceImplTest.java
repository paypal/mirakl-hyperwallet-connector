package com.paypal.invoices.invoicesextract.service.hmc.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.paypal.invoices.infraestructure.configuration.PaymentsHyperwalletApiConfig;
import com.paypal.invoices.invoicesextract.service.hyperwallet.impl.PaymentsHyperwalletSDKServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceHyperwalletSDKUserServiceImplTest {

	private static final String PROGRAM_TOKEN = "programToken";

	private static final String USER_NAME = "userName";

	private static final String PASSWORD = "password";

	private static final String SERVER = "server";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	private static final String API_CLIENT_HYPERWALLET_ENCRYPTION = "apiClient.hyperwalletEncryption";

	@InjectMocks
	private PaymentsHyperwalletSDKServiceImpl testObj;

	@Mock
	private PaymentsHyperwalletApiConfig paymentsHyperwalletApiConfigMock;

	@Test
	void getHyperwalletInstanceWithProgramToken_shouldReturnAnHyperwalletInstance() {
		when(paymentsHyperwalletApiConfigMock.getUsername()).thenReturn(USER_NAME);
		when(paymentsHyperwalletApiConfigMock.getPassword()).thenReturn(PASSWORD);
		when(paymentsHyperwalletApiConfigMock.getServer()).thenReturn(SERVER);

		final Hyperwallet result = testObj.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN);

		assertThat(result).hasFieldOrPropertyWithValue("programToken", PROGRAM_TOKEN)
				.hasFieldOrPropertyWithValue("apiClient.username", USER_NAME)
				.hasFieldOrPropertyWithValue("url", SERVER + "/rest/v4");
	}

	@Test
	void getHyperwalletInstanceByHyperwalletProgram_shouldReturnAnHyperwalletInstance() {
		when(paymentsHyperwalletApiConfigMock.getPaymentStoreTokens())
				.thenReturn(Map.of(HYPERWALLET_PROGRAM, PROGRAM_TOKEN));
		when(paymentsHyperwalletApiConfigMock.getUsername()).thenReturn(USER_NAME);
		when(paymentsHyperwalletApiConfigMock.getPassword()).thenReturn(PASSWORD);
		when(paymentsHyperwalletApiConfigMock.getServer()).thenReturn(SERVER);

		final Hyperwallet result = testObj.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM);

		assertThat(result).hasFieldOrPropertyWithValue("programToken", PROGRAM_TOKEN)
				.hasFieldOrPropertyWithValue("apiClient.username", USER_NAME)
				.hasFieldOrPropertyWithValue("url", SERVER + "/rest/v4");
	}

	@Test
	void getProgramTokenByHyperwalletProgram_shouldReturnAnHyperwalletInstance() {
		when(paymentsHyperwalletApiConfigMock.getPaymentStoreTokens())
				.thenReturn(Map.of(HYPERWALLET_PROGRAM, PROGRAM_TOKEN));

		final String result = testObj.getProgramTokenByHyperwalletProgram(HYPERWALLET_PROGRAM);

		assertThat(result).isEqualTo(PROGRAM_TOKEN);
	}

	@Test
	void getHyperwalletInstanceWithProgramToken_shouldReturnAnHyperwalletInstanceWithNOTEncryptedOption() {
		when(paymentsHyperwalletApiConfigMock.getUsername()).thenReturn(USER_NAME);
		when(paymentsHyperwalletApiConfigMock.getPassword()).thenReturn(PASSWORD);
		when(paymentsHyperwalletApiConfigMock.getServer()).thenReturn(SERVER);

		final Hyperwallet result = testObj.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN);

		assertThat(result).hasFieldOrPropertyWithValue(API_CLIENT_HYPERWALLET_ENCRYPTION, null);
	}

}
