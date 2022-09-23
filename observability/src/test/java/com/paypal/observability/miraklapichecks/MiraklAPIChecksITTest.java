package com.paypal.observability.miraklapichecks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheckStatus;
import com.paypal.observability.miraklapichecks.services.MiraklHealthCheckService;
import com.paypal.observability.testsupport.AbstractMockServerITTest;
import com.paypal.observability.testsupport.ObservabilityWebIntegrationContext;

import static org.assertj.core.api.Assertions.assertThat;

@ObservabilityWebIntegrationContext
class MiraklAPIChecksITTest extends AbstractMockServerITTest {

	@Value("${server.url}")
	private String serverUrl;

	@Autowired
	private MiraklHealthCheckService miraklHealthCheckService;

	@Test
	void shouldCheckWhenMiraklIsUp() {
		healthMockServerFixtures.mockGetVersion_up();

		MiraklAPICheck miraklAPICheck = miraklHealthCheckService.check();

		assertThat(miraklAPICheck.getMiraklAPICheckStatus()).isEqualTo(MiraklAPICheckStatus.UP);
		assertThat(miraklAPICheck.getVersion()).isEqualTo("3.210");
		assertThat(miraklAPICheck.getLocation()).isEqualTo(this.serverUrl);
		assertThat(miraklAPICheck.getError()).isNull();
	}

	@Test
	void shouldCheckWhenMiraklIsDown() {
		healthMockServerFixtures.mockGetVersion_down();

		MiraklAPICheck miraklAPICheck = miraklHealthCheckService.check();

		assertThat(miraklAPICheck.getMiraklAPICheckStatus()).isEqualTo(MiraklAPICheckStatus.DOWN);
		assertThat(miraklAPICheck.getLocation()).isEqualTo(this.serverUrl);
		assertThat(miraklAPICheck.getError()).isEqualTo("[500] Internal Server Error");
	}

}
