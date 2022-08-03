package com.paypal.observability.hyperwalletapichecks.actuator.converters;

import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletAPIHealthCheckActuatorConverterImpl implements HyperwalletAPIHealthCheckActuatorConverter {

	@Override
	public Health from(final HyperwalletAPICheck hyperwalletAPICheck) {
		//@formatter:off
		if (hyperwalletAPICheck.isHealthy()) {
			return Health.up()
					.withDetail("location", hyperwalletAPICheck.getLocation())
					.build();
		}
		else {
			return Health.down()
					.withDetail("error", hyperwalletAPICheck.getError())
					.withDetail("location", hyperwalletAPICheck.getLocation())
					.build();
		}
		//@formatter:on
	}

}
