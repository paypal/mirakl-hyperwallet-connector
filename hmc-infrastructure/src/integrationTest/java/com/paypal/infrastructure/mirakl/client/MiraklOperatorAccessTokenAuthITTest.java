package com.paypal.infrastructure.mirakl.client;

import com.mirakl.client.mmp.domain.version.MiraklVersion;
import com.paypal.infrastructure.mirakl.configuration.MiraklApiClientConfig;
import com.paypal.testsupport.AbstractMockEnabledIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;

@Transactional
class MiraklOperatorAccessTokenAuthITTest extends AbstractMockEnabledIntegrationTest {

	private static final String TEST_ACCESS_TOKEN = "test-access-token-12345";

	private static final String TEST_OPERATOR_API_KEY = "OPERATOR-KEY";

	@Autowired
	private MiraklClient miraklClient;

	@Autowired
	private MiraklApiClientConfig miraklApiClientConfig;

	@AfterEach
	void tearDown() {
		// Restore original operator API key so other tests are not affected
		miraklApiClientConfig.setOperatorAccessToken(null);
		miraklApiClientConfig.setOperatorApiKey(TEST_OPERATOR_API_KEY);
		miraklClient.reloadHttpConfiguration();
	}

	@Test
	void shouldSendBearerAuthorizationHeader_whenOperatorAccessTokenIsConfigured() {
		// given — switch to access token authentication
		miraklApiClientConfig.setOperatorApiKey(null);
		miraklApiClientConfig.setOperatorAccessToken(TEST_ACCESS_TOKEN);
		miraklClient.reloadHttpConfiguration();

		mockServerExpectationsLoader.loadExpectationsFromFolder("mocks/testsets/miraklauth", "mirakl-version",
				Map.of());

		// when
		final MiraklVersion version = miraklClient.getVersion();

		// then — MockServer received the request with Authorization: Bearer <token>
		assertThat(version).isNotNull();
		mockServerClient.verify(request().withMethod("GET")
			.withPath("/api/version")
			.withHeader("Authorization", "Bearer " + TEST_ACCESS_TOKEN), VerificationTimes.exactly(1));
	}

	@Test
	void shouldSendApiKeyAuthorizationHeader_whenOnlyOperatorApiKeyIsConfigured() {
		// given — legacy API key authentication (default in test properties)
		miraklApiClientConfig.setOperatorAccessToken(null);
		miraklApiClientConfig.setOperatorApiKey(TEST_OPERATOR_API_KEY);
		miraklClient.reloadHttpConfiguration();

		mockServerExpectationsLoader.loadExpectationsFromFolder("mocks/testsets/miraklauth", "mirakl-version",
				Map.of());

		// when
		final MiraklVersion version = miraklClient.getVersion();

		// then — MockServer received the request with Authorization: <api-key> (no Bearer
		// prefix)
		assertThat(version).isNotNull();
		mockServerClient.verify(
				request().withMethod("GET").withPath("/api/version").withHeader("Authorization", TEST_OPERATOR_API_KEY),
				VerificationTimes.exactly(1));
	}

	@Test
	void shouldSendBearerAuthorizationHeader_whenBothOperatorAccessTokenAndApiKeyAreConfigured() {
		// given — both credentials set; access token must take precedence
		miraklApiClientConfig.setOperatorApiKey(TEST_OPERATOR_API_KEY);
		miraklApiClientConfig.setOperatorAccessToken(TEST_ACCESS_TOKEN);
		miraklClient.reloadHttpConfiguration();

		mockServerExpectationsLoader.loadExpectationsFromFolder("mocks/testsets/miraklauth", "mirakl-version",
				Map.of());

		// when
		final MiraklVersion version = miraklClient.getVersion();

		// then — Bearer token takes precedence over the legacy API key
		assertThat(version).isNotNull();
		mockServerClient.verify(request().withMethod("GET")
			.withPath("/api/version")
			.withHeader("Authorization", "Bearer " + TEST_ACCESS_TOKEN), VerificationTimes.exactly(1));
		mockServerClient.verify(
				request().withMethod("GET").withPath("/api/version").withHeader("Authorization", TEST_OPERATOR_API_KEY),
				VerificationTimes.exactly(0));
	}

}
