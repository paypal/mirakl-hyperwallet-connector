package com.paypal.observability.startupchecks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Map;
import java.util.Optional;

@Value
@Builder
@AllArgsConstructor
public class StartupCheck {

	private StartupCheckStatus status;

	private Optional<String> statusMessage;

	@Singular
	private Map<String, Object> details;

}
