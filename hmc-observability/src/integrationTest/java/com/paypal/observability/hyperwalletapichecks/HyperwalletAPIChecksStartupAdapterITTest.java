package com.paypal.observability.hyperwalletapichecks;

import com.paypal.observability.AbstractObservabilityIntegrationTest;
import com.paypal.observability.hyperwalletapichecks.startup.HyperwalletHealthCheckStartupCheckPrinter;
import com.paypal.observability.hyperwalletapichecks.startup.HyperwalletHealthCheckStartupProvider;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class HyperwalletAPIChecksStartupAdapterITTest extends AbstractObservabilityIntegrationTest {

	@Autowired
	private HyperwalletHealthCheckStartupProvider hyperwalletHealthCheckStartupProvider;

	@Autowired
	private HyperwalletHealthCheckStartupCheckPrinter hyperwalletHealthCheckStartupCheckPrinter;

	@Test
	void shouldCheckAndReturnReadyStatusWhenAllItsOK() {
		hyperwalletHealthMockServerFixtures.mockGetHealth_up();

		final StartupCheck startupCheck = hyperwalletHealthCheckStartupProvider.check();
		final String[] startupCheckReport = hyperwalletHealthCheckStartupCheckPrinter.print(startupCheck);

		assertThat(startupCheck.getStatus()).isEqualTo(StartupCheckStatus.READY);
		//@formatter:off
		assertThat(startupCheckReport[0]).contains("Hyperwallet API is accessible")
			.contains("status: UP")
			.contains("location: http://localhost");
		//@formatter:on
	}

	@Test
	void shouldCheckAndReturnReadyWithWarningsStatusWhenAllItsOK() {
		hyperwalletHealthMockServerFixtures.mockGetHealth_down();

		final StartupCheck startupCheck = hyperwalletHealthCheckStartupProvider.check();
		final String[] startupCheckReport = hyperwalletHealthCheckStartupCheckPrinter.print(startupCheck);

		assertThat(startupCheck.getStatus()).isEqualTo(StartupCheckStatus.READY_WITH_WARNINGS);
		//@formatter:off
		assertThat(startupCheckReport[0]).contains("Hyperwallet API is not accessible")
			.contains("status: DOWN")
			.contains("error: Error message")
			.contains("location: http://localhost");
		//@formatter:on
	}

}
