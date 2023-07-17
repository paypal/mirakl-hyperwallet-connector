package com.paypal.observability.miraklapichecks.actuator.converters;

import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class MiraklAPIHealthCheckActuatorConverterImpl implements MiraklAPIHealthCheckActuatorConverter {

	@Override
	public Health from(final MiraklAPICheck miraklAPICheck) {
		//@formatter:off
		if (miraklAPICheck.isHealthy()) {
			return Health.up()
					.withDetail("version", miraklAPICheck.getVersion())
					.withDetail("location", miraklAPICheck.getLocation())
					.build();
		}
		else {
			return Health.down()
					.withDetail("error", miraklAPICheck.getError())
					.withDetail("location", miraklAPICheck.getLocation())
					.build();
		}
		//@formatter:on
	}

}
