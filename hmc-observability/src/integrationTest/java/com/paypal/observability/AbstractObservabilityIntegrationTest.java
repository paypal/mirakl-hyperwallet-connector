package com.paypal.observability;

import com.paypal.observability.testsupport.fixtures.AdditionalFieldsMockServerFixtures;
import com.paypal.observability.testsupport.fixtures.DocsMockServerFixtures;
import com.paypal.observability.testsupport.fixtures.HealthMockServerFixtures;
import com.paypal.observability.testsupport.fixtures.HyperwalletHealthMockServerFixtures;
import com.paypal.testsupport.AbstractMockEnabledIntegrationTest;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractObservabilityIntegrationTest extends AbstractMockEnabledIntegrationTest {

	protected AdditionalFieldsMockServerFixtures additionalFieldsMockServerFixtures;

	protected DocsMockServerFixtures docsMockServerFixtures;

	protected HealthMockServerFixtures healthMockServerFixtures;

	protected HyperwalletHealthMockServerFixtures hyperwalletHealthMockServerFixtures;

	@BeforeEach
	void setUpFixtures() {
		additionalFieldsMockServerFixtures = new AdditionalFieldsMockServerFixtures(mockServerClient);
		docsMockServerFixtures = new DocsMockServerFixtures(mockServerClient);
		healthMockServerFixtures = new HealthMockServerFixtures(mockServerClient);
		hyperwalletHealthMockServerFixtures = new HyperwalletHealthMockServerFixtures(mockServerClient);
	}

}
