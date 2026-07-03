package com.paypal.notifications.incoming.services;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.notifications.incoming.cache.WebhookNotificationRetriever;
import com.paypal.notifications.incoming.services.converters.NotificationConverter;
import com.paypal.notifications.storage.repositories.NotificationEntityRepository;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationStatus;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import com.paypal.notifications.storage.services.NotificationStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationProcessingQueueServiceImplTest {

	private static final String WEBHOOK_TOKEN = "wbh-token-1";

	private static final String OBJECT_TOKEN = "usr-token-1";

	private static final String PROGRAM_TOKEN = "prg-token-1";

	@InjectMocks
	private NotificationProcessingQueueServiceImpl testObj;

	@Mock
	private NotificationConverter notificationConverterMock;

	@Mock
	private NotificationStorageService notificationStorageServiceMock;

	@Mock
	private NotificationEntityRepository notificationEntityRepositoryMock;

	@Mock
	private WebhookNotificationRetriever webhookNotificationRetrieverMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@BeforeEach
	void setUp() {
		setInitialRetryDelay(Duration.ofMinutes(30));
		setBackoffMultiplier(2.0);
	}

	// ── enqueue ──────────────────────────────────────────────────────────────────

	@Test
	void enqueue_whenNotDuplicateAndNotOutdated_shouldSaveWithPendingStatusAndProgramToken() {
		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN, OBJECT_TOKEN,
				PROGRAM_TOKEN);
		final NotificationEntity entity = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR, new Date(), 0);

		when(notificationConverterMock.convert(notification)).thenReturn(entity);
		when(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN)).thenReturn(Optional.empty());
		when(notificationStorageServiceMock.existsNewerNotificationByObjectToken(eq(OBJECT_TOKEN), any()))
			.thenReturn(false);
		when(notificationEntityRepositoryMock.findActiveNotificationsSupersededBy(eq(OBJECT_TOKEN),
				eq(NotificationType.USR), any()))
			.thenReturn(List.of());

		testObj.enqueue(notification);

		verify(notificationStorageServiceMock).saveNotification(entity);
		assertThat(entity.getStatus()).isEqualTo(NotificationStatus.PENDING);
		assertThat(entity.getRetryCounter()).isZero();
		assertThat(entity.getProgram()).isEqualTo(PROGRAM_TOKEN);
	}

	@Test
	void enqueue_whenNotDuplicateAndNotOutdated_shouldPopulateCache() {
		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN, OBJECT_TOKEN,
				PROGRAM_TOKEN);
		final NotificationEntity entity = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR, new Date(), 0);

		when(notificationConverterMock.convert(notification)).thenReturn(entity);
		when(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN)).thenReturn(Optional.empty());
		when(notificationStorageServiceMock.existsNewerNotificationByObjectToken(eq(OBJECT_TOKEN), any()))
			.thenReturn(false);
		when(notificationEntityRepositoryMock.findActiveNotificationsSupersededBy(eq(OBJECT_TOKEN),
				eq(NotificationType.USR), any()))
			.thenReturn(List.of());

		testObj.enqueue(notification);

		verify(webhookNotificationRetrieverMock).put(notification);
	}

	@Test
	void enqueue_whenDuplicate_shouldDiscardAndNotSave() {
		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN, OBJECT_TOKEN,
				PROGRAM_TOKEN);
		final NotificationEntity entity = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR, new Date(), 0);

		when(notificationConverterMock.convert(notification)).thenReturn(entity);
		when(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN))
			.thenReturn(Optional.of(new NotificationEntity()));

		testObj.enqueue(notification);

		verify(notificationStorageServiceMock, never()).saveNotification(any());
		verify(webhookNotificationRetrieverMock, never()).put(any());
	}

	@Test
	void enqueue_whenOutdated_shouldDiscardAndNotSave() {
		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN, OBJECT_TOKEN,
				PROGRAM_TOKEN);
		final NotificationEntity entity = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR, new Date(), 0);

		when(notificationConverterMock.convert(notification)).thenReturn(entity);
		when(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN)).thenReturn(Optional.empty());
		when(notificationStorageServiceMock.existsNewerNotificationByObjectToken(eq(OBJECT_TOKEN), any()))
			.thenReturn(true);

		testObj.enqueue(notification);

		verify(notificationStorageServiceMock, never()).saveNotification(any());
		verify(webhookNotificationRetrieverMock, never()).put(any());
	}

	@Test
	void enqueue_whenNotificationBodyIsNull_shouldStoreNullProgram() {
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		notification.setToken(WEBHOOK_TOKEN);
		final NotificationEntity entity = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR, new Date(), 0);

		when(notificationConverterMock.convert(notification)).thenReturn(entity);
		when(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN)).thenReturn(Optional.empty());
		when(notificationStorageServiceMock.existsNewerNotificationByObjectToken(eq(OBJECT_TOKEN), any()))
			.thenReturn(false);
		when(notificationEntityRepositoryMock.findActiveNotificationsSupersededBy(eq(OBJECT_TOKEN),
				eq(NotificationType.USR), any()))
			.thenReturn(List.of());

		testObj.enqueue(notification);

		verify(notificationStorageServiceMock).saveNotification(entity);
		assertThat(entity.getProgram()).isNull();
	}

	// ── enqueue — superseding behaviour ───────────────────────────────────────────

	@Test
	void enqueue_whenNewerNotificationArrivesForSameObjectAndType_shouldMarkOlderPendingAsOutdated() {
		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN, OBJECT_TOKEN,
				PROGRAM_TOKEN);
		final NotificationEntity incomingEntity = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR,
				new Date(2_000_000L), 0);

		final NotificationEntity olderPending = buildEntity("wbh-older-1", OBJECT_TOKEN, NotificationType.USR,
				new Date(1_000_000L), 0);
		olderPending.setStatus(NotificationStatus.PENDING);

		when(notificationConverterMock.convert(notification)).thenReturn(incomingEntity);
		when(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN)).thenReturn(Optional.empty());
		when(notificationStorageServiceMock.existsNewerNotificationByObjectToken(eq(OBJECT_TOKEN), any()))
			.thenReturn(false);
		when(notificationEntityRepositoryMock.findActiveNotificationsSupersededBy(eq(OBJECT_TOKEN),
				eq(NotificationType.USR), any()))
			.thenReturn(List.of(olderPending));

		testObj.enqueue(notification);

		verify(notificationEntityRepositoryMock).updateStatusByWebHookToken("wbh-older-1", NotificationStatus.OUTDATED);
		verify(notificationStorageServiceMock).saveNotification(incomingEntity);
	}

	@Test
	void enqueue_whenNewerNotificationArrivesForSameObjectAndType_shouldMarkOlderRetryingAsOutdated() {
		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN, OBJECT_TOKEN,
				PROGRAM_TOKEN);
		final NotificationEntity incomingEntity = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR,
				new Date(3_000_000L), 0);

		final NotificationEntity olderRetrying = buildEntity("wbh-retrying-1", OBJECT_TOKEN, NotificationType.USR,
				new Date(1_000_000L), 2);
		olderRetrying.setStatus(NotificationStatus.RETRYING);

		when(notificationConverterMock.convert(notification)).thenReturn(incomingEntity);
		when(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN)).thenReturn(Optional.empty());
		when(notificationStorageServiceMock.existsNewerNotificationByObjectToken(eq(OBJECT_TOKEN), any()))
			.thenReturn(false);
		when(notificationEntityRepositoryMock.findActiveNotificationsSupersededBy(eq(OBJECT_TOKEN),
				eq(NotificationType.USR), any()))
			.thenReturn(List.of(olderRetrying));

		testObj.enqueue(notification);

		verify(notificationEntityRepositoryMock).updateStatusByWebHookToken("wbh-retrying-1",
				NotificationStatus.OUTDATED);
		verify(notificationStorageServiceMock).saveNotification(incomingEntity);
	}

	@Test
	void enqueue_whenNewerNotificationSupersededMultipleOlderOnes_shouldMarkAllAsOutdated() {
		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN, OBJECT_TOKEN,
				PROGRAM_TOKEN);
		final NotificationEntity incomingEntity = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR,
				new Date(5_000_000L), 0);

		final NotificationEntity older1 = buildEntity("wbh-older-1", OBJECT_TOKEN, NotificationType.USR,
				new Date(1_000_000L), 0);
		older1.setStatus(NotificationStatus.PENDING);
		final NotificationEntity older2 = buildEntity("wbh-older-2", OBJECT_TOKEN, NotificationType.USR,
				new Date(2_000_000L), 1);
		older2.setStatus(NotificationStatus.RETRYING);

		when(notificationConverterMock.convert(notification)).thenReturn(incomingEntity);
		when(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN)).thenReturn(Optional.empty());
		when(notificationStorageServiceMock.existsNewerNotificationByObjectToken(eq(OBJECT_TOKEN), any()))
			.thenReturn(false);
		when(notificationEntityRepositoryMock.findActiveNotificationsSupersededBy(eq(OBJECT_TOKEN),
				eq(NotificationType.USR), any()))
			.thenReturn(List.of(older1, older2));

		testObj.enqueue(notification);

		verify(notificationEntityRepositoryMock).updateStatusByWebHookToken("wbh-older-1", NotificationStatus.OUTDATED);
		verify(notificationEntityRepositoryMock).updateStatusByWebHookToken("wbh-older-2", NotificationStatus.OUTDATED);
		verify(notificationStorageServiceMock).saveNotification(incomingEntity);
	}

	@Test
	void enqueue_whenNoActiveOlderNotificationsExist_shouldNotCallUpdateStatus() {
		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN, OBJECT_TOKEN,
				PROGRAM_TOKEN);
		final NotificationEntity entity = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR, new Date(), 0);

		when(notificationConverterMock.convert(notification)).thenReturn(entity);
		when(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN)).thenReturn(Optional.empty());
		when(notificationStorageServiceMock.existsNewerNotificationByObjectToken(eq(OBJECT_TOKEN), any()))
			.thenReturn(false);
		when(notificationEntityRepositoryMock.findActiveNotificationsSupersededBy(eq(OBJECT_TOKEN),
				eq(NotificationType.USR), any()))
			.thenReturn(List.of());

		testObj.enqueue(notification);

		verify(notificationEntityRepositoryMock, never()).updateStatusByWebHookToken(any(),
				eq(NotificationStatus.OUTDATED));
		verify(notificationStorageServiceMock).saveNotification(entity);
	}

	@Test
	void enqueue_whenOutdatedGuardTriggered_shouldNotCheckForSupersededNotifications() {
		// The incoming notification is itself outdated (a newer one already in the DB),
		// so superseding-check must not run at all.
		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN, OBJECT_TOKEN,
				PROGRAM_TOKEN);
		final NotificationEntity entity = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR, new Date(), 0);

		when(notificationConverterMock.convert(notification)).thenReturn(entity);
		when(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN)).thenReturn(Optional.empty());
		when(notificationStorageServiceMock.existsNewerNotificationByObjectToken(eq(OBJECT_TOKEN), any()))
			.thenReturn(true);

		testObj.enqueue(notification);

		verify(notificationEntityRepositoryMock, never()).findActiveNotificationsSupersededBy(any(), any(), any());
		verify(notificationEntityRepositoryMock, never()).updateStatusByWebHookToken(any(), any());
	}

	// ── fetchNextBatch ────────────────────────────────────────────────────────────

	@Test
	void fetchNextBatch_shouldDelegateToRepositoryWithCurrentTimeAndPageRequest() {
		final List<NotificationEntity> expected = List.of(new NotificationEntity());
		when(notificationEntityRepositoryMock.findNextBatchForProcessing(any(Date.class), eq(PageRequest.of(0, 10))))
			.thenReturn(expected);

		final List<NotificationEntity> result = testObj.fetchNextBatch(10);

		assertThat(result).isSameAs(expected);
	}

	// ── updateStatus ──────────────────────────────────────────────────────────────

	@Test
	void updateStatus_whenSuccess_shouldUpdateStatusToSuccessWithoutRetryCounter() {
		testObj.updateStatus(Map.of(WEBHOOK_TOKEN, NotificationProcessingService.NotificationProcessingStatus.SUCCESS));

		verify(notificationEntityRepositoryMock).updateStatusByWebHookToken(WEBHOOK_TOKEN, NotificationStatus.SUCCESS);
		verifyNoMoreInteractions(notificationEntityRepositoryMock);
		verifyNoInteractions(mailNotificationUtilMock);
	}

	@Test
	void updateStatus_whenError_andRetriesRemain_shouldIncrementCounterAndSetRetrying() {
		setMaxRetries(5);
		final NotificationEntity entity = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR, new Date(), 1);
		when(notificationEntityRepositoryMock.findByWebHookToken(WEBHOOK_TOKEN)).thenReturn(Optional.of(entity));

		testObj.updateStatus(Map.of(WEBHOOK_TOKEN, NotificationProcessingService.NotificationProcessingStatus.ERROR));

		verify(notificationEntityRepositoryMock).updateStatusAndRetryCounterByWebHookToken(eq(WEBHOOK_TOKEN),
				eq(NotificationStatus.RETRYING), eq(2), any(Date.class), any(Date.class));
		verifyNoInteractions(mailNotificationUtilMock);
	}

	@Test
	void updateStatus_whenError_andMaxRetriesReached_shouldSetFailedAndSendEmail() {
		setMaxRetries(5);
		final NotificationEntity entity = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR, new Date(), 4);
		when(notificationEntityRepositoryMock.findByWebHookToken(WEBHOOK_TOKEN)).thenReturn(Optional.of(entity));

		testObj.updateStatus(Map.of(WEBHOOK_TOKEN, NotificationProcessingService.NotificationProcessingStatus.ERROR));

		verify(notificationEntityRepositoryMock).updateStatusAndRetryCounterByWebHookToken(eq(WEBHOOK_TOKEN),
				eq(NotificationStatus.FAILED), eq(5), any(Date.class), isNull());
		verify(mailNotificationUtilMock).sendPlainTextEmail(any(), any());
	}

	@Test
	void updateStatus_whenError_andRetriesRemain_nextRetryDateShouldGrowExponentially() {
		setMaxRetries(10);
		setInitialRetryDelay(Duration.ofMinutes(10));
		setBackoffMultiplier(2.0);

		// Simulate attempt 1: retryCounter goes from 0 → 1, delay = 10min * 2^0 = 10min
		final NotificationEntity entityAttempt1 = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR,
				new Date(), 0);
		when(notificationEntityRepositoryMock.findByWebHookToken(WEBHOOK_TOKEN))
			.thenReturn(Optional.of(entityAttempt1));

		final long before1 = System.currentTimeMillis();
		testObj.updateStatus(Map.of(WEBHOOK_TOKEN, NotificationProcessingService.NotificationProcessingStatus.ERROR));
		final long after1 = System.currentTimeMillis();

		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Date> nextRetryCaptor1 = ArgumentCaptor.forClass(Date.class);
		verify(notificationEntityRepositoryMock).updateStatusAndRetryCounterByWebHookToken(eq(WEBHOOK_TOKEN),
				eq(NotificationStatus.RETRYING), eq(1), any(Date.class), nextRetryCaptor1.capture());

		final long nextRetry1Ms = nextRetryCaptor1.getValue().getTime();
		assertThat(nextRetry1Ms).isBetween(before1 + Duration.ofMinutes(10).toMillis(),
				after1 + Duration.ofMinutes(10).toMillis());

		// Simulate attempt 2: retryCounter goes from 1 → 2, delay = 10min * 2^1 = 20min
		reset(notificationEntityRepositoryMock);
		final NotificationEntity entityAttempt2 = buildEntity(WEBHOOK_TOKEN, OBJECT_TOKEN, NotificationType.USR,
				new Date(), 1);
		when(notificationEntityRepositoryMock.findByWebHookToken(WEBHOOK_TOKEN))
			.thenReturn(Optional.of(entityAttempt2));

		final long before2 = System.currentTimeMillis();
		testObj.updateStatus(Map.of(WEBHOOK_TOKEN, NotificationProcessingService.NotificationProcessingStatus.ERROR));
		final long after2 = System.currentTimeMillis();

		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Date> nextRetryCaptor2 = ArgumentCaptor.forClass(Date.class);
		verify(notificationEntityRepositoryMock).updateStatusAndRetryCounterByWebHookToken(eq(WEBHOOK_TOKEN),
				eq(NotificationStatus.RETRYING), eq(2), any(Date.class), nextRetryCaptor2.capture());

		final long nextRetry2Ms = nextRetryCaptor2.getValue().getTime();
		assertThat(nextRetry2Ms).isBetween(before2 + Duration.ofMinutes(20).toMillis(),
				after2 + Duration.ofMinutes(20).toMillis());

		// The second delay must be greater than the first
		assertThat(nextRetry2Ms - after2).isGreaterThan(nextRetry1Ms - before1);
	}

	// ── helpers ───────────────────────────────────────────────────────────────────

	private void setMaxRetries(final int value) {
		ReflectionTestUtils.setField(testObj, "maxRetries", value);
	}

	private void setInitialRetryDelay(final Duration value) {
		ReflectionTestUtils.setField(testObj, "initialRetryDelay", value);
	}

	private void setBackoffMultiplier(final double value) {
		ReflectionTestUtils.setField(testObj, "backoffMultiplier", value);
	}

	private HyperwalletWebhookNotification buildNotification(final String token, final String objectToken,
			final String programToken) {
		final HyperwalletWebhookNotification n = new HyperwalletWebhookNotification();
		n.setToken(token);
		n.setObject(Map.of("token", objectToken, "programToken", programToken));
		return n;
	}

	private NotificationEntity buildEntity(final String webHookToken, final String objectToken,
			final NotificationType notificationType, final Date creationDate, final int retryCounter) {
		final NotificationEntity entity = new NotificationEntity();
		entity.setWebHookToken(webHookToken);
		entity.setObjectToken(objectToken);
		entity.setNotificationType(notificationType);
		entity.setCreationDate(creationDate);
		entity.setRetryCounter(retryCounter);
		return entity;
	}

}
