package com.paypal.observability.trafficauditor.adapters;

import com.paypal.observability.trafficauditor.model.TrafficAuditorTrace;
import com.paypal.observability.trafficauditor.services.TrafficAuditorService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TrafficAuditorTraceHolder implements InitializingBean {

	private static TrafficAuditorTraceHolder instance;

	private static final ThreadLocal<TrafficAuditorTrace> currentTrace = new ThreadLocal<>();

	private final TrafficAuditorService trafficAuditorService;

	public TrafficAuditorTraceHolder(final TrafficAuditorService trafficAuditorService) {
		this.trafficAuditorService = trafficAuditorService;
	}

	public TrafficAuditorTrace currentTrace() {
		return currentTrace.get();
	}

	public void clear() {
		currentTrace.remove();
	}

	public void request(final TrafficAuditorTrace trafficAuditorTrace) {
		if (currentTrace.get() != null) {
			sendAndClear(trafficAuditorTrace);
		}
		currentTrace.set(trafficAuditorTrace);
	}

	public void response(final TrafficAuditorTrace trafficAuditorTrace) {
		if (currentTrace.get() != null) {
			sendAndClear(trafficAuditorTrace);
		}
	}

	public void noResponse() {
		if (currentTrace.get() != null) {
			sendAndClear(currentTrace());
		}
	}

	private void sendAndClear(final TrafficAuditorTrace trafficAuditorTrace) {
		trafficAuditorService.send(trafficAuditorTrace);
		currentTrace.remove();
	}

	public void thrown(final Throwable thrown) {
		if (currentTrace.get() != null) {
			final TrafficAuditorTrace trafficAuditorTrace = currentTrace.get();
			trafficAuditorTrace.setThrowable(Optional.of(thrown));
			sendAndClear(trafficAuditorTrace);
		}
	}

	@SuppressWarnings("java:S2696")
	@Override
	public void afterPropertiesSet() {
		instance = this;
	}

	public static TrafficAuditorTraceHolder get() {
		return instance;
	}

}
