package com.paypal.observability.hyperwalletapichecks.actuator.converters;

import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;
import org.springframework.boot.actuate.health.Health;

public interface HyperwalletAPIHealthCheckActuatorConverter {

	Health from(HyperwalletAPICheck hyperwalletAPICheck);

}
