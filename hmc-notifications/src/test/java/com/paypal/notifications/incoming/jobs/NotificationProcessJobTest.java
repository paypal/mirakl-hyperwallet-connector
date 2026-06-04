package com.paypal.notifications.incoming.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.paypal.notifications.incoming.services.NotificationProcessingQueueService;
import com.paypal.notifications.incoming.services.NotificationProcessingService;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationStatus;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class NotificationProcessJobTest {

	private static final String TOKEN = "wbh-token-1";

	private static final String PROGRAM = "prg-token-1";

	@InjectMocks
	private NotificationProcessJob testObj;

	@Mock
	private NotificationProcessingQueueService notificationProcessingQueueServiceMock;

	@Mock
	private NotificationProcessingService notificationProcessingServiceMock;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	// ── happy path ────────────────────────────────────────────────────────────────

	@Test
	void execute_whenNotificationIsProcessedSuccessfully_shouldUpdateStatusToSuccess() {
		final NotificationEntity entity = buildEntity(TOKEN, NotificationType.USR);
		when(notificationProcessingQueueServiceMock.fetchNextBatch(anyInt())).thenReturn(List.of(entity));
		when(notificationProcessingServiceMock.processNotification(entity))
				.thenReturn(NotificationProcessingService.NotificationProcessingStatus.SUCCESS);

		testObj.execute(jobExecutionContextMock);

		verifyStatusUpdate(Map.of(TOKEN, NotificationProcessingService.NotificationProcessingStatus.SUCCESS));
		verify(notificationProcessingServiceMock).processNotification(entity);
	}

	// ── failure path ──────────────────────────────────────────────────────────────

	@Test
	void execute_whenProcessingFails_shouldUpdateStatusToFailed() {
		final NotificationEntity entity = buildEntity(TOKEN, NotificationType.USR);
		when(notificationProcessingQueueServiceMock.fetchNextBatch(anyInt())).thenReturn(List.of(entity));
		when(notificationProcessingServiceMock.processNotification(entity))
				.thenReturn(NotificationProcessingService.NotificationProcessingStatus.ERROR);

		testObj.execute(jobExecutionContextMock);

		verifyStatusUpdate(Map.of(TOKEN, NotificationProcessingService.NotificationProcessingStatus.ERROR));
	}

	// ── edge cases ────────────────────────────────────────────────────────────────

	@Test
	void execute_whenBatchIsEmpty_shouldNotCallUpdateStatus() {
		when(notificationProcessingQueueServiceMock.fetchNextBatch(anyInt())).thenReturn(List.of());

		testObj.execute(jobExecutionContextMock);

		verify(notificationProcessingQueueServiceMock, never()).updateStatus(any());
		verifyNoInteractions(notificationProcessingServiceMock);
	}

	@Test
	void execute_shouldDelegateEachEntityToProcessingService() {
		final NotificationEntity entity1 = buildEntity("tok-1", NotificationType.USR);
		final NotificationEntity entity2 = buildEntity("tok-2", NotificationType.USR);
		when(notificationProcessingQueueServiceMock.fetchNextBatch(anyInt())).thenReturn(List.of(entity1, entity2));
		when(notificationProcessingServiceMock.processNotification(entity1))
				.thenReturn(NotificationProcessingService.NotificationProcessingStatus.SUCCESS);
		when(notificationProcessingServiceMock.processNotification(entity2))
				.thenReturn(NotificationProcessingService.NotificationProcessingStatus.ERROR);

		testObj.execute(jobExecutionContextMock);

		verify(notificationProcessingServiceMock).processNotification(entity1);
		verify(notificationProcessingServiceMock).processNotification(entity2);
		verifyStatusUpdate(Map.of("tok-1", NotificationProcessingService.NotificationProcessingStatus.SUCCESS, "tok-2",
				NotificationProcessingService.NotificationProcessingStatus.ERROR));
	}

	// ── helpers ───────────────────────────────────────────────────────────────────

	private NotificationEntity buildEntity(final String token, final NotificationType type) {
		final NotificationEntity entity = new NotificationEntity();
		entity.setWebHookToken(token);
		entity.setProgram(PROGRAM);
		entity.setNotificationType(type);
		entity.setStatus(NotificationStatus.PENDING);
		return entity;
	}

	@SuppressWarnings("unchecked")
	private void verifyStatusUpdate(
			final Map<String, NotificationProcessingService.NotificationProcessingStatus> expected) {
		final ArgumentCaptor<Map<String, NotificationProcessingService.NotificationProcessingStatus>> captor = ArgumentCaptor
				.forClass(Map.class);
		verify(notificationProcessingQueueServiceMock).updateStatus(captor.capture());
		assertThat(captor.getValue()).containsExactlyInAnyOrderEntriesOf(expected);
	}

}
