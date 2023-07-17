package com.paypal.observability.trafficauditor.loggers.dtos;

import com.paypal.observability.trafficauditor.model.TrafficAuditorRequest;
import com.paypal.observability.trafficauditor.model.TrafficAuditorResponse;
import lombok.Data;

@Data
public class TrafficAuditorTraceLog {

	private TrafficAuditorRequest request;

	private TrafficAuditorResponse response;

	private String throwableMessage;

	private String target;

}
