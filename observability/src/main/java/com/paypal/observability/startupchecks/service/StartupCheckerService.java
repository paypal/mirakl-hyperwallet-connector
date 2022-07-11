package com.paypal.observability.startupchecks.service;

import com.paypal.observability.startupchecks.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StartupCheckerService {

	public static final String LOGMSG_STATUS_NOT_READY = "Some system startup checks has not passed. Please review the logs and fix the reported problems.";

	public static final String LOGMSG_STATUS_UNKNOWN = "Some system startup couldn't be performed. Please review the logs to see the cause of the startup checks failure.";

	public static final String LOGMSG_STATUS_READY_WITH_WARNINGS = "Some system startup checks has warnings. It's recommended to review the logs and fix the reported warnings.";

	public static final String LOGMSG_STATUS_READY = "All system startup checks has not passed.";

	public static final String LOGMSG_SYSTEM_SHUTDOWN = "Some of the errors found during startup checks will cause the system to not work properly. System will shutdown.";

	@Value("${hmc.startupChecks.enabled}")
	protected boolean startupChecksEnabled;

	@Value("${hmc.startupChecks.exitOnFail}")
	protected boolean startupChecksExitOnFail;

	private final List<StartupCheckProvider> startupCheckProviders;

	private final StartupCheckPrinterRegistry startupCheckPrinterRegistry;

	private final ConfigurableApplicationContext applicationContext;

	public StartupCheckerService(List<StartupCheckProvider> startupCheckProviders,
			StartupCheckPrinterRegistry startupCheckPrinterRegistry,
			ConfigurableApplicationContext applicationContext) {
		this.startupCheckProviders = startupCheckProviders;
		this.startupCheckPrinterRegistry = startupCheckPrinterRegistry;
		this.applicationContext = applicationContext;
	}

	@EventListener(ContextRefreshedEvent.class)
	public void startupChecks() {
		if (startupChecksEnabled) {
			doStartupChecks();
		}
	}

	protected void doStartupChecks() {
		Map<String, StartupCheck> startupChecks = analyzeStartupChecks();
		StartupCheckStatus finalStatus = getStatus(startupChecks.values());
		StartupCheckReport startupCheckReport = buildStartupCheckReport(startupChecks, finalStatus);
		logStartupCheckReport(startupCheckReport);
		shutdownIfNotReady(finalStatus);
	}

	private void shutdownIfNotReady(StartupCheckStatus finalStatus) {
		if (finalStatus.equals(StartupCheckStatus.NOT_READY)) {
			shutdownSpringApplication();
		}
	}

	protected void shutdownSpringApplication() {
		if (startupChecksExitOnFail) {
			log.error(LOGMSG_SYSTEM_SHUTDOWN);
			applicationContext.close();
		}
	}

	private void logStartupCheckReport(StartupCheckReport startupCheckReport) {
		String logMessage = String.format("Startup Check Report -> Status: <%s>. Dumping individual checks:",
				startupCheckReport.getStatus());
		logStartupCheckMessage(startupCheckReport.getStatus(), logMessage);
		startupCheckReport.getChecks().forEach(this::logStartupCheck);
		logStartupCheckFinalStatusMessage(startupCheckReport);
	}

	private void logStartupCheck(String startupCheckName, StartupCheck startupCheck) {
		StartupCheckPrinter startupCheckPrinter = startupCheckPrinterRegistry.getStartupCheckPrinter(startupCheckName);
		String startupCheckText = String.join("\n---------\n", startupCheckPrinter.print(startupCheck));
		String logMessage = String.format("Startup Check: <%s>, Status <%s>, CheckDetails:%n%s", startupCheckName,
				startupCheck.getStatus(), startupCheckText);
		logStartupCheckMessage(startupCheck.getStatus(), logMessage);
	}

	private void logStartupCheckMessage(StartupCheckStatus startupCheckStatus, String logMessage) {
		if (startupCheckStatus.equals(StartupCheckStatus.NOT_READY)) {
			log.error(logMessage);
		}
		else if (startupCheckStatus.equals(StartupCheckStatus.READY)) {
			log.info(logMessage);
		}
		else {
			log.warn(logMessage);
		}
	}

	private void logStartupCheckFinalStatusMessage(StartupCheckReport startupCheckReport) {
		if (startupCheckReport.getStatus().equals(StartupCheckStatus.NOT_READY)) {
			log.error(LOGMSG_STATUS_NOT_READY);
		}
		else if (startupCheckReport.getStatus().equals(StartupCheckStatus.UNKNOWN)) {
			log.warn(LOGMSG_STATUS_UNKNOWN);
		}
		if (startupCheckReport.getStatus().equals(StartupCheckStatus.READY_WITH_WARNINGS)) {
			log.warn(LOGMSG_STATUS_READY_WITH_WARNINGS);
		}
		if (startupCheckReport.getStatus().equals(StartupCheckStatus.READY)) {
			log.info(LOGMSG_STATUS_READY);
		}
	}

	private StartupCheckReport buildStartupCheckReport(Map<String, StartupCheck> startupChecks,
			StartupCheckStatus status) {
		return new StartupCheckReport(status, startupChecks);
	}

	private Map<String, StartupCheck> analyzeStartupChecks() {
		return startupCheckProviders.stream()
				.collect(Collectors.toMap(StartupCheckProvider::getName, this::executeStartupCheck));
	}

	private StartupCheck executeStartupCheck(StartupCheckProvider startupCheckProvider) {
		try {
			return startupCheckProvider.check();
		}
		catch (Exception e) {
			log.error("Startup check failed", e);
			return new StartupCheck(StartupCheckStatus.UNKNOWN, Optional.of("Check failed."), Map.of());
		}
	}

	private StartupCheckStatus getStatus(Collection<StartupCheck> startupChecks) {
		if (findCheckWithStatus(startupChecks, StartupCheckStatus.NOT_READY)) {
			return StartupCheckStatus.NOT_READY;
		}
		else if (findCheckWithStatus(startupChecks, StartupCheckStatus.UNKNOWN)) {
			return StartupCheckStatus.UNKNOWN;
		}
		else if (findCheckWithStatus(startupChecks, StartupCheckStatus.READY_WITH_WARNINGS)) {
			return StartupCheckStatus.READY_WITH_WARNINGS;
		}

		return StartupCheckStatus.READY;
	}

	private boolean findCheckWithStatus(Collection<StartupCheck> startupChecks, StartupCheckStatus notReady) {
		return startupChecks.stream().anyMatch(x -> x.getStatus().equals(notReady));
	}

}
