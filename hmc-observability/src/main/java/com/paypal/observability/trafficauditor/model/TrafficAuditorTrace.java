package com.paypal.observability.trafficauditor.model;

import lombok.Data;

import java.util.Optional;

@Data
public class TrafficAuditorTrace {

	private TrafficAuditorRequest request;

	private Optional<TrafficAuditorResponse> response = Optional.empty();

	private Optional<Throwable> throwable = Optional.empty();

	private TrafficAuditorTarget target;

	public boolean failed() {
		return throwable.isPresent();
	}

}
