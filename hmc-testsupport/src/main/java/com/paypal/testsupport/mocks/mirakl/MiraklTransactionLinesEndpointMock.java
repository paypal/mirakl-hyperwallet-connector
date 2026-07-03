package com.paypal.testsupport.mocks.mirakl;

import org.mockserver.client.MockServerClient;
import org.mockserver.model.Parameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class MiraklTransactionLinesEndpointMock extends AbstractResourceLoadingEndpointMock {

	private static final String URL = "/api/sellerpayment/transactions_logs";

	private static final String MOCKS_FOLDER = "mocks/mirakl/transaction-lines";

	public MiraklTransactionLinesEndpointMock(final MockServerClient mockServerClient) {
		super(mockServerClient);
	}

	public void getTransactionLines(final String responseFile) {
		final String jsonReturned = loadResource(responseFile);

		//@formatter:off
		mockServerClient
				.when(request()
						.withMethod(HttpMethod.GET.name())
						.withPath(URL))
					.respond(response()
						.withStatusCode(HttpStatus.OK.value())
						.withBody(jsonReturned));
		//@formatter:on
	}

	public void getTransactionLinesWithPageToken(final String pageToken, final String responseFile) {
		final String jsonReturned = loadResource(responseFile);

		//@formatter:off
		mockServerClient
				.when(request()
						.withMethod(HttpMethod.GET.name())
						.withPath(URL)
						.withQueryStringParameter(new Parameter("page_token", pageToken)))
					.respond(response()
						.withStatusCode(HttpStatus.OK.value())
						.withBody(jsonReturned));
		//@formatter:on
	}

	@Override
	protected String getFolder() {
		return MOCKS_FOLDER;
	}

}
