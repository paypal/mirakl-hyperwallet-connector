package com.paypal.observability.miraklapichecks.actuator.converters;

import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import org.springframework.boot.actuate.health.Health;

public interface MiraklAPIHealthCheckActuatorConverter {

	Health from(MiraklAPICheck miraklAPICheck);

}
