package com.paypal.testsupport.mocks;

import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;
import org.mockserver.model.Parameter;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public abstract class AbstractMockServerFixtures {

	protected final MockServerClient mockServerClient;

	protected AbstractMockServerFixtures(final MockServerClient mockServerClient) {
		this.mockServerClient = mockServerClient;
	}

	protected void mockGet(final String path, final String responseFile, final int statusCode,
			final Map<String, String> pathParams) {
		//@formatter:off
		mockServerClient
				.when(request()
						.withMethod("GET")
						.withPath(path)
						.withPathParameters(pathParams.entrySet()
								.stream()
								.map(entry -> new Parameter(entry.getKey(), entry.getValue()))
								.collect(Collectors.toList())))
				.respond(response()
						.withStatusCode(statusCode)
						.withContentType(MediaType.APPLICATION_JSON)
						.withBody(loadResource(responseFile)));
		//@formatter:on
	}

	protected void mockGet(final String path, final String responseFile, final int statusCode) {
		//@formatter:off
		mockServerClient
				.when(request()
						.withMethod("GET")
						.withPath(path))
				.respond(response()
						.withStatusCode(statusCode)
						.withContentType(MediaType.APPLICATION_JSON)
						.withBody(loadResource(responseFile)));
		//@formatter:on
	}

	protected void mockGet(final String path, final Map<String, List<String>> queryParameters,
			final String responseFile, final int statusCode) {
		//@formatter:off
		mockServerClient
				.when(request()
						.withMethod("GET")
						.withPath(path)
						.withQueryStringParameters(queryParameters))
				.respond(response()
						.withStatusCode(statusCode)
						.withContentType(MediaType.APPLICATION_JSON)
						.withBody(loadResource(responseFile)));
		//@formatter:on
	}

	protected String loadResource(final String responseFile) {
		return loadResource(responseFile, Map.of());
	}

	protected String loadResource(final String responseFile, final Map<String, String> bodyReplacements) {
		try {
			final File file = ResourceUtils.getFile("classpath:" + getFolder() + "/" + responseFile);
			String body = new String(Files.readAllBytes(file.toPath()));
			for (final Map.Entry<String, String> entry : bodyReplacements.entrySet()) {
				body = body.replace(entry.getKey(), entry.getValue());
			}
			return body;
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected abstract String getFolder();

}
