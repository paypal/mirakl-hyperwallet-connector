package com.paypal.observability.trafficauditor.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TrafficAuditorRequest {

	private String method;

	private String url;

	private Map<String, List<String>> headers;

	private Map<String, String> queryParameters;

	private String body;

}
