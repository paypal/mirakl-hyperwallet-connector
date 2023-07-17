package com.paypal.observability.trafficauditor.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TrafficAuditorResponse {

	private int responseCode;

	private Map<String, List<String>> headers;

	private String body;

}
