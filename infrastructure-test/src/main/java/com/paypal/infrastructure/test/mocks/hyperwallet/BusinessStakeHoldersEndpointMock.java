package com.paypal.infrastructure.test.mocks.hyperwallet;

import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.hyperwallet.clientsdk.util.HyperwalletJsonUtil;
import org.mockserver.client.MockServerClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.exactly;

public class BusinessStakeHoldersEndpointMock {

	private static final String URL = "/api/rest/v4/users/%s/business-stakeholders/";

	private final MockServerClient mockServerClient;

	public BusinessStakeHoldersEndpointMock(final MockServerClient mockServerClient) {
		this.mockServerClient = mockServerClient;
	}

	public void uploadDocument(final String userToken, final String token) {
		final String url = getUrl(userToken).concat(token);
		mockServerClient.when(request().withMethod(HttpMethod.PUT.name()).withPath(url))
				.respond(response().withStatusCode(HttpStatus.OK.value())
						.withBody(HyperwalletJsonUtil.toJson(new HyperwalletBusinessStakeholder())));
	}

	public void verifyUploadDocument(final String userToken, final String token) {
		final String url = getUrl(userToken).concat(token);
		mockServerClient.verify(request().withMethod(HttpMethod.PUT.name()).withPath(url), exactly(1));
	}

	private String getUrl(final String userToken) {
		return String.format(URL, userToken);
	}

}
