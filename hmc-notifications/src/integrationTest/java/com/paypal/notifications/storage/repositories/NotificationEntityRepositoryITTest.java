package com.paypal.notifications.storage.repositories;

import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationStatus;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import com.paypal.testsupport.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for all custom JPQL queries defined in
 * {@link NotificationEntityRepository}.
 *
 * <p>
 * Each test group covers one repository method. Fixtures are built via
 * {@link #entity(String, NotificationStatus, NotificationType, Date, Date)} helpers and
 * cleaned up in {@code @AfterEach} to keep tests independent.
 */
class NotificationEntityRepositoryITTest extends AbstractIntegrationTest {

	// ── shared tokens ────────────────────────────────────────────────────────────

	private static final String TOKEN_1 = "wbh-repo-it-001";

	private static final String TOKEN_2 = "wbh-repo-it-002";

	private static final String TOKEN_3 = "wbh-repo-it-003";

	private static final String OBJECT_TOKEN = "usr-repo-it-obj-001";

	private static final String PROGRAM = "prg-repo-it-001";

	// ── date anchors ─────────────────────────────────────────────────────────────

	/** A reception date 100 days ago — older than any reasonable retention window. */
	private static final Date DATE_OLD = Date.from(Instant.now().minus(100, ChronoUnit.DAYS));

	/** A reception date 30 days ago — within a 90-day window. */
	private static final Date DATE_RECENT = Date.from(Instant.now().minus(30, ChronoUnit.DAYS));

	/** The current instant, used as the boundary in range and batch-fetch queries. */
	private static final Date NOW = new Date();

	// ── spring beans ─────────────────────────────────────────────────────────────

	@Autowired
	private NotificationEntityRepository repository;

	@AfterEach
	void cleanUp() {
		repository.deleteAll();
	}

	// ═══════════════════════════════════════════════════════════════════════════
	// findNotificationsBetween
	// ═══════════════════════════════════════════════════════════════════════════

	@Test
	void findNotificationsBetween_shouldReturnOnlyEntitiesWithinRange() {
		repository.save(entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, NOW, DATE_OLD));
		repository.save(entity(TOKEN_2, NotificationStatus.SUCCESS, NotificationType.USR, NOW, DATE_RECENT));
		repository.save(entity(TOKEN_3, NotificationStatus.FAILED, NotificationType.USR, NOW, NOW));

		final Date from = Date.from(Instant.now().minus(40, ChronoUnit.DAYS));
		final Date to = new Date();

		final List<NotificationEntity> result = repository.findNotificationsBetween(from, to);

		assertThat(result).extracting(NotificationEntity::getWebHookToken).containsExactlyInAnyOrder(TOKEN_2, TOKEN_3);
	}

	@Test
	void findNotificationsBetween_whenNoEntityFallsInRange_shouldReturnEmpty() {
		repository.save(entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, NOW, DATE_OLD));

		final Date from = Date.from(Instant.now().minus(5, ChronoUnit.DAYS));

		final List<NotificationEntity> result = repository.findNotificationsBetween(from, new Date());

		assertThat(result).isEmpty();
	}

	// ═══════════════════════════════════════════════════════════════════════════
	// deleteNotificationsBetween
	// ═══════════════════════════════════════════════════════════════════════════

	@Test
	void deleteNotificationsBetween_shouldRemoveOnlyEntitiesWithinRange() {
		repository.save(entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, NOW, DATE_OLD));
		repository.save(entity(TOKEN_2, NotificationStatus.SUCCESS, NotificationType.USR, NOW, DATE_RECENT));

		final Date from = Date.from(Instant.now().minus(40, ChronoUnit.DAYS));

		repository.deleteNotificationsBetween(from, new Date());

		assertThat(repository.findAll()).extracting(NotificationEntity::getWebHookToken).containsExactly(TOKEN_1);
	}

	@Test
	void deleteNotificationsBetween_whenNothingInRange_shouldLeaveTableUnchanged() {
		repository.save(entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, NOW, DATE_OLD));

		final Date from = Date.from(Instant.now().minus(5, ChronoUnit.DAYS));

		repository.deleteNotificationsBetween(from, new Date());

		assertThat(repository.findAll()).hasSize(1);
	}

	// ═══════════════════════════════════════════════════════════════════════════
	// findFailedNotifications
	// ═══════════════════════════════════════════════════════════════════════════

	@Test
	void findFailedNotifications_withNoFilters_shouldReturnFailedAndRetrying() {
		repository.save(entity(TOKEN_1, NotificationStatus.FAILED, NotificationType.USR, NOW, NOW));
		repository.save(entity(TOKEN_2, NotificationStatus.RETRYING, NotificationType.PMT, NOW, NOW));
		repository.save(entity(TOKEN_3, NotificationStatus.SUCCESS, NotificationType.USR, NOW, NOW));

		final Page<NotificationEntity> result = repository.findFailedNotifications(null, null, PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).extracting(NotificationEntity::getWebHookToken)
				.containsExactlyInAnyOrder(TOKEN_1, TOKEN_2);
	}

	@Test
	void findFailedNotifications_withTypeFilter_shouldReturnOnlyMatchingType() {
		repository.save(entity(TOKEN_1, NotificationStatus.FAILED, NotificationType.USR, NOW, NOW));
		repository.save(entity(TOKEN_2, NotificationStatus.FAILED, NotificationType.PMT, NOW, NOW));

		final Page<NotificationEntity> result = repository.findFailedNotifications(NotificationType.USR, null,
				PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getWebHookToken()).isEqualTo(TOKEN_1);
	}

	@Test
	void findFailedNotifications_withObjectTokenFilter_shouldReturnOnlyMatchingToken() {
		final NotificationEntity e1 = entity(TOKEN_1, NotificationStatus.FAILED, NotificationType.USR, NOW, NOW);
		e1.setObjectToken(OBJECT_TOKEN);
		final NotificationEntity e2 = entity(TOKEN_2, NotificationStatus.FAILED, NotificationType.USR, NOW, NOW);
		e2.setObjectToken("usr-other-obj");
		repository.save(e1);
		repository.save(e2);

		final Page<NotificationEntity> result = repository.findFailedNotifications(null, OBJECT_TOKEN,
				PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getWebHookToken()).isEqualTo(TOKEN_1);
	}

	@Test
	void findFailedNotifications_shouldNotReturnPendingSuccessOrOutdated() {
		repository.save(entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, NOW, NOW));
		repository.save(entity(TOKEN_2, NotificationStatus.SUCCESS, NotificationType.USR, NOW, NOW));
		repository.save(entity(TOKEN_3, NotificationStatus.OUTDATED, NotificationType.USR, NOW, NOW));

		final Page<NotificationEntity> result = repository.findFailedNotifications(null, null, PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isZero();
	}

	// ═══════════════════════════════════════════════════════════════════════════
	// findByWebHookToken
	// ═══════════════════════════════════════════════════════════════════════════

	@Test
	void findByWebHookToken_whenTokenExists_shouldReturnEntity() {
		repository.save(entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, NOW, NOW));

		final Optional<NotificationEntity> result = repository.findByWebHookToken(TOKEN_1);

		assertThat(result).isPresent().get().extracting(NotificationEntity::getWebHookToken).isEqualTo(TOKEN_1);
	}

	@Test
	void findByWebHookToken_whenTokenAbsent_shouldReturnEmpty() {
		final Optional<NotificationEntity> result = repository.findByWebHookToken("wbh-nonexistent");

		assertThat(result).isEmpty();
	}

	// ═══════════════════════════════════════════════════════════════════════════
	// deleteByWebHookToken
	// ═══════════════════════════════════════════════════════════════════════════

	@Test
	void deleteByWebHookToken_whenTokenExists_shouldRemoveThatEntityOnly() {
		repository.save(entity(TOKEN_1, NotificationStatus.FAILED, NotificationType.USR, NOW, NOW));
		repository.save(entity(TOKEN_2, NotificationStatus.FAILED, NotificationType.USR, NOW, NOW));

		repository.deleteByWebHookToken(TOKEN_1);

		assertThat(repository.findByWebHookToken(TOKEN_1)).isEmpty();
		assertThat(repository.findByWebHookToken(TOKEN_2)).isPresent();
	}

	// ═══════════════════════════════════════════════════════════════════════════
	// findNotificationsByWebHookToken
	// ═══════════════════════════════════════════════════════════════════════════

	@Test
	void findNotificationsByWebHookToken_whenTokenExists_shouldReturnList() {
		repository.save(entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, NOW, NOW));
		repository.save(entity(TOKEN_2, NotificationStatus.PENDING, NotificationType.USR, NOW, NOW));

		final List<NotificationEntity> result = repository.findNotificationsByWebHookToken(TOKEN_1);

		assertThat(result).hasSize(1).extracting(NotificationEntity::getWebHookToken).containsExactly(TOKEN_1);
	}

	@Test
	void findNotificationsByWebHookToken_whenTokenAbsent_shouldReturnEmptyList() {
		final List<NotificationEntity> result = repository.findNotificationsByWebHookToken("wbh-nonexistent");

		assertThat(result).isEmpty();
	}

	// ═══════════════════════════════════════════════════════════════════════════
	// findNotificationsByObjectTokenAndAndCreationDateAfter
	// ═══════════════════════════════════════════════════════════════════════════

	@Test
	void findNotificationsByObjectTokenAndCreationDateAfter_shouldReturnOnlyNewerOnesForSameObject() {
		final Date older = Date.from(Instant.now().minus(10, ChronoUnit.DAYS));
		final Date newer = new Date();

		final NotificationEntity e1 = entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, older, NOW);
		e1.setObjectToken(OBJECT_TOKEN);
		final NotificationEntity e2 = entity(TOKEN_2, NotificationStatus.PENDING, NotificationType.USR, newer, NOW);
		e2.setObjectToken(OBJECT_TOKEN);
		// different object — must not appear
		final NotificationEntity e3 = entity(TOKEN_3, NotificationStatus.PENDING, NotificationType.USR, newer, NOW);
		e3.setObjectToken("usr-other-obj");
		repository.save(e1);
		repository.save(e2);
		repository.save(e3);

		final Date boundary = Date.from(Instant.now().minus(5, ChronoUnit.DAYS));
		final List<NotificationEntity> result = repository
				.findNotificationsByObjectTokenAndAndCreationDateAfter(OBJECT_TOKEN, boundary);

		assertThat(result).hasSize(1).extracting(NotificationEntity::getWebHookToken).containsExactly(TOKEN_2);
	}

	// ═══════════════════════════════════════════════════════════════════════════
	// findActiveNotificationsSupersededBy
	// ═══════════════════════════════════════════════════════════════════════════

	@Test
	void findActiveNotificationsSupersededBy_shouldReturnPendingAndRetryingWithOlderCreationDate() {
		final Date old = new Date(1_000_000L);
		final Date recent = new Date(2_000_000L);
		final Date incoming = new Date(3_000_000L);

		final NotificationEntity pending = entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, old, NOW);
		pending.setObjectToken(OBJECT_TOKEN);
		final NotificationEntity retrying = entity(TOKEN_2, NotificationStatus.RETRYING, NotificationType.USR, recent,
				NOW);
		retrying.setObjectToken(OBJECT_TOKEN);
		// terminal — must not be returned
		final NotificationEntity success = entity(TOKEN_3, NotificationStatus.SUCCESS, NotificationType.USR, old, NOW);
		success.setObjectToken(OBJECT_TOKEN);
		repository.save(pending);
		repository.save(retrying);
		repository.save(success);

		final List<NotificationEntity> result = repository.findActiveNotificationsSupersededBy(OBJECT_TOKEN,
				NotificationType.USR, incoming);

		assertThat(result).extracting(NotificationEntity::getWebHookToken).containsExactlyInAnyOrder(TOKEN_1, TOKEN_2);
	}

	@Test
	void findActiveNotificationsSupersededBy_shouldNotReturnEntitiesForDifferentType() {
		final Date old = new Date(1_000_000L);
		final Date incoming = new Date(3_000_000L);

		final NotificationEntity pmt = entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.PMT, old, NOW);
		pmt.setObjectToken(OBJECT_TOKEN);
		repository.save(pmt);

		// query for USR type — the PMT entity must not appear
		final List<NotificationEntity> result = repository.findActiveNotificationsSupersededBy(OBJECT_TOKEN,
				NotificationType.USR, incoming);

		assertThat(result).isEmpty();
	}

	@Test
	void findActiveNotificationsSupersededBy_shouldNotReturnEntitiesWithNewerOrEqualCreationDate() {
		final Date sameAsIncoming = new Date(2_000_000L);

		final NotificationEntity e = entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, sameAsIncoming,
				NOW);
		e.setObjectToken(OBJECT_TOKEN);
		repository.save(e);

		// incoming has the same timestamp — strictly older check means this must NOT be
		// returned
		final List<NotificationEntity> result = repository.findActiveNotificationsSupersededBy(OBJECT_TOKEN,
				NotificationType.USR, sameAsIncoming);

		assertThat(result).isEmpty();
	}

	// ═══════════════════════════════════════════════════════════════════════════
	// findNextBatchForProcessing
	// ═══════════════════════════════════════════════════════════════════════════

	@Test
	void findNextBatchForProcessing_shouldReturnPendingNotifications() {
		repository.save(entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, NOW, NOW));

		final List<NotificationEntity> result = repository.findNextBatchForProcessing(new Date(),
				PageRequest.of(0, 10));

		assertThat(result).extracting(NotificationEntity::getWebHookToken).containsExactly(TOKEN_1);
	}

	@Test
	void findNextBatchForProcessing_shouldReturnRetryingWhenNextRetryDateHasPassed() {
		final NotificationEntity retrying = entity(TOKEN_1, NotificationStatus.RETRYING, NotificationType.USR, NOW,
				NOW);
		// nextRetryDate already in the past
		retrying.setNextRetryDate(Date.from(Instant.now().minus(1, ChronoUnit.SECONDS)));
		repository.save(retrying);

		final List<NotificationEntity> result = repository.findNextBatchForProcessing(new Date(),
				PageRequest.of(0, 10));

		assertThat(result).extracting(NotificationEntity::getWebHookToken).containsExactly(TOKEN_1);
	}

	@Test
	void findNextBatchForProcessing_shouldExcludeRetryingWhenBackoffNotElapsed() {
		final NotificationEntity retrying = entity(TOKEN_1, NotificationStatus.RETRYING, NotificationType.USR, NOW,
				NOW);
		// nextRetryDate still in the future
		retrying.setNextRetryDate(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));
		repository.save(retrying);

		final List<NotificationEntity> result = repository.findNextBatchForProcessing(new Date(),
				PageRequest.of(0, 10));

		assertThat(result).isEmpty();
	}

	@Test
	void findNextBatchForProcessing_shouldExcludeTerminalStatuses() {
		repository.save(entity(TOKEN_1, NotificationStatus.SUCCESS, NotificationType.USR, NOW, NOW));
		repository.save(entity(TOKEN_2, NotificationStatus.FAILED, NotificationType.USR, NOW, NOW));
		repository.save(entity(TOKEN_3, NotificationStatus.OUTDATED, NotificationType.USR, NOW, NOW));

		final List<NotificationEntity> result = repository.findNextBatchForProcessing(new Date(),
				PageRequest.of(0, 10));

		assertThat(result).isEmpty();
	}

	@Test
	void findNextBatchForProcessing_shouldRespectBatchSizeLimit() {
		repository.save(entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, new Date(1_000L), NOW));
		repository.save(entity(TOKEN_2, NotificationStatus.PENDING, NotificationType.USR, new Date(2_000L), NOW));
		repository.save(entity(TOKEN_3, NotificationStatus.PENDING, NotificationType.USR, new Date(3_000L), NOW));

		final List<NotificationEntity> result = repository.findNextBatchForProcessing(new Date(), PageRequest.of(0, 2));

		assertThat(result).hasSize(2);
	}

	@Test
	void findNextBatchForProcessing_shouldReturnResultsOrderedByCreationDateAscending() {
		repository.save(entity(TOKEN_3, NotificationStatus.PENDING, NotificationType.USR, new Date(3_000L), NOW));
		repository.save(entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, new Date(1_000L), NOW));
		repository.save(entity(TOKEN_2, NotificationStatus.PENDING, NotificationType.USR, new Date(2_000L), NOW));

		final List<NotificationEntity> result = repository.findNextBatchForProcessing(new Date(),
				PageRequest.of(0, 10));

		assertThat(result).extracting(NotificationEntity::getWebHookToken).containsExactly(TOKEN_1, TOKEN_2, TOKEN_3);
	}

	// ═══════════════════════════════════════════════════════════════════════════
	// updateStatusByWebHookToken
	// ═══════════════════════════════════════════════════════════════════════════

	@Test
	void updateStatusByWebHookToken_shouldUpdateStatusOfMatchingEntityOnly() {
		repository.save(entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, NOW, NOW));
		repository.save(entity(TOKEN_2, NotificationStatus.PENDING, NotificationType.USR, NOW, NOW));

		repository.updateStatusByWebHookToken(TOKEN_1, NotificationStatus.SUCCESS);

		assertThat(repository.findByWebHookToken(TOKEN_1)).isPresent().get().extracting(NotificationEntity::getStatus)
				.isEqualTo(NotificationStatus.SUCCESS);
		assertThat(repository.findByWebHookToken(TOKEN_2)).isPresent().get().extracting(NotificationEntity::getStatus)
				.isEqualTo(NotificationStatus.PENDING);
	}

	// ═══════════════════════════════════════════════════════════════════════════
	// updateStatusAndRetryCounterByWebHookToken
	// ═══════════════════════════════════════════════════════════════════════════

	@Test
	void updateStatusAndRetryCounterByWebHookToken_shouldPersistAllUpdatedFields() {
		repository.save(entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, NOW, NOW));

		final Date lastRetry = new Date();
		final Date nextRetry = Date.from(Instant.now().plus(5, ChronoUnit.MINUTES));

		repository.updateStatusAndRetryCounterByWebHookToken(TOKEN_1, NotificationStatus.RETRYING, 1, lastRetry,
				nextRetry);

		final NotificationEntity updated = repository.findByWebHookToken(TOKEN_1).orElseThrow();
		assertThat(updated.getStatus()).isEqualTo(NotificationStatus.RETRYING);
		assertThat(updated.getRetryCounter()).isEqualTo(1);
		assertThat(updated.getLastRetryDate()).isCloseTo(lastRetry, 999);
		assertThat(updated.getNextRetryDate()).isCloseTo(nextRetry, 999);
	}

	@Test
	void updateStatusAndRetryCounterByWebHookToken_whenFinalFailure_shouldAllowNullNextRetryDate() {
		repository.save(entity(TOKEN_1, NotificationStatus.RETRYING, NotificationType.USR, NOW, NOW));

		repository.updateStatusAndRetryCounterByWebHookToken(TOKEN_1, NotificationStatus.FAILED, 5, new Date(), null);

		final NotificationEntity updated = repository.findByWebHookToken(TOKEN_1).orElseThrow();
		assertThat(updated.getStatus()).isEqualTo(NotificationStatus.FAILED);
		assertThat(updated.getRetryCounter()).isEqualTo(5);
		assertThat(updated.getNextRetryDate()).isNull();
	}

	// ═══════════════════════════════════════════════════════════════════════════
	// deleteByReceptionDateBeforeAndStatusIn
	// ═══════════════════════════════════════════════════════════════════════════

	@Test
	void deleteByReceptionDateBeforeAndStatusIn_shouldDeleteMatchingStatusesOlderThanCutoff() {
		repository.save(entity(TOKEN_1, NotificationStatus.FAILED, NotificationType.USR, NOW, DATE_OLD));
		repository.save(entity(TOKEN_2, NotificationStatus.SUCCESS, NotificationType.USR, NOW, DATE_OLD));
		repository.save(entity(TOKEN_3, NotificationStatus.OUTDATED, NotificationType.USR, NOW, DATE_OLD));

		final Date cutoff = Date.from(Instant.now().minus(50, ChronoUnit.DAYS));

		repository.deleteByReceptionDateBeforeAndStatusIn(cutoff,
				EnumSet.of(NotificationStatus.FAILED, NotificationStatus.SUCCESS, NotificationStatus.OUTDATED));

		assertThat(repository.findAll()).isEmpty();
	}

	@Test
	void deleteByReceptionDateBeforeAndStatusIn_shouldNotDeleteRecentEntitiesEvenIfStatusMatches() {
		repository.save(entity(TOKEN_1, NotificationStatus.FAILED, NotificationType.USR, NOW, DATE_RECENT));
		repository.save(entity(TOKEN_2, NotificationStatus.SUCCESS, NotificationType.USR, NOW, DATE_RECENT));

		final Date cutoff = Date.from(Instant.now().minus(50, ChronoUnit.DAYS));

		repository.deleteByReceptionDateBeforeAndStatusIn(cutoff,
				EnumSet.of(NotificationStatus.FAILED, NotificationStatus.SUCCESS, NotificationStatus.OUTDATED));

		assertThat(repository.findAll()).hasSize(2);
	}

	@Test
	void deleteByReceptionDateBeforeAndStatusIn_shouldNotDeletePendingOrRetryingEvenIfOld() {
		repository.save(entity(TOKEN_1, NotificationStatus.PENDING, NotificationType.USR, NOW, DATE_OLD));
		repository.save(entity(TOKEN_2, NotificationStatus.RETRYING, NotificationType.USR, NOW, DATE_OLD));

		final Date cutoff = Date.from(Instant.now().minus(50, ChronoUnit.DAYS));

		repository.deleteByReceptionDateBeforeAndStatusIn(cutoff,
				EnumSet.of(NotificationStatus.FAILED, NotificationStatus.SUCCESS, NotificationStatus.OUTDATED));

		assertThat(repository.findAll()).hasSize(2);
	}

	@Test
	void deleteByReceptionDateBeforeAndStatusIn_shouldDeleteOnlyStatusesInProvidedList() {
		repository.save(entity(TOKEN_1, NotificationStatus.FAILED, NotificationType.USR, NOW, DATE_OLD));
		repository.save(entity(TOKEN_2, NotificationStatus.SUCCESS, NotificationType.USR, NOW, DATE_OLD));
		repository.save(entity(TOKEN_3, NotificationStatus.OUTDATED, NotificationType.USR, NOW, DATE_OLD));

		final Date cutoff = Date.from(Instant.now().minus(50, ChronoUnit.DAYS));

		// Delete only FAILED — SUCCESS and OUTDATED must survive
		repository.deleteByReceptionDateBeforeAndStatusIn(cutoff, EnumSet.of(NotificationStatus.FAILED));

		assertThat(repository.findAll()).extracting(NotificationEntity::getWebHookToken)
				.containsExactlyInAnyOrder(TOKEN_2, TOKEN_3);
	}

	// ═══════════════════════════════════════════════════════════════════════════
	// helper
	// ═══════════════════════════════════════════════════════════════════════════

	/**
	 * Builds a minimal but valid {@link NotificationEntity} ready to be persisted.
	 * @param webhookToken unique webhook token.
	 * @param status initial processing status.
	 * @param type notification type.
	 * @param creationDate date the original Hyperwallet event was created.
	 * @param receptionDate date the connector received the webhook.
	 */
	private NotificationEntity entity(final String webhookToken, final NotificationStatus status,
			final NotificationType type, final Date creationDate, final Date receptionDate) {
		final NotificationEntity e = new NotificationEntity();
		e.setWebHookToken(webhookToken);
		e.setObjectToken(OBJECT_TOKEN);
		e.setProgram(PROGRAM);
		e.setNotificationType(type);
		e.setStatus(status);
		e.setCreationDate(creationDate);
		e.setReceptionDate(receptionDate);
		return e;
	}

}
