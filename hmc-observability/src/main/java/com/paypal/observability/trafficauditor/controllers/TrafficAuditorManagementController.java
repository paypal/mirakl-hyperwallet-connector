package com.paypal.observability.trafficauditor.controllers;

import com.paypal.observability.trafficauditor.configuration.TrafficAuditorConfiguration;
import com.paypal.observability.trafficauditor.controllers.dtos.TrafficAuditorConfigurationDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrafficAuditorManagementController {

	private final TrafficAuditorConfiguration trafficAuditorConfiguration;

	public TrafficAuditorManagementController(final TrafficAuditorConfiguration trafficAuditorConfiguration) {
		this.trafficAuditorConfiguration = trafficAuditorConfiguration;
	}

	@GetMapping("/management/traffic-auditor/configuration")
	public TrafficAuditorConfigurationDto getTrafficAuditorConfiguration() {
		final TrafficAuditorConfigurationDto trafficAuditorConfigurationDto = new TrafficAuditorConfigurationDto();
		trafficAuditorConfigurationDto.setTrafficAuditorEnabled(trafficAuditorConfiguration.isTrafficAuditorEnabled());

		return trafficAuditorConfigurationDto;
	}

	@PutMapping("/management/traffic-auditor/configuration")
	public void setTrafficAuditorConfiguration(
			@RequestBody final TrafficAuditorConfigurationDto trafficAuditorConfigurationDto) {
		trafficAuditorConfiguration.setTrafficAuditorEnabled(trafficAuditorConfigurationDto.isTrafficAuditorEnabled());
	}

}
