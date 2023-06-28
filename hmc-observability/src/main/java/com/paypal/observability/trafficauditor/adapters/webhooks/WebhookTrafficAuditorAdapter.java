package com.paypal.observability.trafficauditor.adapters.webhooks;

import com.paypal.observability.trafficauditor.adapters.AbstractTrafficAuditorAdapter;
import com.paypal.observability.trafficauditor.adapters.TrafficAuditorAdapter;
import com.paypal.observability.trafficauditor.adapters.TrafficAuditorTraceHolder;
import com.paypal.observability.trafficauditor.configuration.TrafficAuditorConfiguration;
import com.paypal.observability.trafficauditor.model.TrafficAuditorRequest;
import com.paypal.observability.trafficauditor.model.TrafficAuditorResponse;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTarget;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.paypal.observability.trafficauditor.interceptors.webhooks.WebhookLoggingRequestFilter.MAX_PAYLOAD_LENGTH;

@Component
public class WebhookTrafficAuditorAdapter extends AbstractTrafficAuditorAdapter<HttpServletRequest, HttpServletRequest>
		implements TrafficAuditorAdapter<HttpServletRequest, HttpServletRequest>, InitializingBean {

	private static WebhookTrafficAuditorAdapter instance;

	protected WebhookTrafficAuditorAdapter(final TrafficAuditorConfiguration trafficAuditorConfiguration,
										   final TrafficAuditorTraceHolder trafficAuditorTraceHolder) {
		super(trafficAuditorConfiguration, trafficAuditorTraceHolder);
	}

	public static WebhookTrafficAuditorAdapter get() {
		return instance;
	}

	@Override
	protected TrafficAuditorRequest doCaptureRequest(final HttpServletRequest request) {
		return getRequest(request);
	}

	@Override
	protected TrafficAuditorResponse doCaptureResponse(final HttpServletRequest response) {
		return null;
	}

	@Override
	protected TrafficAuditorTarget getTarget() {
		return TrafficAuditorTarget.HMC;
	}

	private TrafficAuditorRequest getRequest(final HttpServletRequest request) {
		final TrafficAuditorRequest trafficAuditorRequest = new TrafficAuditorRequest();
		trafficAuditorRequest.setMethod(request.getMethod());
		trafficAuditorRequest.setUrl(request.getRequestURI());
		trafficAuditorRequest.setQueryParameters(getQueryParameters(request));
		trafficAuditorRequest.setHeaders(getHeaders(request));
		trafficAuditorRequest.setBody(getBody(request));

		return trafficAuditorRequest;
	}

	@Nullable
	private String getBody(final HttpServletRequest request) {
		final ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request,
				ContentCachingRequestWrapper.class);
		if (wrapper != null) {
			final byte[] buf = wrapper.getContentAsByteArray();
			if (buf.length > 0) {
				final int length = Math.min(buf.length, MAX_PAYLOAD_LENGTH);
				try {
					return new String(buf, 0, length, wrapper.getCharacterEncoding());
				}
				catch (final UnsupportedEncodingException ex) {
					return "[unknown]";
				}
			}
		}
		return null;
	}

	private Map<String, List<String>> getHeaders(final HttpServletRequest request) {
		final HttpHeaders headers = new ServletServerHttpRequest(request).getHeaders();
		return headers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private Map<String, String> getQueryParameters(final HttpServletRequest request) {
		final String queryString = request.getQueryString() != null ? request.getQueryString() : "";
		return Arrays.stream(queryString.split("&")).filter(s -> s.contains("=")).map(s -> s.split("="))
				.collect(Collectors.toMap(s -> s[0], s -> s[1]));
	}

	@SuppressWarnings("java:S2696")
	@Override
	public void afterPropertiesSet() {
		instance = this;
	}
}
