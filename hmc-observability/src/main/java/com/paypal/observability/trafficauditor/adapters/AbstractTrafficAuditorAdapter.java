package com.paypal.observability.trafficauditor.adapters;

import com.paypal.observability.trafficauditor.configuration.TrafficAuditorConfiguration;
import com.paypal.observability.trafficauditor.model.TrafficAuditorRequest;
import com.paypal.observability.trafficauditor.model.TrafficAuditorResponse;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTarget;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public abstract class AbstractTrafficAuditorAdapter<T, R> implements TrafficAuditorAdapter<T, R>, InitializingBean {

	private final TrafficAuditorConfiguration trafficAuditorConfiguration;

	private final TrafficAuditorTraceHolder trafficAuditorTraceHolder;

	protected AbstractTrafficAuditorAdapter(final TrafficAuditorConfiguration trafficAuditorConfiguration,
			final TrafficAuditorTraceHolder trafficAuditorTraceHolder) {
		this.trafficAuditorConfiguration = trafficAuditorConfiguration;
		this.trafficAuditorTraceHolder = trafficAuditorTraceHolder;
	}

	@Override
	public void startTraceCapture(final T request) {
		if (!trafficAuditorConfiguration.isTrafficAuditorEnabled()) {
			return;
		}

		try {
			final TrafficAuditorTrace trafficAuditorTrace = new TrafficAuditorTrace();
			trafficAuditorTrace.setTarget(getTarget());
			trafficAuditorTrace.setRequest(doCaptureRequest(request));

			if (shouldCapture(trafficAuditorTrace.getRequest())) {
				trafficAuditorTraceHolder.request(trafficAuditorTrace);
			}
		}
		catch (final Exception e) {
			log.trace("Error while capturing request", e);
		}
	}

	@Override
	public void endTraceCapture(final R response) {
		if (!trafficAuditorConfiguration.isTrafficAuditorEnabled()) {
			return;
		}

		try {
			final TrafficAuditorTrace trafficAuditorTrace = trafficAuditorTraceHolder.currentTrace();
			trafficAuditorTrace.setResponse(Optional.ofNullable(doCaptureResponse(response)));

			if (shouldCapture(trafficAuditorTrace.getResponse())) {
				trafficAuditorTraceHolder.response(trafficAuditorTrace);
			}
		}
		catch (final Exception e) {
			log.trace("Error while capturing response", e);
			trafficAuditorTraceHolder.noResponse();
		}
	}

	@Override
	public void endTraceCapture() {
		if (!trafficAuditorConfiguration.isTrafficAuditorEnabled()) {
			return;
		}

		try {
			trafficAuditorTraceHolder.noResponse();
		}
		catch (final Exception e) {
			log.trace("Error while ending trace capture with no response", e);
			trafficAuditorTraceHolder.clear();
		}
	}

	@Override
	public void endTraceCapture(final Throwable thrown) {
		if (!trafficAuditorConfiguration.isTrafficAuditorEnabled()) {
			return;
		}

		try {
			trafficAuditorTraceHolder.thrown(thrown);
		}
		catch (final Exception e) {
			log.trace("Error while ending trace capture with thrown", e);
			trafficAuditorTraceHolder.clear();
		}
	}

	protected boolean shouldCapture(final TrafficAuditorRequest request) {
		return isNotMultiPart(request.getHeaders());
	}

	protected boolean shouldCapture(final Optional<TrafficAuditorResponse> response) {
		return response.isEmpty() || isNotMultiPart(response.get().getHeaders());
	}

	private boolean isNotMultiPart(final Map<String, List<String>> headers) {
		//@formatter:off
		return headers.entrySet().stream()
				.filter(e -> e.getKey() != null)
				.filter(e -> e.getKey().toLowerCase().contains("content-type"))
				.filter(e -> String.join(",", e.getValue()).toLowerCase().contains("multipart"))
				.findFirst()
				.isEmpty();
		//@formatter:on
	}

	protected abstract TrafficAuditorRequest doCaptureRequest(T request);

	protected abstract TrafficAuditorResponse doCaptureResponse(R response);

	protected abstract TrafficAuditorTarget getTarget();

}
