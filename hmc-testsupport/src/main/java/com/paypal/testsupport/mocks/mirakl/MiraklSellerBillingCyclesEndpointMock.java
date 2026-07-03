package com.paypal.testsupport.mocks.mirakl;

import org.mockserver.client.MockServerClient;
import org.mockserver.model.Parameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class MiraklSellerBillingCyclesEndpointMock extends AbstractResourceLoadingEndpointMock {

	private static final String URL = "/api/seller-billing-cycles";

	private static final String MOCKS_FOLDER = "mocks/mirakl/seller-billing-cycles";

	private static final String PAY_OUT_STATE = "pay_out_state";

	private static final String START_DATE = "start_date";

	private static final String PAGE_TOKEN = "page_token";

	public MiraklSellerBillingCyclesEndpointMock(final MockServerClient mockServerClient) {
		super(mockServerClient);
	}

	public void getSellerBillingCycles(final String responseFile) {
		final String jsonReturned = loadResource(responseFile);

		mockServerClient.when(request().withMethod(HttpMethod.GET.name()).withPath(URL))
			.respond(response().withStatusCode(HttpStatus.OK.value()).withBody(jsonReturned));
	}

	public void getSellerBillingCyclesWithPayOutState(final String payOutState, final String responseFile) {
		final String jsonReturned = loadResource(responseFile);

		mockServerClient
			.when(request().withMethod(HttpMethod.GET.name())
				.withPath(URL)
				.withQueryStringParameter(new Parameter(PAY_OUT_STATE, payOutState)))
			.respond(response().withStatusCode(HttpStatus.OK.value()).withBody(jsonReturned));
	}

	public void getSellerBillingCyclesWithPageToken(final String pageToken, final String responseFile) {
		final String jsonReturned = loadResource(responseFile);

		mockServerClient
			.when(request().withMethod(HttpMethod.GET.name())
				.withPath(URL)
				.withQueryStringParameter(new Parameter(PAGE_TOKEN, pageToken)))
			.respond(response().withStatusCode(HttpStatus.OK.value()).withBody(jsonReturned));
	}

	public void getSellerBillingCyclesWithStartDate(final String startDate, final String payOutState,
			final String responseFile) {
		final String jsonReturned = loadResource(responseFile);

		mockServerClient
			.when(request().withMethod(HttpMethod.GET.name())
				.withPath(URL)
				.withQueryStringParameter(new Parameter(START_DATE, startDate))
				.withQueryStringParameter(new Parameter(PAY_OUT_STATE, payOutState)))
			.respond(response().withStatusCode(HttpStatus.OK.value()).withBody(jsonReturned));
	}

	@Override
	protected String getFolder() {
		return MOCKS_FOLDER;
	}

}
