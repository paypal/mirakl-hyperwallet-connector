package com.paypal.infrastructure.hyperwallet.api;

import com.paypal.infrastructure.exceptions.InvalidConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentsHyperwalletApiConfigTest {

	private static final String PREFIX = "payment.hyperwallet.api.hyperwalletprogram.token.";

	@Spy
	@InjectMocks
	private PaymentsHyperwalletApiConfig testObj;

	@Mock
	private Environment environmentMock;

	@Test
	void loadTokenMapConfiguration_whenConfigurationIsAvailableForTokensArchitecture()
			throws InvalidConfigurationException {
		testObj.loadInvoicesTokenMapConfiguration();

		verify(testObj).callSuperLoad(PREFIX);
	}

	@Test
	void loadTokenMapConfiguration_whenConfigurationIsNotAvailableForTokensArchitecture()
			throws InvalidConfigurationException {
		testObj.loadInvoicesTokenMapConfiguration();

		assertThat(testObj.getPaymentStoreTokens()).isEmpty();
	}

	@Test
	void equals_shouldReturnTrueWhenBothAreEquals() {
		final PaymentsHyperwalletApiConfig invoiceApiConfigOne = createInvoiceApiConfig();
		final PaymentsHyperwalletApiConfig invoiceApiConfigTwo = createInvoiceApiConfig();

		final boolean result = invoiceApiConfigOne.equals(invoiceApiConfigTwo);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenBothAreNotEquals() {
		final PaymentsHyperwalletApiConfig invoiceApiConfigOne = createInvoiceApiConfig();
		final PaymentsHyperwalletApiConfig invoiceApiConfigTwo = createAnotherInvoiceApiConfig();

		final boolean result = invoiceApiConfigOne.equals(invoiceApiConfigTwo);

		assertThat(result).isFalse();
	}

	@Test
	void equals_shouldReturnTrueWhenSameObjectIsCompared() {
		final PaymentsHyperwalletApiConfig invoiceApiConfigOne = createInvoiceApiConfig();

		final boolean result = invoiceApiConfigOne.equals(invoiceApiConfigOne);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenComparedWithAnotherInstanceObject() {
		final PaymentsHyperwalletApiConfig invoiceApiConfigOne = createInvoiceApiConfig();

		final Object o = new Object();

		final boolean result = invoiceApiConfigOne.equals(o);

		assertThat(result).isFalse();
	}

	private PaymentsHyperwalletApiConfig createInvoiceApiConfig() {
		final PaymentsHyperwalletApiConfig paymentsHyperwalletApiConfig = new PaymentsHyperwalletApiConfig();
		paymentsHyperwalletApiConfig.setEnvironment(environmentMock);
		paymentsHyperwalletApiConfig.setHyperwalletPrograms(List.of("test1", "test2", "test3"));
		paymentsHyperwalletApiConfig.setPassword("password");
		paymentsHyperwalletApiConfig.setServer("server");
		paymentsHyperwalletApiConfig.setUsername("username");
		paymentsHyperwalletApiConfig
				.setPaymentStoreTokens(Map.of("test1", "token1", "test2", "token2", "test3", "token3"));

		return paymentsHyperwalletApiConfig;
	}

	private PaymentsHyperwalletApiConfig createAnotherInvoiceApiConfig() {
		final PaymentsHyperwalletApiConfig paymentsHyperwalletApiConfig = new PaymentsHyperwalletApiConfig();
		paymentsHyperwalletApiConfig.setEnvironment(environmentMock);
		paymentsHyperwalletApiConfig.setHyperwalletPrograms(List.of("test1", "test2", "test3"));
		paymentsHyperwalletApiConfig.setPassword("password");
		paymentsHyperwalletApiConfig.setServer("server");
		paymentsHyperwalletApiConfig.setUsername("username");
		paymentsHyperwalletApiConfig.setPaymentStoreTokens(Map.of());

		return paymentsHyperwalletApiConfig;
	}

}
