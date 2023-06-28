package com.paypal.observability.miraklapichecks.actuator;

import com.paypal.observability.miraklapichecks.actuator.converters.MiraklAPIHealthCheckActuatorConverter;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import com.paypal.observability.miraklapichecks.services.MiraklHealthCheckService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MiraklAPIHealthCheckHealthIndicator implements HealthIndicator {

	private final MiraklHealthCheckService miraklHealthCheckService;

	private final MiraklAPIHealthCheckActuatorConverter miraklAPIHealthCheckActuatorConverter;

	public MiraklAPIHealthCheckHealthIndicator(final MiraklHealthCheckService miraklHealthCheckService,
			final MiraklAPIHealthCheckActuatorConverter miraklAPIHealthCheckActuatorConverter) {
		this.miraklHealthCheckService = miraklHealthCheckService;
		this.miraklAPIHealthCheckActuatorConverter = miraklAPIHealthCheckActuatorConverter;
	}

	@Override
	public Health health() {
		final MiraklAPICheck miraklAPICheck = miraklHealthCheckService.check();
		return miraklAPIHealthCheckActuatorConverter.from(miraklAPICheck);
	}

}
