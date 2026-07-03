package com.paypal.testsupport.mocks.mirakl;

import lombok.RequiredArgsConstructor;
import org.mockserver.client.MockServerClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RequiredArgsConstructor
public class MiraklConfirmSellerBillingCyclePaymentEndpointMock {

	private static final String URL = "/api/seller-billing-cycles";

	private final MockServerClient mockServerClient;

	public void confirmSellerBillingCyclePayment() {
		mockServerClient.when(request().withMethod(HttpMethod.PUT.name()).withPath(URL))
			.respond(response().withStatusCode(HttpStatus.NO_CONTENT.value()));
	}

}
