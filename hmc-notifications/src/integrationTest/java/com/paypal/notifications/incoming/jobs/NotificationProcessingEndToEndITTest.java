package com.paypal.notifications.incoming.jobs;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.notifications.incoming.handlers.NotificationHandler;
import com.paypal.notifications.incoming.services.NotificationProcessingQueueService;
import com.paypal.notifications.storage.repositories.NotificationEntityRepository;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationStatus;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import com.paypal.testsupport.AbstractMockEnabledIntegrationTest;
import com.paypal.testsupport.TestJobExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

/**
 * Integration test for the end-to-end notification processing engine.
 * <p>
 * Tests the full cycle:
 * <ol>
 * <li>Enqueue via {@link NotificationProcessingQueueService}</li>
 * <li>Process via {@link NotificationProcessJob} triggered through
 * {@link TestJobExecutor}</li>
 * <li>Retry / failure handling via exponential back-off in the queue service</li>
 * </ol>
 * <p>
 * The Hyperwallet SDK calls are intercepted by MockServer via
 * {@link com.paypal.testsupport.mocks.hyperwallet.WebhookNotificationEndpointMock}. The
 * {@link NotificationHandler} implementations are mocked so the test is not coupled to
 * the KYC/Invoices domain logic.
 * <p>
 * Retry configuration is overridden via {@code @TestPropertySource} to use a 2 s initial
 * delay and a multiplier of 1.0 so back-off intervals stay at 2 s throughout the test.
 * Tests that need the back-off window to expire sleep for 2 100 ms; tests that must NOT
 * see a second pick-up simply run the job immediately without sleeping.
 */
@TestPropertySource(properties = { "hmc.webhooks.retries.max-retries=3",
		"hmc.webhooks.retries.initial-retry-delay=PT2S", "hmc.webhooks.retries.backoff-multiplier=1.0" })
class NotificationProcessingEndToEndITTest extends AbstractMockEnabledIntegrationTest {

	// ------------------------------------------------------------------
	// Shared fixture tokens
	// ------------------------------------------------------------------

	private static final String PROGRAM_TOKEN = "DEFAULT";

	private static final String WEBHOOK_TOKEN_USR = "wbh-usr-0001-aaaa-bbbb-cccc-ddddeeee0001";

	private static final String WEBHOOK_TOKEN_PMT = "wbh-pmt-0002-aaaa-bbbb-cccc-ddddeeee0002";

	private static final String WEBHOOK_TOKEN_RETRY = "wbh-usr-retry-aaaa-bbbb-cccc-ddddeee0003";

	private static final String WEBHOOK_TOKEN_FAIL = "wbh-usr-fail-aaaa-bbbb-cccc-ddddeee0004";

	private static final String WEBHOOK_TOKEN_SDK_MISS = "wbh-usr-sdk-miss-aaaa-0005";

	private static final String WEBHOOK_TOKEN_DUPLICATE = "wbh-dup-0006";

	private static final String WEBHOOK_TOKEN_OUTDATED = "wbh-outdated-0007";

	// ------------------------------------------------------------------
	// Spring beans under test
	// ------------------------------------------------------------------

	@Autowired
	private NotificationProcessingQueueService notificationProcessingQueueService;

	@Autowired
	private NotificationEntityRepository notificationEntityRepository;

	@Autowired
	private TestJobExecutor testJobExecutor;

	// ------------------------------------------------------------------
	// Collaborators: mocked / spied
	// ------------------------------------------------------------------

	/**
	 * Mocked handler registered as a Spring bean. All tests configure this via
	 * {@code when(notificationHandler.supports(...))} as needed.
	 */
	@MockitoBean
	private NotificationHandler notificationHandler;

	@MockitoSpyBean
	private MailNotificationUtil mailNotificationUtil;

	// ------------------------------------------------------------------
	// Tear down
	// ------------------------------------------------------------------

	@AfterEach
	void cleanDatabase() {
		notificationEntityRepository.deleteAll();
	}

	// ==================================================================
	// 1. Enqueue tests — duplicate / outdated guards
	// ==================================================================

	@Test
	void enqueue_whenDuplicateWebhookToken_shouldDiscardSecondNotification() {
		final HyperwalletWebhookNotification first = buildNotification(WEBHOOK_TOKEN_DUPLICATE,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", "usr-001", new Date(1_000_000L));
		final HyperwalletWebhookNotification duplicate = buildNotification(WEBHOOK_TOKEN_DUPLICATE,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", "usr-001", new Date(2_000_000L));

		notificationProcessingQueueService.enqueue(first);
		notificationProcessingQueueService.enqueue(duplicate);

		final List<NotificationEntity> stored = notificationEntityRepository
			.findNotificationsByWebHookToken(WEBHOOK_TOKEN_DUPLICATE);
		assertThat(stored).hasSize(1);
	}

	@Test
	void enqueue_whenIncomingIsOlderThanAlreadyQueuedNotification_shouldDiscardIncoming() {
		// A newer notification for the same object is already in the queue.
		// The incoming (older) one must be silently discarded.
		final HyperwalletWebhookNotification newer = buildNotification(WEBHOOK_TOKEN_OUTDATED,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", "usr-outdated", new Date(2_000_000L));
		final HyperwalletWebhookNotification older = buildNotification("wbh-old-for-outdated-test",
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", "usr-outdated", new Date(1_000_000L));

		notificationProcessingQueueService.enqueue(newer);
		notificationProcessingQueueService.enqueue(older); // should be discarded

		final List<NotificationEntity> stored = notificationEntityRepository.findAll();
		assertThat(stored).hasSize(1);
		assertThat(stored.get(0).getWebHookToken()).isEqualTo(WEBHOOK_TOKEN_OUTDATED);
	}

	@Test
	void enqueue_whenValidNotification_shouldPersistWithPendingStatus() {
		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN_USR,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", "usr-001", new Date());

		notificationProcessingQueueService.enqueue(notification);

		final List<NotificationEntity> stored = notificationEntityRepository
			.findNotificationsByWebHookToken(WEBHOOK_TOKEN_USR);
		assertThat(stored).hasSize(1);
		assertThat(stored.get(0).getStatus()).isEqualTo(NotificationStatus.PENDING);
		assertThat(stored.get(0).getRetryCounter()).isZero();
		assertThat(stored.get(0).getProgram()).isEqualTo(PROGRAM_TOKEN);
	}

	// ==================================================================
	// 2. Enqueue tests — superseding of already-queued notifications
	// ==================================================================

	@Test
	void enqueue_whenNewerNotificationArrivesAndOlderIsPending_shouldMarkOlderAsOutdated() {
		final String objectToken = "usr-supersede-pending";
		final String type = "USERS.UPDATED.VERIFICATION_STATUS.REQUIRED";

		final HyperwalletWebhookNotification older = buildNotification("wbh-older-pending-001", type, objectToken,
				new Date(1_000_000L));
		final HyperwalletWebhookNotification newer = buildNotification("wbh-newer-001", type, objectToken,
				new Date(2_000_000L));

		notificationProcessingQueueService.enqueue(older);
		notificationProcessingQueueService.enqueue(newer);

		final NotificationEntity olderEntity = getSingleEntity("wbh-older-pending-001");
		final NotificationEntity newerEntity = getSingleEntity("wbh-newer-001");

		assertThat(olderEntity.getStatus()).isEqualTo(NotificationStatus.OUTDATED);
		assertThat(newerEntity.getStatus()).isEqualTo(NotificationStatus.PENDING);
	}

	@Test
	void enqueue_whenNewerNotificationArrivesAndOlderIsRetrying_shouldMarkOlderAsOutdated() {
		final String objectToken = "usr-supersede-retrying";
		final String type = "USERS.UPDATED.VERIFICATION_STATUS.REQUIRED";

		// Insert an older notification directly in RETRYING state (simulating a
		// notification that already failed once and is waiting for its back-off window)
		final NotificationEntity olderRetrying = new NotificationEntity();
		olderRetrying.setWebHookToken("wbh-older-retrying-001");
		olderRetrying.setObjectToken(objectToken);
		olderRetrying.setNotificationType(NotificationType.USR);
		olderRetrying.setCreationDate(new Date(1_000_000L));
		olderRetrying.setReceptionDate(new Date());
		olderRetrying.setStatus(NotificationStatus.RETRYING);
		olderRetrying.setRetryCounter(1);
		olderRetrying.setNextRetryDate(new Date(System.currentTimeMillis() + 60_000L));
		olderRetrying.setProgram(PROGRAM_TOKEN);
		notificationEntityRepository.save(olderRetrying);

		// Enqueue a newer notification for the same object and type
		final HyperwalletWebhookNotification newer = buildNotification("wbh-newer-002", type, objectToken,
				new Date(2_000_000L));
		notificationProcessingQueueService.enqueue(newer);

		assertThat(getSingleEntity("wbh-older-retrying-001").getStatus()).isEqualTo(NotificationStatus.OUTDATED);
		assertThat(getSingleEntity("wbh-newer-002").getStatus()).isEqualTo(NotificationStatus.PENDING);
	}

	@Test
	void enqueue_whenNewerNotificationArrivesAndMultipleOlderActiveOnesExist_shouldMarkAllOlderAsOutdated() {
		final String objectToken = "usr-supersede-multi";
		final String type = "USERS.UPDATED.VERIFICATION_STATUS.REQUIRED";

		final HyperwalletWebhookNotification first = buildNotification("wbh-multi-first", type, objectToken,
				new Date(1_000_000L));
		final HyperwalletWebhookNotification second = buildNotification("wbh-multi-second", type, objectToken,
				new Date(2_000_000L));
		final HyperwalletWebhookNotification newest = buildNotification("wbh-multi-newest", type, objectToken,
				new Date(3_000_000L));

		notificationProcessingQueueService.enqueue(first);
		notificationProcessingQueueService.enqueue(second);
		notificationProcessingQueueService.enqueue(newest);

		assertThat(getSingleEntity("wbh-multi-first").getStatus()).isEqualTo(NotificationStatus.OUTDATED);
		assertThat(getSingleEntity("wbh-multi-second").getStatus()).isEqualTo(NotificationStatus.OUTDATED);
		assertThat(getSingleEntity("wbh-multi-newest").getStatus()).isEqualTo(NotificationStatus.PENDING);
	}

	@Test
	void enqueue_whenNewerNotificationArrivesButOlderIsAlreadySucceeded_shouldNotMarkOlderAsOutdated() {
		final String objectToken = "usr-supersede-success";
		final String type = "USERS.UPDATED.VERIFICATION_STATUS.REQUIRED";

		// Insert an older notification that is already SUCCESS (terminal state)
		final NotificationEntity olderSuccess = new NotificationEntity();
		olderSuccess.setWebHookToken("wbh-older-success-001");
		olderSuccess.setObjectToken(objectToken);
		olderSuccess.setNotificationType(NotificationType.USR);
		olderSuccess.setCreationDate(new Date(1_000_000L));
		olderSuccess.setReceptionDate(new Date());
		olderSuccess.setStatus(NotificationStatus.SUCCESS);
		olderSuccess.setProgram(PROGRAM_TOKEN);
		notificationEntityRepository.save(olderSuccess);

		final HyperwalletWebhookNotification newer = buildNotification("wbh-newer-success-002", type, objectToken,
				new Date(2_000_000L));
		notificationProcessingQueueService.enqueue(newer);

		// The SUCCESS entity must stay SUCCESS — only active (PENDING/RETRYING) ones are
		// superseded
		assertThat(getSingleEntity("wbh-older-success-001").getStatus()).isEqualTo(NotificationStatus.SUCCESS);
		assertThat(getSingleEntity("wbh-newer-success-002").getStatus()).isEqualTo(NotificationStatus.PENDING);
	}

	@Test
	void enqueue_whenNewerNotificationArrivesButOlderIsAlreadyFailed_shouldNotMarkOlderAsOutdated() {
		final String objectToken = "usr-supersede-failed";
		final String type = "USERS.UPDATED.VERIFICATION_STATUS.REQUIRED";

		final NotificationEntity olderFailed = new NotificationEntity();
		olderFailed.setWebHookToken("wbh-older-failed-001");
		olderFailed.setObjectToken(objectToken);
		olderFailed.setNotificationType(NotificationType.USR);
		olderFailed.setCreationDate(new Date(1_000_000L));
		olderFailed.setReceptionDate(new Date());
		olderFailed.setStatus(NotificationStatus.FAILED);
		olderFailed.setProgram(PROGRAM_TOKEN);
		notificationEntityRepository.save(olderFailed);

		final HyperwalletWebhookNotification newer = buildNotification("wbh-newer-failed-002", type, objectToken,
				new Date(2_000_000L));
		notificationProcessingQueueService.enqueue(newer);

		assertThat(getSingleEntity("wbh-older-failed-001").getStatus()).isEqualTo(NotificationStatus.FAILED);
		assertThat(getSingleEntity("wbh-newer-failed-002").getStatus()).isEqualTo(NotificationStatus.PENDING);
	}

	@Test
	void enqueue_whenNewerNotificationArrives_shouldOnlySupersedeSameType() {
		// A newer USR notification must NOT mark an older PMT notification as OUTDATED
		// even if they share the same numeric suffix (different objectToken prefix =
		// different type).
		final String usrObjectToken = "usr-cross-type-001";
		final String pmtObjectToken = "pmt-cross-type-001";

		final HyperwalletWebhookNotification olderPmt = buildNotification("wbh-older-pmt-001",
				"PAYMENTS.UPDATED.STATUS.COMPLETED", pmtObjectToken, new Date(1_000_000L));
		final HyperwalletWebhookNotification newerUsr = buildNotification("wbh-newer-usr-001",
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", usrObjectToken, new Date(2_000_000L));

		notificationProcessingQueueService.enqueue(olderPmt);
		notificationProcessingQueueService.enqueue(newerUsr);

		// PMT must not be touched — different objectToken (and therefore different
		// NotificationType)
		assertThat(getSingleEntity("wbh-older-pmt-001").getStatus()).isEqualTo(NotificationStatus.PENDING);
		assertThat(getSingleEntity("wbh-newer-usr-001").getStatus()).isEqualTo(NotificationStatus.PENDING);
	}

	@Test
	void processJob_whenNotificationIsOutdated_shouldNotBePickedUpByJob() {
		// Enqueue an older notification, then a newer one that supersedes it.
		// The job must only process the newer (PENDING) one; the OUTDATED one must be
		// ignored.
		given(notificationHandler.supports(any(NotificationType.class))).willReturn(true);

		final String objectToken = "usr-job-outdated";
		final String type = "USERS.UPDATED.VERIFICATION_STATUS.REQUIRED";

		final HyperwalletWebhookNotification older = buildNotification("wbh-outdated-job-001", type, objectToken,
				new Date(1_000_000L));
		final HyperwalletWebhookNotification newer = buildNotification("wbh-current-job-001", type, objectToken,
				new Date(2_000_000L));

		webhookNotificationEndpointMock.getWebhookNotificationRequest(newer);

		notificationProcessingQueueService.enqueue(older);
		notificationProcessingQueueService.enqueue(newer);

		// Older must already be OUTDATED before the job runs
		assertThat(getSingleEntity("wbh-outdated-job-001").getStatus()).isEqualTo(NotificationStatus.OUTDATED);

		testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);

		// Newer processed, older left as OUTDATED
		assertThat(getSingleEntity("wbh-current-job-001").getStatus()).isEqualTo(NotificationStatus.SUCCESS);
		assertThat(getSingleEntity("wbh-outdated-job-001").getStatus()).isEqualTo(NotificationStatus.OUTDATED);

		// Handler invoked exactly once (for the newer notification only)
		verify(notificationHandler, times(1)).process(any(NotificationEntity.class),
				any(HyperwalletWebhookNotification.class));
	}

	// ==================================================================
	// 2. Successful processing
	// ==================================================================

	@Test
	void processJob_whenHandlerSucceeds_shouldMarkNotificationsAsSuccess() {
		// arrange
		given(notificationHandler.supports(any(NotificationType.class))).willReturn(true);

		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN_USR,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", "usr-001", new Date());
		webhookNotificationEndpointMock.getWebhookNotificationRequest(notification);
		notificationProcessingQueueService.enqueue(notification);

		// act
		testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);

		// assert
		final NotificationEntity entity = getSingleEntity(WEBHOOK_TOKEN_USR);
		assertThat(entity.getStatus()).isEqualTo(NotificationStatus.SUCCESS);
		verify(notificationHandler, times(1)).process(any(NotificationEntity.class),
				any(HyperwalletWebhookNotification.class));
	}

	@Test
	void processJob_whenMultipleHandlersAndNotificationsWithDifferentTypes_shouldProcessEachWithCorrectHandler() {
		// arrange — one USR notification and one PMT notification
		given(notificationHandler.supports(NotificationType.USR)).willReturn(true);
		given(notificationHandler.supports(NotificationType.PMT)).willReturn(true);

		final HyperwalletWebhookNotification usrNotification = buildNotification(WEBHOOK_TOKEN_USR,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", "usr-001", new Date());
		final HyperwalletWebhookNotification pmtNotification = buildNotification(WEBHOOK_TOKEN_PMT,
				"PAYMENTS.UPDATED.STATUS.COMPLETED", "pmt-002", new Date());

		webhookNotificationEndpointMock.getWebhookNotificationRequest(usrNotification);
		webhookNotificationEndpointMock.getWebhookNotificationRequest(pmtNotification);

		notificationProcessingQueueService.enqueue(usrNotification);
		notificationProcessingQueueService.enqueue(pmtNotification);

		// act
		testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);

		// assert — both marked SUCCESS
		assertThat(getSingleEntity(WEBHOOK_TOKEN_USR).getStatus()).isEqualTo(NotificationStatus.SUCCESS);
		assertThat(getSingleEntity(WEBHOOK_TOKEN_PMT).getStatus()).isEqualTo(NotificationStatus.SUCCESS);
		verify(notificationHandler, times(2)).process(any(NotificationEntity.class),
				any(HyperwalletWebhookNotification.class));
	}

	// ==================================================================
	// 3. Retry path
	// ==================================================================

	@Test
	void processJob_whenHandlerFailsOnce_shouldTransitionToRetrying() {
		given(notificationHandler.supports(any(NotificationType.class))).willReturn(true);
		willThrow(new RuntimeException("transient error")).given(notificationHandler).process(any(), any());

		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN_RETRY,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", "usr-retry", new Date());
		webhookNotificationEndpointMock.getWebhookNotificationRequest(notification);
		notificationProcessingQueueService.enqueue(notification);

		// first job run — should fail and move to RETRYING
		testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);

		final NotificationEntity entity = getSingleEntity(WEBHOOK_TOKEN_RETRY);
		assertThat(entity.getStatus()).isEqualTo(NotificationStatus.RETRYING);
		assertThat(entity.getRetryCounter()).isEqualTo(1);
		assertThat(entity.getLastRetryDate()).isNotNull();
		assertThat(entity.getNextRetryDate()).isNotNull();
		verify(mailNotificationUtil, never()).sendPlainTextEmail(anyString(), anyString());
	}

	@Test
	void processJob_whenHandlerFailsOnFirstAttemptThenSucceeds_shouldFinallyMarkSuccess() throws InterruptedException {
		given(notificationHandler.supports(any(NotificationType.class))).willReturn(true);

		// fail on first process(), succeed on second
		willThrow(new RuntimeException("transient error")).willDoNothing()
			.given(notificationHandler)
			.process(any(), any());

		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN_RETRY,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", "usr-retry", new Date());
		webhookNotificationEndpointMock.getWebhookNotificationRequest(notification);
		notificationProcessingQueueService.enqueue(notification);

		// first run → RETRYING
		testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);
		assertThat(getSingleEntity(WEBHOOK_TOKEN_RETRY).getStatus()).isEqualTo(NotificationStatus.RETRYING);

		// wait for the 100 ms back-off to expire, then run again → SUCCESS
		Thread.sleep(2100);
		testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);
		assertThat(getSingleEntity(WEBHOOK_TOKEN_RETRY).getStatus()).isEqualTo(NotificationStatus.SUCCESS);

		verify(mailNotificationUtil, never()).sendPlainTextEmail(anyString(), anyString());
	}

	@Test
	void processJob_whenRetryingNotificationBackoffNotElapsed_shouldNotPickItUpAgain() {
		given(notificationHandler.supports(any(NotificationType.class))).willReturn(true);
		willThrow(new RuntimeException("transient error")).given(notificationHandler).process(any(), any());

		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN_RETRY,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", "usr-retry", new Date());
		webhookNotificationEndpointMock.getWebhookNotificationRequest(notification);
		notificationProcessingQueueService.enqueue(notification);

		// first run → RETRYING (nextRetryDate set 100ms in future)
		testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);
		assertThat(getSingleEntity(WEBHOOK_TOKEN_RETRY).getStatus()).isEqualTo(NotificationStatus.RETRYING);

		// second run immediately — back-off NOT elapsed → handler must NOT be called
		// again
		testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);

		// still RETRYING with retryCounter still 1 (not incremented a second time)
		final NotificationEntity entity = getSingleEntity(WEBHOOK_TOKEN_RETRY);
		assertThat(entity.getStatus()).isEqualTo(NotificationStatus.RETRYING);
		assertThat(entity.getRetryCounter()).isEqualTo(1);
		// handler called only once (from the first run)
		verify(notificationHandler, times(1)).process(any(), any());
	}

	// ==================================================================
	// 4. Final failure path (maxRetries exhausted)
	// ==================================================================

	@Test
	void processJob_whenMaxRetriesExhausted_shouldMarkAsFailedAndSendAlertEmail() throws InterruptedException {
		// max-retries = 3 (overridden by @TestPropertySource); always fail
		given(notificationHandler.supports(any(NotificationType.class))).willReturn(true);
		willThrow(new RuntimeException("permanent error")).given(notificationHandler).process(any(), any());

		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN_FAIL,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", "usr-fail", new Date());
		webhookNotificationEndpointMock.getWebhookNotificationRequest(notification);
		notificationProcessingQueueService.enqueue(notification);

		// run the job 3 times, waiting 200 ms between each so backoff windows expire
		for (int i = 0; i < 3; i++) {
			Thread.sleep(2100);
			testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);
		}

		final NotificationEntity entity = getSingleEntity(WEBHOOK_TOKEN_FAIL);
		assertThat(entity.getStatus()).isEqualTo(NotificationStatus.FAILED);

		// alert email must be sent exactly once (on final failure)
		verify(mailNotificationUtil, times(1)).sendPlainTextEmail(contains(WEBHOOK_TOKEN_FAIL), anyString());
	}

	@Test
	void processJob_whenMaxRetriesExhausted_shouldNotPickUpNotificationAgain() throws InterruptedException {
		given(notificationHandler.supports(any(NotificationType.class))).willReturn(true);
		willThrow(new RuntimeException("permanent error")).given(notificationHandler).process(any(), any());

		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN_FAIL,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", "usr-fail", new Date());
		webhookNotificationEndpointMock.getWebhookNotificationRequest(notification);
		notificationProcessingQueueService.enqueue(notification);

		// exhaust retries
		for (int i = 0; i < 3; i++) {
			Thread.sleep(2100);
			testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);
		}
		assertThat(getSingleEntity(WEBHOOK_TOKEN_FAIL).getStatus()).isEqualTo(NotificationStatus.FAILED);

		// one additional run after FAILED — handler must NOT be called again
		Thread.sleep(2100);
		testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);

		// handler was called exactly 3 times (one per retry cycle)
		verify(notificationHandler, times(3)).process(any(), any());
	}

	// ==================================================================
	// 5. Cache miss — SDK fallback
	// ==================================================================

	@Test
	void processJob_whenNotificationNotInCacheAndSdkReturnsIt_shouldProcessSuccessfully() {
		given(notificationHandler.supports(any(NotificationType.class))).willReturn(true);

		// Build the notification but do NOT enqueue through queue service so cache is
		// empty
		// Instead, persist directly in the DB to simulate a cache-evicted PENDING entry
		final HyperwalletWebhookNotification notification = buildNotification(WEBHOOK_TOKEN_SDK_MISS,
				"USERS.UPDATED.VERIFICATION_STATUS.REQUIRED", "usr-sdk-miss", new Date());

		// Register MockServer expectation for the SDK GET call
		webhookNotificationEndpointMock.getWebhookNotificationRequest(notification);

		// Insert entity directly (bypassing cache population)
		final NotificationEntity entity = new NotificationEntity();
		entity.setWebHookToken(WEBHOOK_TOKEN_SDK_MISS);
		entity.setObjectToken("usr-sdk-miss");
		entity.setNotificationType(NotificationType.USR);
		entity.setCreationDate(new Date());
		entity.setReceptionDate(new Date());
		entity.setStatus(NotificationStatus.PENDING);
		entity.setRetryCounter(0);
		entity.setProgram(PROGRAM_TOKEN);
		notificationEntityRepository.save(entity);

		// act
		testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);

		// assert — should have retrieved from SDK and processed successfully
		assertThat(getSingleEntity(WEBHOOK_TOKEN_SDK_MISS).getStatus()).isEqualTo(NotificationStatus.SUCCESS);
	}

	@Test
	void processJob_whenNotificationNotInCacheAndSdkReturnsNotFound_shouldTransitionToRetrying() {
		given(notificationHandler.supports(any(NotificationType.class))).willReturn(true);

		// SDK returns 404 — simulates notification no longer available
		webhookNotificationEndpointMock.getWebhookNotificationNotFoundRequest(WEBHOOK_TOKEN_SDK_MISS);

		final NotificationEntity entity = new NotificationEntity();
		entity.setWebHookToken(WEBHOOK_TOKEN_SDK_MISS);
		entity.setObjectToken("usr-sdk-miss");
		entity.setNotificationType(NotificationType.USR);
		entity.setCreationDate(new Date());
		entity.setReceptionDate(new Date());
		entity.setStatus(NotificationStatus.PENDING);
		entity.setRetryCounter(0);
		entity.setProgram(PROGRAM_TOKEN);
		notificationEntityRepository.save(entity);

		testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);

		assertThat(getSingleEntity(WEBHOOK_TOKEN_SDK_MISS).getStatus()).isEqualTo(NotificationStatus.RETRYING);
		verify(notificationHandler, never()).process(any(), any());
	}

	// ==================================================================
	// 6. No-op when queue is empty
	// ==================================================================

	@Test
	void processJob_whenQueueIsEmpty_shouldNotInvokeHandler() {
		testJobExecutor.executeJobAndWaitForCompletion(NotificationProcessJob.class);

		verify(notificationHandler, never()).process(any(), any());
	}

	// ==================================================================
	// Helpers
	// ==================================================================

	private NotificationEntity getSingleEntity(final String webhookToken) {
		final List<NotificationEntity> results = notificationEntityRepository
			.findNotificationsByWebHookToken(webhookToken);
		assertThat(results).hasSize(1);
		return results.get(0);
	}

	private static HyperwalletWebhookNotification buildNotification(final String token, final String type,
			final String objectToken, final Date createdOn) {
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		notification.setToken(token);
		notification.setType(type);
		notification.setCreatedOn(createdOn);
		notification.setObject(Map.of("token", objectToken, "programToken", PROGRAM_TOKEN));
		return notification;
	}

}
