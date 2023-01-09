package com.paypal.infrastructure.test.mocks.hyperwallet;

import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.hyperwallet.clientsdk.util.HyperwalletJsonUtil;
import com.nimbusds.jose.shaded.gson.Gson;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class PaymentsEndpointMock {

	private static final String URL = "/api/rest/v4/payments";

	private static final String PARAMETER_CLIENT_PAYMENT_ID = "clientPaymentId";

	private final MockServerClient mockServerClient;

	public PaymentsEndpointMock(MockServerClient mockServerClient) {
		this.mockServerClient = mockServerClient;
	}

	public void createPaymentRequest(final HyperwalletPayment payment) {
		mockServerClient
				.when(request().withMethod(HttpMethod.POST.name()).withPath(URL)
						.withBody(HyperwalletJsonUtil.toJson(payment)))
				.respond(
						response().withStatusCode(HttpStatus.OK.value()).withBody(HyperwalletJsonUtil.toJson(payment)));
	}

	public void createPaymentErrorRequest(final HyperwalletPayment payment) {
		mockServerClient
				.when(request().withMethod(HttpMethod.POST.name()).withPath(URL)
						.withBody(HyperwalletJsonUtil.toJson(payment)))
				.respond(response().withStatusCode(HttpStatus.BAD_REQUEST.value()));
	}

	public void listPaymentsRequest(final String clientPaymentId, final Collection<String> statuses) {
		final HyperwalletList<HyperwalletPayment> response = createHyperwalletListWithStatus(statuses);
		mockServerClient
				.when(request().withMethod(HttpMethod.GET.name()).withPath(URL)
						.withQueryStringParameter(PARAMETER_CLIENT_PAYMENT_ID, clientPaymentId))
				.respond(response().withStatusCode(HttpStatus.OK.value()).withContentType(MediaType.APPLICATION_JSON)
						.withBody(new Gson().toJson(response)));
	}

	public void listPaymentsErrorRequest(final String clientPaymentId) {
		mockServerClient
				.when(request().withMethod(HttpMethod.GET.name()).withPath(URL)
						.withQueryStringParameter(PARAMETER_CLIENT_PAYMENT_ID, clientPaymentId))
				.respond(response().withStatusCode(HttpStatus.BAD_REQUEST.value())
						.withContentType(MediaType.APPLICATION_JSON));
	}

	private HyperwalletList<HyperwalletPayment> createHyperwalletListWithStatus(final Collection<String> statuses) {
		final HyperwalletList<HyperwalletPayment> response = new HyperwalletList<>();
		final List<HyperwalletPayment> dataWithAllFailures = statuses.stream()
				.map(status -> new HyperwalletPayment().status(status)).collect(Collectors.toUnmodifiableList());
		response.setData(dataWithAllFailures);
		return response;
	}

}
