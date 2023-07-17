package com.paypal.observability.trafficauditor.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class TrafficAuditorConfiguration {

	@Value("${hmc.toggle-features.http-capture}")
	private boolean trafficAuditorEnabled;

}
