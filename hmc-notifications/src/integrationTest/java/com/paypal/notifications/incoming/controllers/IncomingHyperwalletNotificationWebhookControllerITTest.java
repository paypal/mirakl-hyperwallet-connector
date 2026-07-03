package com.paypal.notifications.incoming.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.incoming.services.NotificationProcessingQueueService;
import com.paypal.testsupport.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for {@link IncomingHyperwalletNotificationWebhookController}.
 * <p>
 * Uses Spring MockMvc to exercise the full HTTP request/response cycle (serialisation,
 * security filters disabled, routing) while mocking
 * {@link NotificationProcessingQueueService} to keep the test focused on the controller
 * layer.
 */
@AutoConfigureMockMvc(addFilters = false)
class IncomingHyperwalletNotificationWebhookControllerITTest extends AbstractIntegrationTest {

	private static final String ENDPOINT = "/webhooks/notifications";

	private static final String WEBHOOK_TOKEN = "wbh-aabbccdd-1111-2222-3333-aabbccddeeff";

	private static final String PROGRAM_TOKEN = "prg-1fb3df0d-787b-4bbd-9eb7-1d9fe8ed6c8e";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private NotificationProcessingQueueService notificationProcessingQueueService;

	// -------------------------------------------------------------------------
	// Success path
	// -------------------------------------------------------------------------

	@Test
	void receiveIncomingNotification_shouldReturn200AndDelegateToQueueService() throws Exception {
		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED");

		mockMvc
			.perform(post(ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(notification)))
			.andExpect(status().isOk());

		verify(notificationProcessingQueueService).enqueue(any(HyperwalletWebhookNotification.class));
	}

	@Test
	void receiveIncomingNotification_shouldDelegateExactPayloadToQueueService() throws Exception {
		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN,
				"PAYMENTS.UPDATED.STATUS.COMPLETED");

		mockMvc
			.perform(post(ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(notification)))
			.andExpect(status().isOk());

		verify(notificationProcessingQueueService).enqueue(any(HyperwalletWebhookNotification.class));
		verifyNoMoreInteractions(notificationProcessingQueueService);
	}

	// -------------------------------------------------------------------------
	// Edge cases: malformed / minimal payloads
	// -------------------------------------------------------------------------

	@Test
	void receiveIncomingNotification_withMinimalPayload_shouldReturn200() throws Exception {
		// Minimal body — token only; optional fields absent
		final String minimalJson = """
				{"token":"%s"}
				""".formatted(WEBHOOK_TOKEN);

		mockMvc.perform(post(ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(minimalJson))
			.andExpect(status().isOk());

		verify(notificationProcessingQueueService).enqueue(any(HyperwalletWebhookNotification.class));
	}

	@Test
	void receiveIncomingNotification_withEmptyBody_shouldReturn400() throws Exception {
		mockMvc.perform(post(ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(""))
			.andExpect(status().isBadRequest());
	}

	// -------------------------------------------------------------------------
	// Service exception propagation
	// -------------------------------------------------------------------------

	@Test
	void receiveIncomingNotification_whenQueueServiceThrows_shouldPropagateException() throws Exception {
		willThrow(new RuntimeException("DB unavailable")).given(notificationProcessingQueueService)
			.enqueue(any(HyperwalletWebhookNotification.class));

		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED");
		final String content = objectMapper.writeValueAsString(notification);

		assertThatThrownBy(
				() -> mockMvc.perform(post(ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(content)))
			.hasCauseInstanceOf(RuntimeException.class)
			.hasMessageContaining("DB unavailable");
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private static HyperwalletWebhookNotification buildNotification(final String token, final String type) {
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		notification.setToken(token);
		notification.setType(type);
		notification.setCreatedOn(new Date());
		notification.setObject(Map.of("token", "usr-001", "programToken", PROGRAM_TOKEN));
		return notification;
	}

}
