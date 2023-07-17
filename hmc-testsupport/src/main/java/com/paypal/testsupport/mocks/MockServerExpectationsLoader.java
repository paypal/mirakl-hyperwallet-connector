package com.paypal.testsupport.mocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockserver.client.MockServerClient;
import org.mockserver.mock.Expectation;
import org.mockserver.serialization.ObjectMapperFactory;
import org.mockserver.serialization.model.ExpectationDTO;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.NonNull;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class MockServerExpectationsLoader {

	private final ObjectMapper objectMapper = ObjectMapperFactory.createObjectMapper();

	private final MockServerClient mockServerClient;

	public MockServerExpectationsLoader(final MockServerClient mockServerClient) {
		this.mockServerClient = mockServerClient;
	}

	public void loadExpectationsFromFolder(final String folder, final String prefix,
			final Map<String, String> responseBodyReplacements) {
		//@formatter:off
		final Resource[] resources = getFolderResources(folder);
		Arrays.stream(resources)
				.map(Resource::getFilename)
				.filter(Objects::nonNull)
				.filter(filename -> filename.contains(prefix) && filename.endsWith("-expectation.json"))
				.map(fn -> "%s/%s".formatted(folder, fn))
				.map(this::loadResource)
				.map(this::loadExpectation)
				.map(e -> applyBodyReplacements(e, responseBodyReplacements))
				.forEach(mockServerClient::upsert);
		//@formatter:on
	}

	private Expectation applyBodyReplacements(final Expectation expectation,
			final Map<String, String> bodyReplacements) {
		final String newBody = applyBodyReplacements(new String(expectation.getHttpResponse().getBody().getRawBytes()),
				bodyReplacements);
		expectation.getHttpResponse().withBody(newBody);

		return expectation;
	}

	private String applyBodyReplacements(final String body, final Map<String, String> bodyReplacements) {
		String replacedBody = body;
		for (final Map.Entry<String, String> entry : bodyReplacements.entrySet()) {
			replacedBody = replacedBody.replace(entry.getKey(), entry.getValue());
		}
		return replacedBody;
	}

	@NonNull
	private Expectation loadExpectation(final String expectationJson) {
		try {
			return objectMapper.readValue(expectationJson, ExpectationDTO.class).buildObject();
		}
		catch (final JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

	@NonNull
	private Resource[] getFolderResources(final String folder) {
		final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = new Resource[0];
		try {
			resources = resolver.getResources("classpath:" + folder + "/*");
		}
		catch (final IOException e) {
			throw new IllegalStateException(e);
		}
		return resources;
	}

	private String loadResource(final String responseFile) {
		try {
			final File file = ResourceUtils.getFile("classpath:" + responseFile);
			return new String(Files.readAllBytes(file.toPath()));
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

}
