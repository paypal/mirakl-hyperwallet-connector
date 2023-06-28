package com.paypal.observability.startupchecks;

import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.observability.AbstractObservabilityIntegrationTest;
import com.paypal.observability.startupchecks.model.StartupCheckPrinterRegistry;
import com.paypal.observability.startupchecks.model.StartupCheckProvider;
import com.paypal.observability.startupchecks.service.StartupCheckerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StartupChecksITTest extends AbstractObservabilityIntegrationTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ApplicationEventPublisher publisher;

	@SpyBean
	private MyStartupCheckerService startupCheckerService;

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForType(StartupCheckerService.class);

	@BeforeEach
	void disableAutomaticShutdown() {
		startupCheckerService.setStartupChecksExitOnFail(false);
	}

	@Test
	void shouldDoStartupCheckTest_AfterApplicationStarts_WhenEnabled() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_emptySchema();

		startupCheckerService.setStartupChecksEnabled(true);
		publisher.publishEvent(new ContextRefreshedEvent(applicationContext));
		verify(startupCheckerService, atLeastOnce()).doStartupChecks();
	}

	@Test
	void shouldShutdownApplication_AfterApplicationStarts_WhenChecksAreNotOk() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_emptySchema();

		startupCheckerService.setStartupChecksEnabled(true);
		publisher.publishEvent(new ContextRefreshedEvent(applicationContext));
		verify(startupCheckerService, atLeastOnce()).doStartupChecks();
		verify(startupCheckerService, atLeastOnce()).shutdownSpringApplication();
	}

	@Test
	void shouldNotShutdownApplication_AfterApplicationStarts_WhenChecksOk() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_kyc_correct();

		startupCheckerService.setStartupChecksEnabled(true);
		publisher.publishEvent(new ContextRefreshedEvent(applicationContext));
		verify(startupCheckerService, atLeastOnce()).doStartupChecks();
		verify(startupCheckerService, times(0)).shutdownSpringApplication();
	}

	@Test
	void shouldNotShutdownApplication_AfterApplicationStarts_WhenChecksOkWithWarns() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_kyc_correctWithWarnings();

		startupCheckerService.setStartupChecksEnabled(true);
		publisher.publishEvent(new ContextRefreshedEvent(applicationContext));
		verify(startupCheckerService, atLeastOnce()).doStartupChecks();
		verify(startupCheckerService, times(0)).shutdownSpringApplication();
	}

	@Test
	void shouldNotShutdownApplication_AfterApplicationStarts_WhenChecksFails() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_internalServerError();

		startupCheckerService.setStartupChecksEnabled(true);
		publisher.publishEvent(new ContextRefreshedEvent(applicationContext));
		verify(startupCheckerService, atLeastOnce()).doStartupChecks();
		verify(startupCheckerService, times(0)).shutdownSpringApplication();
	}

	@Test
	void shouldTriggerAllChecks_AfterApplicationStarts_WhenEnabled() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_emptySchema();

		startupCheckerService.setStartupChecksEnabled(true);
		publisher.publishEvent(new ContextRefreshedEvent(applicationContext));
		assertThat(logTrackerStub.contains("Startup Check: <miraklCustomFieldsSchemaCheck>")).isTrue();
		assertThat(logTrackerStub.contains("Startup Check: <miraklDocSchemaCheck>")).isTrue();
		assertThat(logTrackerStub.contains("Startup Check: <miraklHealthCheck>")).isTrue();
		assertThat(logTrackerStub.contains("Startup Check: <hyperwalletHealthCheck>")).isTrue();
	}

	@Test
	void shouldShowReport_AfterApplicationStarts_WhenEverythingIsOK() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_kyc_correct();
		healthMockServerFixtures.mockGetVersion_up();
		hyperwalletHealthMockServerFixtures.mockGetHealth_up();
		docsMockServerFixtures.mockGetDocsConfiguration_correctSchemaResponse();

		startupCheckerService.setStartupChecksEnabled(true);
		publisher.publishEvent(new ContextRefreshedEvent(applicationContext));
		assertThat(logTrackerStub.contains("Startup Check Report -> Status: <READY>")).isTrue();
		assertThat(logTrackerStub.contains(StartupCheckerService.LOGMSG_STATUS_READY)).isTrue();
	}

	@Test
	void shouldShowReport_AfterApplicationStarts_WhenThereAreWarnings() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_kyc_correctWithWarnings();
		docsMockServerFixtures.mockGetDocsConfiguration_correctSchemaResponse();

		startupCheckerService.setStartupChecksEnabled(true);
		publisher.publishEvent(new ContextRefreshedEvent(applicationContext));
		assertThat(logTrackerStub.contains("Startup Check Report -> Status: <READY_WITH_WARNINGS>")).isTrue();
		assertThat(logTrackerStub.contains(StartupCheckerService.LOGMSG_STATUS_READY_WITH_WARNINGS)).isTrue();
	}

	@Test
	void shouldShowReport_AfterApplicationStarts_WhenThereAreFails() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_emptySchema();

		startupCheckerService.setStartupChecksEnabled(true);
		publisher.publishEvent(new ContextRefreshedEvent(applicationContext));
		assertThat(logTrackerStub.contains("Startup Check Report -> Status: <NOT_READY>")).isTrue();
		assertThat(logTrackerStub.contains(StartupCheckerService.LOGMSG_STATUS_NOT_READY)).isTrue();
	}

	@Test
	void shouldShowReport_AfterApplicationStart_WhenUnexpectedErrorsOccursWhileChecking() {
		additionalFieldsMockServerFixtures.mockGetAdditionalFields_internalServerError();

		startupCheckerService.setStartupChecksEnabled(true);
		publisher.publishEvent(new ContextRefreshedEvent(applicationContext));
		assertThat(logTrackerStub.contains("Startup Check Report -> Status: <UNKNOWN>")).isTrue();
		assertThat(logTrackerStub.contains(StartupCheckerService.LOGMSG_STATUS_UNKNOWN)).isTrue();
	}

	@Qualifier("startupCheckerService")
	static class MyStartupCheckerService extends StartupCheckerService {

		public MyStartupCheckerService(final List<StartupCheckProvider> startupCheckProviders,
				final StartupCheckPrinterRegistry startupCheckPrinterRegistry,
				final ConfigurableApplicationContext applicationContext) {
			super(startupCheckProviders, startupCheckPrinterRegistry, applicationContext);
		}

		@Override
		protected void doStartupChecks() {
			super.doStartupChecks();
		}

		@Override
		protected void shutdownSpringApplication() {
			super.shutdownSpringApplication();
		}

		public void setStartupChecksEnabled(final boolean startupChecksEnabled) {
			this.startupChecksEnabled = startupChecksEnabled;
		}

		public void setStartupChecksExitOnFail(final boolean startupChecksExitOnFail) {
			this.startupChecksExitOnFail = startupChecksExitOnFail;
		}

	}

}
