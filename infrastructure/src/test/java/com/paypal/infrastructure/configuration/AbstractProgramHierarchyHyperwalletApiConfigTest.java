package com.paypal.infrastructure.configuration;

import com.paypal.infrastructure.exceptions.InvalidConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractProgramHierarchyHyperwalletApiConfigTest {

	private static final String PREFIX_TOKEN = "prefixToken.";

	private static final String TOKEN_TEST_1 = "tokenTest1";

	private static final String TOKEN_TEST_2 = "tokenTest2";

	private static final String TOKEN_TEST_3 = "tokenTest3";

	private static final String TEST_1 = "TEST1";

	private static final String TEST_2 = "TEST2";

	private static final String TEST_3 = "TEST3";

	private AbstractProgramHierarchyHyperwalletApiConfig testObj;

	@Mock
	private Environment environmentMock;

	@BeforeEach
	void setUp() {
		testObj = new MyAbstractProgramHierarchyHyperwalletApiConfig();
		testObj.setEnvironment(environmentMock);
		testObj.setHyperwalletPrograms(List.of("TEST1", "TEST2", "TEST3"));
	}

	@Test
	void loadTokenMapConfiguration_shouldLoadTokensMap() throws InvalidConfigurationException {
		when(environmentMock.getProperty(PREFIX_TOKEN + TEST_1)).thenReturn(TOKEN_TEST_1);
		when(environmentMock.getProperty(PREFIX_TOKEN + TEST_2)).thenReturn(TOKEN_TEST_2);
		when(environmentMock.getProperty(PREFIX_TOKEN + TEST_3)).thenReturn(TOKEN_TEST_3);

		final Map<String, String> result = testObj.loadTokenMapConfiguration(PREFIX_TOKEN);

		assertThat(result).containsOnly(entry(TEST_1, TOKEN_TEST_1), entry(TEST_2, TOKEN_TEST_2),
				entry(TEST_3, TOKEN_TEST_3));
	}

	@Test
	void loadTokenMapConfiguration_shouldEmptyMapWhenHyperwalletProgramsParameterConfigurationIsNotSet()
			throws InvalidConfigurationException {
		testObj.setHyperwalletPrograms(null);
		final Map<String, String> result = testObj.loadTokenMapConfiguration(PREFIX_TOKEN);

		assertThat(result).isEmpty();
	}

	@Test
	void equals_shouldReturnTrueWhenBothAreEquals() {
		final MyAbstractProgramHierarchyHyperwalletApiConfig apiConfigOne = createApiConfig();
		final MyAbstractProgramHierarchyHyperwalletApiConfig apiConfigTwo = createApiConfig();

		final boolean result = apiConfigOne.equals(apiConfigTwo);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenBothAreNotEquals() {
		final MyAbstractProgramHierarchyHyperwalletApiConfig apiConfigOne = createApiConfig();
		final MyAbstractProgramHierarchyHyperwalletApiConfig apiConfigTwo = createAnotherApiConfig();

		final boolean result = apiConfigOne.equals(apiConfigTwo);

		assertThat(result).isFalse();
	}

	@Test
	void equals_shouldReturnTrueWhenSameObjectIsCompared() {
		final MyAbstractProgramHierarchyHyperwalletApiConfig apiConfigOne = createApiConfig();

		final boolean result = apiConfigOne.equals(apiConfigOne);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenComparedWithAnotherInstanceObject() {
		final MyAbstractProgramHierarchyHyperwalletApiConfig apiConfigOne = createApiConfig();

		final Object o = new Object();

		final boolean result = apiConfigOne.equals(o);

		assertThat(result).isFalse();
	}

	private MyAbstractProgramHierarchyHyperwalletApiConfig createApiConfig() {
		final MyAbstractProgramHierarchyHyperwalletApiConfig apiConfigOne = new MyAbstractProgramHierarchyHyperwalletApiConfig();
		apiConfigOne.setHyperwalletPrograms(List.of("test1", "test2", "test3"));

		return apiConfigOne;
	}

	private MyAbstractProgramHierarchyHyperwalletApiConfig createAnotherApiConfig() {
		final MyAbstractProgramHierarchyHyperwalletApiConfig apiConfigOne = new MyAbstractProgramHierarchyHyperwalletApiConfig();
		apiConfigOne.setHyperwalletPrograms(List.of("test1", "test2", "test4"));

		return apiConfigOne;
	}

	private static class MyAbstractProgramHierarchyHyperwalletApiConfig
			extends AbstractProgramHierarchyHyperwalletApiConfig {

	}

}
