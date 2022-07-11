package com.paypal.observability.testsupport;

import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;

public abstract class AbstractMockServerITTest {

	protected MockServerClient mockServerClient;

	protected AdditionalFieldsMockServerFixtures additionalFieldsMockServerFixtures;

	protected DocsMockServerFixtures docsMockServerFixtures;

	protected HealthMockServerFixtures healthMockServerFixtures;

	@BeforeEach
	void setUpFixtures() {
		additionalFieldsMockServerFixtures = new AdditionalFieldsMockServerFixtures(mockServerClient);
		docsMockServerFixtures = new DocsMockServerFixtures(mockServerClient);
		healthMockServerFixtures = new HealthMockServerFixtures(mockServerClient);
	}

}
