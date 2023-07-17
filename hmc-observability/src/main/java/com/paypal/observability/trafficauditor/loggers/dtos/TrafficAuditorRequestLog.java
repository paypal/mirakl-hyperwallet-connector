package com.paypal.observability.trafficauditor.loggers.dtos;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TrafficAuditorRequestLog {

	private String method;

	private String url;

	private Map<String, List<String>> headers;

	private Map<String, String> queryParameters;

	private String body;

}
