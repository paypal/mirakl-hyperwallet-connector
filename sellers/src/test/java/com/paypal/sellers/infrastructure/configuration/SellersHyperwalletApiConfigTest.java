package com.paypal.sellers.infrastructure.configuration;

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
class SellersHyperwalletApiConfigTest {

	private static final String PREFIX = "sellers.hyperwallet.api.hyperwalletprogram.token.";

	@Spy
	@InjectMocks
	private SellersHyperwalletApiConfig testObj;

	@Mock
	private Environment environmentMock;

	@Test
	void loadSellerTokenMapConfiguration_whenConfigurationIsAvailableForTokensArchitecture()
			throws InvalidConfigurationException {
		testObj.loadSellerTokenMapConfiguration();

		verify(testObj).callSuperLoad(PREFIX);
	}

	@Test
	void loadSellerTokenMapConfiguration_whenConfigurationIsNotAvailableForTokensArchitecture()
			throws InvalidConfigurationException {

		testObj.loadSellerTokenMapConfiguration();

		assertThat(testObj.getUserStoreTokens()).isEmpty();
	}

	@Test
	void equals_shouldReturnTrueWhenBothAreEquals() {

		final SellersHyperwalletApiConfig sellerApiConfigOne = createUserApiConfig();
		final SellersHyperwalletApiConfig sellerApiConfigTwo = createUserApiConfig();

		final boolean result = sellerApiConfigOne.equals(sellerApiConfigTwo);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenBothAreNotEquals() {

		final SellersHyperwalletApiConfig sellerApiConfigOne = createUserApiConfig();
		final SellersHyperwalletApiConfig sellerApiConfigTwo = createSellerInvoiceApiConfig();

		final boolean result = sellerApiConfigOne.equals(sellerApiConfigTwo);

		assertThat(result).isFalse();
	}

	@Test
	void equals_shouldReturnTrueWhenSameObjectIsCompared() {

		final SellersHyperwalletApiConfig sellerApiConfigOne = createUserApiConfig();

		final boolean result = sellerApiConfigOne.equals(sellerApiConfigOne);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenComparedWithAnotherInstanceObject() {

		final SellersHyperwalletApiConfig sellerApiConfigOne = createUserApiConfig();

		final Object o = new Object();

		final boolean result = sellerApiConfigOne.equals(o);

		assertThat(result).isFalse();
	}

	private SellersHyperwalletApiConfig createUserApiConfig() {
		SellersHyperwalletApiConfig sellerHyperwalletApiConfig = new SellersHyperwalletApiConfig();
		sellerHyperwalletApiConfig.setEnvironment(environmentMock);
		sellerHyperwalletApiConfig.setHyperwalletPrograms("test1,test2,test3");
		sellerHyperwalletApiConfig.setPassword("password");
		sellerHyperwalletApiConfig.setServer("server");
		sellerHyperwalletApiConfig.setUsername("username");
		sellerHyperwalletApiConfig.setUserStoreTokens(Map.of("test1", "token1", "test2", "token2", "test3", "token3"));

		return sellerHyperwalletApiConfig;
	}

	private SellersHyperwalletApiConfig createSellerInvoiceApiConfig() {
		SellersHyperwalletApiConfig invoicesHyperwalletApiConfig = new SellersHyperwalletApiConfig();
		invoicesHyperwalletApiConfig.setEnvironment(environmentMock);
		invoicesHyperwalletApiConfig.setHyperwalletPrograms("test1,test2,test3");
		invoicesHyperwalletApiConfig.setPassword("password");
		invoicesHyperwalletApiConfig.setServer("server");
		invoicesHyperwalletApiConfig.setUsername("username");
		invoicesHyperwalletApiConfig.setUserStoreTokens(Map.of());

		return invoicesHyperwalletApiConfig;
	}

}
