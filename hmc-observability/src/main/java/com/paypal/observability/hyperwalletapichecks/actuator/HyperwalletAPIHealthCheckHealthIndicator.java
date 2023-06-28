package com.paypal.observability.hyperwalletapichecks.actuator;

import com.paypal.observability.hyperwalletapichecks.actuator.converters.HyperwalletAPIHealthCheckActuatorConverter;
import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;
import com.paypal.observability.hyperwalletapichecks.services.HyperwalletHealthCheckService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletAPIHealthCheckHealthIndicator implements HealthIndicator {

	private final HyperwalletHealthCheckService hyperwalletHealthCheckService;

	private final HyperwalletAPIHealthCheckActuatorConverter hyperwalletAPIHealthCheckActuatorConverter;

	public HyperwalletAPIHealthCheckHealthIndicator(final HyperwalletHealthCheckService hyperwalletHealthCheckService,
			final HyperwalletAPIHealthCheckActuatorConverter hyperwalletAPIHealthCheckActuatorConverter) {
		this.hyperwalletHealthCheckService = hyperwalletHealthCheckService;
		this.hyperwalletAPIHealthCheckActuatorConverter = hyperwalletAPIHealthCheckActuatorConverter;
	}

	@Override
	public Health health() {
		final HyperwalletAPICheck hyperwalletAPICheck = hyperwalletHealthCheckService.check();
		return hyperwalletAPIHealthCheckActuatorConverter.from(hyperwalletAPICheck);
	}

}
