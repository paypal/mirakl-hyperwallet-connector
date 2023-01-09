package com.paypal.infrastructure.test.mocks.hyperwallet;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.util.HyperwalletJsonUtil;
import org.mockserver.client.MockServerClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.exactly;

public class UsersEndpointMock {

	private static final String URL = "/api/rest/v4/users/%s";

	private final MockServerClient mockServerClient;

	public UsersEndpointMock(final MockServerClient mockServerClient) {
		this.mockServerClient = mockServerClient;
	}

	public void updatedUser(final String userToken) {
		final String json = HyperwalletJsonUtil.toJson(createHyperwalletUser(userToken));
		final String url = getUrl(userToken);

		mockServerClient.when(request().withMethod(HttpMethod.PUT.name()).withPath(url).withBody(json))
				.respond(response().withStatusCode(HttpStatus.OK.value()).withBody(json));
	}

	public void verifyUpdatedUser(final String userToken) {
		final String json = HyperwalletJsonUtil.toJson(createHyperwalletUser(userToken));
		final String url = getUrl(userToken);

		mockServerClient.verify(request().withMethod(HttpMethod.PUT.name()).withPath(url).withBody(json), exactly(1));
	}

	private static String getUrl(final String userToken) {
		return String.format(URL, userToken);
	}

	private static HyperwalletUser createHyperwalletUser(String userToken) {
		HyperwalletUser user = new HyperwalletUser();
		user.setToken(userToken);
		user.setBusinessStakeholderVerificationStatus(
				HyperwalletUser.BusinessStakeholderVerificationStatus.READY_FOR_REVIEW);
		return user;
	}

}
