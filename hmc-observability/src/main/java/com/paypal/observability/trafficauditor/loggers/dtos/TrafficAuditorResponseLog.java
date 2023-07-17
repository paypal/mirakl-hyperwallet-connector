package com.paypal.observability.trafficauditor.loggers.dtos;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TrafficAuditorResponseLog {

	private int responseCode;

	private Map<String, List<String>> headers;

	private String body;

}
