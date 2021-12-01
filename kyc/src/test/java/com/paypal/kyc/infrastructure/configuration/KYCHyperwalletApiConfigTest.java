package com.paypal.kyc.infrastructure.configuration;

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
class KYCHyperwalletApiConfigTest {

	private static final String PREFIX = "kyc.hyperwallet.api.hyperwalletprogram.token.";

	@Spy
	@InjectMocks
	private KYCHyperwalletApiConfig testObj;

	@Mock
	private Environment environmentMock;

	@Test
	void loadKYCTokenMapConfiguration_whenConfigurationIsAvailableForTokensArchitecture()
			throws InvalidConfigurationException {

		testObj.loadKYCTokenMapConfiguration();

		verify(testObj).callSuperLoad(PREFIX);
	}

	@Test
	void loadKYCTokenMapConfiguration_whenConfigurationIsNotAvailableForTokensArchitecture()
			throws InvalidConfigurationException {

		testObj.loadKYCTokenMapConfiguration();

		assertThat(testObj.getUserStoreTokens()).isEmpty();
	}

	@Test
	void equals_shouldReturnTrueWhenBothAreEquals() {
		final KYCHyperwalletApiConfig kycApiConfigOne = createKycApiConfig();
		final KYCHyperwalletApiConfig kycApiConfigTwo = createKycApiConfig();

		final boolean result = kycApiConfigOne.equals(kycApiConfigTwo);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenBothAreNotEquals() {
		final KYCHyperwalletApiConfig kycApiConfigOne = createKycApiConfig();
		final KYCHyperwalletApiConfig kycApiConfigTwo = createAnotherKycApiConfig();

		final boolean result = kycApiConfigOne.equals(kycApiConfigTwo);

		assertThat(result).isFalse();
	}

	@Test
	void equals_shouldReturnTrueWhenSameObjectIsCompared() {
		final KYCHyperwalletApiConfig kycApiConfigOne = createKycApiConfig();

		final boolean result = kycApiConfigOne.equals(kycApiConfigOne);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenComparedWithAnotherInstanceObject() {
		final KYCHyperwalletApiConfig kycApiConfigOne = createKycApiConfig();

		final Object o = new Object();

		final boolean result = kycApiConfigOne.equals(o);

		assertThat(result).isFalse();
	}

	private KYCHyperwalletApiConfig createKycApiConfig() {
		KYCHyperwalletApiConfig kycApiConfigOne = new KYCHyperwalletApiConfig();
		kycApiConfigOne.setEnvironment(environmentMock);
		kycApiConfigOne.setHyperwalletPrograms("test1,test2,test3");
		kycApiConfigOne.setPassword("password");
		kycApiConfigOne.setServer("server");
		kycApiConfigOne.setUsername("username");
		kycApiConfigOne.setUserStoreTokens(Map.of("test1", "token1", "test2", "token2", "test3", "token3"));

		return kycApiConfigOne;
	}

	private KYCHyperwalletApiConfig createAnotherKycApiConfig() {
		KYCHyperwalletApiConfig kycApiConfigOne = new KYCHyperwalletApiConfig();
		kycApiConfigOne.setEnvironment(environmentMock);
		kycApiConfigOne.setHyperwalletPrograms("test1,test2,test3");
		kycApiConfigOne.setPassword("password");
		kycApiConfigOne.setServer("server");
		kycApiConfigOne.setUsername("username");
		kycApiConfigOne.setUserStoreTokens(Map.of());

		return kycApiConfigOne;
	}

}
