package com.paypal.notifications.incoming.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.incoming.cache.WebhookNotificationRetriever;
import com.paypal.notifications.incoming.handlers.NotificationHandler;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationStatus;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class NotificationProcessingServiceImplTest {

	private static final String TOKEN = "wbh-token-1";

	private static final String PROGRAM = "prg-token-1";

	@InjectMocks
	private NotificationProcessingServiceImpl testObj;

	@Mock
	private WebhookNotificationRetriever webhookNotificationRetrieverMock;

	@Mock
	private NotificationHandler notificationHandlerMock;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(testObj, "notificationHandlers", List.of(notificationHandlerMock));
	}

	// ── happy path ────────────────────────────────────────────────────────────────

	@Test
	void processNotification_whenHandlerSucceeds_shouldReturnSuccess() {
		final NotificationEntity entity = buildEntity(TOKEN, NotificationType.USR);
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		when(webhookNotificationRetrieverMock.get(PROGRAM, TOKEN)).thenReturn(notification);
		when(notificationHandlerMock.supports(NotificationType.USR)).thenReturn(true);

		final NotificationProcessingService.NotificationProcessingStatus result = testObj.processNotification(entity);

		assertThat(result).isEqualTo(NotificationProcessingService.NotificationProcessingStatus.SUCCESS);
		verify(notificationHandlerMock).process(entity, notification);
	}

	// ── failure paths ─────────────────────────────────────────────────────────────

	@Test
	void processNotification_whenNotificationNotInCache_shouldReturnError() {
		final NotificationEntity entity = buildEntity(TOKEN, NotificationType.USR);
		when(webhookNotificationRetrieverMock.get(PROGRAM, TOKEN)).thenReturn(null);

		final NotificationProcessingService.NotificationProcessingStatus result = testObj.processNotification(entity);

		assertThat(result).isEqualTo(NotificationProcessingService.NotificationProcessingStatus.ERROR);
		verifyNoInteractions(notificationHandlerMock);
	}

	@Test
	void processNotification_whenNoHandlerSupportsType_shouldReturnError() {
		final NotificationEntity entity = buildEntity(TOKEN, NotificationType.USR);
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		when(webhookNotificationRetrieverMock.get(PROGRAM, TOKEN)).thenReturn(notification);
		when(notificationHandlerMock.supports(NotificationType.USR)).thenReturn(false);

		final NotificationProcessingService.NotificationProcessingStatus result = testObj.processNotification(entity);

		assertThat(result).isEqualTo(NotificationProcessingService.NotificationProcessingStatus.ERROR);
		verify(notificationHandlerMock, never()).process(any(), any());
	}

	@Test
	void processNotification_whenHandlerThrows_shouldReturnError() {
		final NotificationEntity entity = buildEntity(TOKEN, NotificationType.USR);
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		when(webhookNotificationRetrieverMock.get(PROGRAM, TOKEN)).thenReturn(notification);
		when(notificationHandlerMock.supports(NotificationType.USR)).thenReturn(true);
		doThrow(new RuntimeException("handler error")).when(notificationHandlerMock).process(entity, notification);

		final NotificationProcessingService.NotificationProcessingStatus result = testObj.processNotification(entity);

		assertThat(result).isEqualTo(NotificationProcessingService.NotificationProcessingStatus.ERROR);
	}

	// ── helpers ───────────────────────────────────────────────────────────────────

	private NotificationEntity buildEntity(final String token, final NotificationType type) {
		final NotificationEntity entity = new NotificationEntity();
		entity.setWebHookToken(token);
		entity.setProgram(PROGRAM);
		entity.setNotificationType(type);
		entity.setRetryCounter(0);
		entity.setStatus(NotificationStatus.PENDING);
		return entity;
	}

}
