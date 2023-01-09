package com.paypal.infrastructure.test.mocks.mirakl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.mockserver.client.MockServerClient;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

public abstract class AbstractResourceLoadingEndpointMock {

	protected final ObjectMapper mapper;

	protected final MockServerClient mockServerClient;

	@SuppressWarnings("java:S1874")
	protected AbstractResourceLoadingEndpointMock(MockServerClient mockServerClient) {
		this.mapper = new ObjectMapper();
		this.mockServerClient = mockServerClient;
		this.mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		this.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
	}

	protected String loadResource(final String responseFile) {
		try {
			final File file = ResourceUtils.getFile("classpath:" + getFolder() + "/" + responseFile);
			return new String(Files.readAllBytes(file.toPath()));
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected byte[] loadResourceAsBinary(final String responseFile) {
		try {
			final File file = ResourceUtils.getFile("classpath:" + getFolder() + "/" + responseFile);
			return Files.readAllBytes(file.toPath());
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected abstract String getFolder();

}
