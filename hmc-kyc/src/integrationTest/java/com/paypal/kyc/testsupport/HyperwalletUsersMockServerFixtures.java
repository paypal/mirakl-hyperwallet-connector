package com.paypal.kyc.testsupport;

import com.paypal.testsupport.mocks.AbstractMockServerFixtures;
import org.mockserver.client.MockServerClient;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class HyperwalletUsersMockServerFixtures extends AbstractMockServerFixtures {

	private static final String ISO_8601_DATE_FORMAT_REGEX = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z";

	private final String mockServerUrl;

	public HyperwalletUsersMockServerFixtures(final MockServerClient mockServerClient, final String mockServerUrl) {
		super(mockServerClient);
		this.mockServerUrl = mockServerUrl;
	}

	@Override
	protected String getFolder() {
		return "mocks/hyperwallet/users";
	}

	public void mockGetHyperwalletUsers(final Date from, final Date to) {
		//@formatter:off
		final Map<String, List<String>> queryParameters = Map.of("createdAfter", List.of(ISO_8601_DATE_FORMAT_REGEX),
				"createdBefore", List.of(ISO_8601_DATE_FORMAT_REGEX));
		mockGet("/api/rest/v4/users",
				Map.of("createdAfter", List.of(ISO_8601_DATE_FORMAT_REGEX),
						"createdBefore", List.of(ISO_8601_DATE_FORMAT_REGEX),
						"after", List.of("usr-73080ef1-79b1-4faf-8794-88d485170cdf")),
				"hyperwallet-users-01.json", 200);
		mockGet("/api/rest/v4/users",
				Map.of("createdAfter", List.of(ISO_8601_DATE_FORMAT_REGEX),
						"createdBefore", List.of(ISO_8601_DATE_FORMAT_REGEX),
						"after", List.of("usr-d2c0d3f3-ba8b-4f34-b544-8206267db547")),
				"hyperwallet-users-02.json", 200);
		mockGet("/api/rest/v4/users", queryParameters, "hyperwallet-users-00.json", 200);
		//@formatter:on
	}

	@Override
	protected String loadResource(final String responseFile) {
		return super.loadResource(responseFile, Map.of("https://uat-api.paylution.com", mockServerUrl));
	}

}
