package com.paypal.observability.hyperwalletapichecks;

import com.paypal.observability.AbstractObservabilityIntegrationTest;
import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;
import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheckStatus;
import com.paypal.observability.hyperwalletapichecks.services.HyperwalletHealthCheckService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class HyperwalletAPIChecksITTest extends AbstractObservabilityIntegrationTest {

	@Autowired
	private HyperwalletHealthCheckService hyperwalletHealthCheckService;

	@Test
	void shouldCheckWhenHyperwalletIsUp() {
		hyperwalletHealthMockServerFixtures.mockGetHealth_up();

		final HyperwalletAPICheck hyperwalletAPICheck = hyperwalletHealthCheckService.check();

		assertThat(hyperwalletAPICheck.getHyperwalletAPICheckStatus()).isEqualTo(HyperwalletAPICheckStatus.UP);
		assertThat(hyperwalletAPICheck.getLocation()).contains("http://localhost");
		assertThat(hyperwalletAPICheck.getError()).isNull();
	}

	@Test
	void shouldCheckWhenHyperwalletIsDown() {
		hyperwalletHealthMockServerFixtures.mockGetHealth_down();

		final HyperwalletAPICheck hyperwalletAPICheck = hyperwalletHealthCheckService.check();

		assertThat(hyperwalletAPICheck.getHyperwalletAPICheckStatus()).isEqualTo(HyperwalletAPICheckStatus.DOWN);
		assertThat(hyperwalletAPICheck.getLocation()).contains("http://localhost");
		assertThat(hyperwalletAPICheck.getError()).isEqualTo("Error message");
	}

}
