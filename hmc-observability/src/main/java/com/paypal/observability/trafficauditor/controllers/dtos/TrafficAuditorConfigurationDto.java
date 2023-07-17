package com.paypal.observability.trafficauditor.controllers.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrafficAuditorConfigurationDto {

	private boolean trafficAuditorEnabled;

}
