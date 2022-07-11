package com.paypal.observability.startupchecks.model;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Value
public class StartupCheckReport {

	private StartupCheckStatus status;

	private Optional<String> statusMessage;

	private Map<String, StartupCheck> checks;

	public StartupCheckReport(StartupCheckStatus status, Map<String, StartupCheck> checks) {
		this(status, checks, null);
	}

	public StartupCheckReport(StartupCheckStatus status, Map<String, StartupCheck> checks, String statusMessage) {
		this.status = status;
		this.checks = new HashMap<>(checks);
		this.statusMessage = Optional.ofNullable(statusMessage);
	}

}
