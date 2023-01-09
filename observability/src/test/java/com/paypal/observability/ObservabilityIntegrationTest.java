package com.paypal.observability;

import com.paypal.infrastructure.test.InfrastructureIntegrationTest;
import com.paypal.observability.testsupport.fixtures.AdditionalFieldsMockServerFixtures;
import com.paypal.observability.testsupport.fixtures.DocsMockServerFixtures;
import com.paypal.observability.testsupport.fixtures.HealthMockServerFixtures;
import com.paypal.observability.testsupport.fixtures.HyperwalletHealthMockServerFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;

@AutoConfigureMockMvc
@EnableAutoConfiguration
@ComponentScan
public abstract class ObservabilityIntegrationTest extends InfrastructureIntegrationTest {

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
