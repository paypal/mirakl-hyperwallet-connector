package com.paypal.invoices.infraestructure.configuration;

import com.paypal.infrastructure.exceptions.InvalidConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InvoicesHyperwalletApiConfigTest {

	private static final String PREFIX = "invoices.hyperwallet.api.hyperwalletprogram.token.";

	@Spy
	@InjectMocks
	private InvoicesHyperwalletApiConfig testObj;

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
		final InvoicesHyperwalletApiConfig invoiceApiConfigOne = createInvoiceApiConfig();
		final InvoicesHyperwalletApiConfig invoiceApiConfigTwo = createInvoiceApiConfig();

		final boolean result = invoiceApiConfigOne.equals(invoiceApiConfigTwo);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenBothAreNotEquals() {
		final InvoicesHyperwalletApiConfig invoiceApiConfigOne = createInvoiceApiConfig();
		final InvoicesHyperwalletApiConfig invoiceApiConfigTwo = createAnotherInvoiceApiConfig();

		final boolean result = invoiceApiConfigOne.equals(invoiceApiConfigTwo);

		assertThat(result).isFalse();
	}

	@Test
	void equals_shouldReturnTrueWhenSameObjectIsCompared() {
		final InvoicesHyperwalletApiConfig invoiceApiConfigOne = createInvoiceApiConfig();

		final boolean result = invoiceApiConfigOne.equals(invoiceApiConfigOne);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenComparedWithAnotherInstanceObject() {
		final InvoicesHyperwalletApiConfig invoiceApiConfigOne = createInvoiceApiConfig();

		final Object o = new Object();

		final boolean result = invoiceApiConfigOne.equals(o);

		assertThat(result).isFalse();
	}

	private InvoicesHyperwalletApiConfig createInvoiceApiConfig() {
		InvoicesHyperwalletApiConfig invoicesHyperwalletApiConfig = new InvoicesHyperwalletApiConfig();
		invoicesHyperwalletApiConfig.setEnvironment(environmentMock);
		invoicesHyperwalletApiConfig.setHyperwalletPrograms("test1,test2,test3");
		invoicesHyperwalletApiConfig.setPassword("password");
		invoicesHyperwalletApiConfig.setServer("server");
		invoicesHyperwalletApiConfig.setUsername("username");
		invoicesHyperwalletApiConfig
				.setPaymentStoreTokens(Map.of("test1", "token1", "test2", "token2", "test3", "token3"));

		return invoicesHyperwalletApiConfig;
	}

	private InvoicesHyperwalletApiConfig createAnotherInvoiceApiConfig() {
		InvoicesHyperwalletApiConfig invoicesHyperwalletApiConfig = new InvoicesHyperwalletApiConfig();
		invoicesHyperwalletApiConfig.setEnvironment(environmentMock);
		invoicesHyperwalletApiConfig.setHyperwalletPrograms("test1,test2,test3");
		invoicesHyperwalletApiConfig.setPassword("password");
		invoicesHyperwalletApiConfig.setServer("server");
		invoicesHyperwalletApiConfig.setUsername("username");
		invoicesHyperwalletApiConfig.setPaymentStoreTokens(Map.of());

		return invoicesHyperwalletApiConfig;
	}

}
