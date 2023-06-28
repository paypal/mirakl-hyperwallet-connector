package com.paypal.observability.trafficauditor.adapters.hyperwallet;

import cc.protea.util.http.Response;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.hyperwallet.clientsdk.util.Request;
import com.paypal.observability.trafficauditor.adapters.AbstractTrafficAuditorAdapter;
import com.paypal.observability.trafficauditor.adapters.TrafficAuditorAdapter;
import com.paypal.observability.trafficauditor.adapters.TrafficAuditorTraceHolder;
import com.paypal.observability.trafficauditor.adapters.support.QueryParamExtractor;
import com.paypal.observability.trafficauditor.adapters.support.TrafficAuditorAdapterUtils;
import com.paypal.observability.trafficauditor.configuration.TrafficAuditorConfiguration;
import com.paypal.observability.trafficauditor.model.TrafficAuditorRequest;
import com.paypal.observability.trafficauditor.model.TrafficAuditorResponse;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTarget;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class HyperwalletTrafficAuditorAdapter extends AbstractTrafficAuditorAdapter<Request, Response>
		implements TrafficAuditorAdapter<Request, Response>, InitializingBean {

	private static HyperwalletTrafficAuditorAdapter instance;

	private final HyperwalletEncryption hyperwalletEncryption;

	public HyperwalletTrafficAuditorAdapter(final TrafficAuditorConfiguration trafficAuditorConfiguration,
			final TrafficAuditorTraceHolder trafficAuditorTraceHolder,
			@Nullable final HyperwalletEncryption hyperwalletEncryption) {
		super(trafficAuditorConfiguration, trafficAuditorTraceHolder);
		this.hyperwalletEncryption = hyperwalletEncryption;
	}

	public static HyperwalletTrafficAuditorAdapter get() {
		return instance;
	}

	@Override
	protected TrafficAuditorRequest doCaptureRequest(final Request request) {
		return getTrafficAuditorRequest(request);
	}

	@Override
	protected TrafficAuditorResponse doCaptureResponse(final Response response) {
		final TrafficAuditorResponse trafficAuditorResponse = new TrafficAuditorResponse();
		trafficAuditorResponse.setBody(decryptBodyIfNeeded(response.getBody()));
		trafficAuditorResponse.setHeaders(getHeaders(response));
		trafficAuditorResponse.setResponseCode(response.getResponseCode());

		return trafficAuditorResponse;
	}

	@Override
	protected TrafficAuditorTarget getTarget() {
		return TrafficAuditorTarget.HYPERWALLET;
	}

	@NotNull
	private TrafficAuditorRequest getTrafficAuditorRequest(final Request request) {
		final TrafficAuditorRequest trafficAuditorRequest = new TrafficAuditorRequest();
		trafficAuditorRequest.setMethod(getMethod(request));
		trafficAuditorRequest.setHeaders(getHeaders(request));
		trafficAuditorRequest.setQueryParameters(getQuery(request));
		trafficAuditorRequest.setBody(decryptBodyIfNeeded(getBody(request)));
		trafficAuditorRequest.setUrl(getUrl(request));
		return trafficAuditorRequest;
	}

	private String getMethod(final Request request) {
		final HttpURLConnection connection = (HttpURLConnection) getField(request, "connection");
		return connection != null ? connection.getRequestMethod() : "";
	}

	private String getUrl(final Request request) {
		final URL url = (URL) getField(request, "url");
		return url != null ? url.toString() : "";
	}

	private String getBody(final Request request) {
		return (String) getField(request, "body");
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> getQuery(final Request request) {
		Map<String, String> requestQueryParams = (Map<String, String>) getField(request, "query");
		requestQueryParams = requestQueryParams != null ? requestQueryParams : Map.of();

		final String url = getUrl(request);
		final Map<String, String> parsedQueryParams = QueryParamExtractor.getQueryParametersFromUri(url);

		final Map<String, String> queryParams = Stream.of(requestQueryParams.entrySet(), parsedQueryParams.entrySet())
				.flatMap(Set::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, "%s,%s"::formatted));
		return TrafficAuditorAdapterUtils.cleanMap(queryParams);
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<String>> getHeaders(final Request request) {
		return TrafficAuditorAdapterUtils.cleanMap((Map<String, List<String>>) getField(request, "headers"));
	}

	@SuppressWarnings({ "java:S3011", "java:S2259" })
	private Object getField(final Request request, final String fieldName) {
		final Field field = ReflectionUtils.findField(request.getClass(), fieldName);
		assert field != null;
		field.setAccessible(true);
		return ReflectionUtils.getField(field, request);
	}

	private static Map<String, List<String>> getHeaders(final Response response) {
		return TrafficAuditorAdapterUtils.cleanMap(response.getHeaders());
	}

	private String decryptBodyIfNeeded(final String body) {
		if (body == null || body.isBlank() || hyperwalletEncryption == null) {
			return body;
		}
		try {
			return hyperwalletEncryption.decrypt(body);
		}
		catch (final Exception e) {
			log.trace("Failed to decrypt body during Hyperwallet HTTP traffic capture", e);
			return "Failed to decrypt body";
		}
	}

	@SuppressWarnings("java:S2696")
	@Override
	public void afterPropertiesSet() {
		instance = this;
	}
}
