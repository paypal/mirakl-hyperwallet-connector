package com.paypal.observability.miraklapichecks;

import com.paypal.observability.AbstractObservabilityIntegrationTest;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheckStatus;
import com.paypal.observability.miraklapichecks.services.MiraklHealthCheckService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static org.assertj.core.api.Assertions.assertThat;

class MiraklAPIChecksITTest extends AbstractObservabilityIntegrationTest {

	@Value("${server.url}")
	private String serverUrl;

	@Autowired
	private MiraklHealthCheckService miraklHealthCheckService;

	@Test
	void shouldCheckWhenMiraklIsUp() {
		healthMockServerFixtures.mockGetVersion_up();

		final MiraklAPICheck miraklAPICheck = miraklHealthCheckService.check();

		assertThat(miraklAPICheck.getMiraklAPICheckStatus()).isEqualTo(MiraklAPICheckStatus.UP);
		assertThat(miraklAPICheck.getVersion()).isEqualTo("3.210");
		assertThat(miraklAPICheck.getLocation()).isEqualTo(this.serverUrl);
		assertThat(miraklAPICheck.getError()).isNull();
	}

	@Test
	void shouldCheckWhenMiraklIsDown() {
		healthMockServerFixtures.mockGetVersion_down();

		final MiraklAPICheck miraklAPICheck = miraklHealthCheckService.check();

		assertThat(miraklAPICheck.getMiraklAPICheckStatus()).isEqualTo(MiraklAPICheckStatus.DOWN);
		assertThat(miraklAPICheck.getLocation()).isEqualTo(this.serverUrl);
		assertThat(miraklAPICheck.getError()).isEqualTo("[500] Internal Server Error");
	}

}
