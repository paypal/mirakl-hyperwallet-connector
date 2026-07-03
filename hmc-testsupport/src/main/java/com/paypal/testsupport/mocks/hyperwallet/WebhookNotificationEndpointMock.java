package com.paypal.testsupport.mocks.hyperwallet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import lombok.RequiredArgsConstructor;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * MockServer endpoint mock for the Hyperwallet webhook-notifications REST endpoint.
 * <p>
 * Covers {@code GET /api/rest/v4/webhook-notifications/{token}} — the endpoint invoked by
 * the Hyperwallet SDK's {@code Hyperwallet#getWebhookEvent(String)} method when there is
 * a cache miss in {@code WebhookNotificationRetrieverImpl}.
 */
@RequiredArgsConstructor
public class WebhookNotificationEndpointMock {

	private static final String URL_PATTERN = "/api/rest/v4/webhook-notifications/%s";

	private final MockServerClient mockServerClient;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Registers a MockServer expectation that responds with {@code 200 OK} and the
	 * serialised {@link HyperwalletWebhookNotification} JSON when the SDK calls
	 * {@code GET /api/rest/v4/webhook-notifications/{token}}.
	 * @param notification the notification that the SDK should return.
	 */
	public void getWebhookNotificationRequest(final HyperwalletWebhookNotification notification) {
		mockServerClient
			.when(request().withMethod(HttpMethod.GET.name()).withPath(URL_PATTERN.formatted(notification.getToken())))
			.respond(response().withStatusCode(HttpStatus.OK.value())
				.withContentType(MediaType.APPLICATION_JSON)
				.withBody(toJson(notification)));
	}

	/**
	 * Registers a MockServer expectation that responds with {@code 404 Not Found} for the
	 * given webhook token — simulating a notification that can no longer be retrieved
	 * from the Hyperwallet API.
	 * @param webhookToken the webhook token that should return a 404.
	 */
	public void getWebhookNotificationNotFoundRequest(final String webhookToken) {
		mockServerClient.when(request().withMethod(HttpMethod.GET.name()).withPath(URL_PATTERN.formatted(webhookToken)))
			.respond(response().withStatusCode(HttpStatus.NOT_FOUND.value()));
	}

	private String toJson(final HyperwalletWebhookNotification notification) {
		try {
			return objectMapper.writeValueAsString(notification);
		}
		catch (final JsonProcessingException e) {
			throw new IllegalStateException("Failed to serialise HyperwalletWebhookNotification", e);
		}
	}

}
