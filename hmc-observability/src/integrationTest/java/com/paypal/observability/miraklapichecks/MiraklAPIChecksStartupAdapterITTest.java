package com.paypal.observability.miraklapichecks;

import com.paypal.observability.AbstractObservabilityIntegrationTest;
import com.paypal.observability.miraklapichecks.startup.MiraklHealthCheckStartupCheckPrinter;
import com.paypal.observability.miraklapichecks.startup.MiraklHealthCheckStartupProvider;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class MiraklAPIChecksStartupAdapterITTest extends AbstractObservabilityIntegrationTest {

	@Autowired
	private MiraklHealthCheckStartupProvider miraklHealthCheckStartupProvider;

	@Autowired
	private MiraklHealthCheckStartupCheckPrinter miraklHealthCheckStartupCheckPrinter;

	@Test
	void shouldCheckAndReturnReadyStatusWhenAllItsOK() {
		healthMockServerFixtures.mockGetVersion_up();

		final StartupCheck startupCheck = miraklHealthCheckStartupProvider.check();
		final String[] startupCheckReport = miraklHealthCheckStartupCheckPrinter.print(startupCheck);

		assertThat(startupCheck.getStatus()).isEqualTo(StartupCheckStatus.READY);
		//@formatter:off
		assertThat(startupCheckReport[0]).contains("Mirakl API is accessible")
			.contains("status: UP")
			.contains("version: 3.210")
			.contains("location: http://localhost");
		//@formatter:on
	}

	@Test
	void shouldCheckAndReturnReadyWithWarningsStatusWhenAllItsOK() {
		healthMockServerFixtures.mockGetVersion_down();

		final StartupCheck startupCheck = miraklHealthCheckStartupProvider.check();
		final String[] startupCheckReport = miraklHealthCheckStartupCheckPrinter.print(startupCheck);

		assertThat(startupCheck.getStatus()).isEqualTo(StartupCheckStatus.READY_WITH_WARNINGS);
		//@formatter:off
		assertThat(startupCheckReport[0]).contains("Mirakl API is not accessible")
			.contains("status: DOWN")
			.contains("error: [500] Internal Server Error")
			.contains("location: http://localhost");
		//@formatter:on
	}

}
