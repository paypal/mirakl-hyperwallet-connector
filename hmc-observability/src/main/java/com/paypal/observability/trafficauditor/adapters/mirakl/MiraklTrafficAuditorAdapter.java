package com.paypal.observability.trafficauditor.adapters.mirakl;

import com.paypal.observability.trafficauditor.adapters.AbstractTrafficAuditorAdapter;
import com.paypal.observability.trafficauditor.adapters.TrafficAuditorAdapter;
import com.paypal.observability.trafficauditor.adapters.TrafficAuditorTraceHolder;
import com.paypal.observability.trafficauditor.adapters.support.TrafficAuditorAdapterUtils;
import com.paypal.observability.trafficauditor.adapters.support.QueryParamExtractor;
import com.paypal.observability.trafficauditor.configuration.TrafficAuditorConfiguration;
import com.paypal.observability.trafficauditor.model.TrafficAuditorRequest;
import com.paypal.observability.trafficauditor.model.TrafficAuditorResponse;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTarget;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.util.Args;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MiraklTrafficAuditorAdapter extends AbstractTrafficAuditorAdapter<HttpRequest, HttpResponse>
		implements TrafficAuditorAdapter<HttpRequest, HttpResponse>, InitializingBean {

	private static MiraklTrafficAuditorAdapter instance;

	protected MiraklTrafficAuditorAdapter(final TrafficAuditorConfiguration trafficAuditorConfiguration,
										  final TrafficAuditorTraceHolder trafficAuditorTraceHolder) {
		super(trafficAuditorConfiguration, trafficAuditorTraceHolder);
	}

	public static MiraklTrafficAuditorAdapter get() {
		return instance;
	}

	@Override
	protected TrafficAuditorRequest doCaptureRequest(final HttpRequest request) {
		final TrafficAuditorRequest trafficAuditorRequest = new TrafficAuditorRequest();
		trafficAuditorRequest.setUrl(getUrl(request));
		trafficAuditorRequest.setHeaders(getHeaders(request.getAllHeaders()));
		trafficAuditorRequest.setQueryParameters(getQueryParameters(request));
		trafficAuditorRequest.setMethod(getMethod(request));
		trafficAuditorRequest.setBody(getRequestBody(request));

		return trafficAuditorRequest;
	}

	@Override
	protected TrafficAuditorResponse doCaptureResponse(final HttpResponse response) {
		final TrafficAuditorResponse trafficAuditorResponse = new TrafficAuditorResponse();
		trafficAuditorResponse.setResponseCode(response.getStatusLine().getStatusCode());
		trafficAuditorResponse.setHeaders(getHeaders(response.getAllHeaders()));
		trafficAuditorResponse.setBody(getResponseBody(response));

		return trafficAuditorResponse;
	}

	@Override
	protected TrafficAuditorTarget getTarget() {
		return TrafficAuditorTarget.MIRAKL;
	}

	private Map<String, List<String>> getHeaders(final Header[] headers) {
		final Map<String, List<String>> headersMap = Arrays.stream(headers)
				.collect(Collectors.toMap(Header::getName, header -> Arrays.asList(header.getValue()), this::merge));

		return TrafficAuditorAdapterUtils.cleanMap(headersMap);
	}

	@NotNull
	private List<String> merge(final List<String> v1, final List<String> v2) {
		return Stream.of(v1, v2).flatMap(List::stream).collect(Collectors.toList());
	}

	@SneakyThrows
	private String getRequestBody(final HttpRequest request) {
		if (request instanceof HttpEntityEnclosingRequest) {
			final HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			entity.writeTo(baos);
			return baos.toString();
		}

		return null;
	}

	private String getMethod(final HttpRequest request) {
		return request.getRequestLine().getMethod();
	}

	private Map<String, String> getQueryParameters(final HttpRequest request) {
		return QueryParamExtractor.getQueryParametersFromUri(getUrl(request));
	}

	private String getUrl(final HttpRequest request) {
		return request.getRequestLine().getUri();
	}

	@SneakyThrows
	private String getResponseBody(final HttpResponse response) {
		final CachedContentHttpEntityWrapper entity = new CachedContentHttpEntityWrapper(response.getEntity());
		response.setEntity(entity);
		return entity.getContentAsString();
	}

	@SuppressWarnings("java:S2696")
	@Override
	public void afterPropertiesSet() {
		instance = this;
	}

	static class CachedContentHttpEntityWrapper extends HttpEntityWrapper {

		private final byte[] content;

		public CachedContentHttpEntityWrapper(final HttpEntity wrappedEntity) throws IOException {
			super(wrappedEntity);

			content = IOUtils.toByteArray(wrappedEntity.getContent());
		}

		@Override
		public InputStream getContent() throws IOException {
			return new ByteArrayInputStream(this.content);
		}

		public String getContentAsString() {
			return new String(this.content);
		}

		@Override
		public void writeTo(final OutputStream outStream) throws IOException {
			Args.notNull(outStream, "Output stream");
			outStream.write(this.content);
			outStream.flush();
		}

	}

}
