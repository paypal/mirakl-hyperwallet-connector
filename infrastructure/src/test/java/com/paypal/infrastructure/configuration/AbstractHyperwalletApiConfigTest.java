package com.paypal.infrastructure.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractHyperwalletApiConfigTest {

	@Test
	void equals_shouldReturnTrueWhenBothAreEquals() {

		final MyAbstractHyperwalletApiConfig apiConfigOne = createApiConfig();
		final MyAbstractHyperwalletApiConfig apiConfigTwo = createApiConfig();

		final boolean result = apiConfigOne.equals(apiConfigTwo);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenBothAreNotEquals() {

		final MyAbstractHyperwalletApiConfig apiConfigOne = createApiConfig();
		final MyAbstractHyperwalletApiConfig apiConfigTwo = createAnotherApiConfig();

		final boolean result = apiConfigOne.equals(apiConfigTwo);

		assertThat(result).isFalse();
	}

	@Test
	void equals_shouldReturnTrueWhenSameObjectIsCompared() {

		final MyAbstractHyperwalletApiConfig apiConfigOne = createApiConfig();

		final boolean result = apiConfigOne.equals(apiConfigOne);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenComparedWithAnotherInstanceObject() {

		final MyAbstractHyperwalletApiConfig apiConfigOne = createApiConfig();

		final Object o = new Object();

		final boolean result = apiConfigOne.equals(o);

		assertThat(result).isFalse();
	}

	private MyAbstractHyperwalletApiConfig createApiConfig() {
		MyAbstractHyperwalletApiConfig apiConfigOne = new MyAbstractHyperwalletApiConfig();
		apiConfigOne.setServer("server");
		apiConfigOne.setUsername("username");
		apiConfigOne.setPassword("password");

		return apiConfigOne;
	}

	private MyAbstractHyperwalletApiConfig createAnotherApiConfig() {
		MyAbstractHyperwalletApiConfig apiConfigOne = new MyAbstractHyperwalletApiConfig();
		apiConfigOne.setServer("anotherServer");
		apiConfigOne.setUsername("username");
		apiConfigOne.setPassword("password");

		return apiConfigOne;
	}

	private static class MyAbstractHyperwalletApiConfig extends AbstractHyperwalletApiConfig {

	}

}
