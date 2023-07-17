package com.paypal.observability.trafficauditor.loggers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.observability.trafficauditor.loggers.converters.TrafficAuditorTraceConverter;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class TrafficAuditorLogger {

	private final TrafficAuditorTraceConverter trafficAuditorTraceConverter;

	private final ObjectMapper objectMapper;

	public TrafficAuditorLogger(final TrafficAuditorTraceConverter trafficAuditorTraceConverter) {
		this.trafficAuditorTraceConverter = trafficAuditorTraceConverter;
		this.objectMapper = getObjectMapper();
	}

	public void log(final TrafficAuditorTrace trace) {
		try {
			log.info("{}", objectMapper.writeValueAsString(trafficAuditorTraceConverter.from(trace)));
		}
		catch (final Exception e) {
			// errors during logging should not affect the application
		}
	}

	private ObjectMapper getObjectMapper() {
		final ObjectMapper traceLogObjectMapper = new ObjectMapper();
		traceLogObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return traceLogObjectMapper;
	}

}
