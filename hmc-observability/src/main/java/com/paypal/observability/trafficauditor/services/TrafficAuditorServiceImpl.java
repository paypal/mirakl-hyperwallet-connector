package com.paypal.observability.trafficauditor.services;

import com.paypal.observability.trafficauditor.loggers.TrafficAuditorLogger;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrafficAuditorServiceImpl implements TrafficAuditorService {

	private final TrafficAuditorLogger trafficAuditorLogger;

	public TrafficAuditorServiceImpl(final TrafficAuditorLogger trafficAuditorLogger) {
		this.trafficAuditorLogger = trafficAuditorLogger;
	}

	@Override
	public void send(final TrafficAuditorTrace trafficAuditorTrace) {
		trafficAuditorLogger.log(trafficAuditorTrace);
	}

}
