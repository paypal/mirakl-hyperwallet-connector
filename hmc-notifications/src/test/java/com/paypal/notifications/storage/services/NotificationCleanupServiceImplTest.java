package com.paypal.notifications.storage.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.paypal.notifications.storage.repositories.NotificationEntityRepository;
import com.paypal.notifications.storage.repositories.entities.NotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
class NotificationCleanupServiceImplTest {

	private static final int RETENTION_DAYS = 90;

	@Mock
	private NotificationEntityRepository notificationEntityRepositoryMock;

	private NotificationCleanupServiceImpl testObj;

	@BeforeEach
	void setUp() {
		testObj = new NotificationCleanupServiceImpl(notificationEntityRepositoryMock, RETENTION_DAYS);
	}

	// ── happy path ────────────────────────────────────────────────────────────────

	@Test
	void deleteExpiredNotifications_shouldDeleteOnlyTerminalStatuses() {
		testObj.deleteExpiredNotifications();

		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Collection<NotificationStatus>> statusCaptor = ArgumentCaptor.forClass(Collection.class);
		verify(notificationEntityRepositoryMock).deleteByReceptionDateBeforeAndStatusIn(any(Date.class),
				statusCaptor.capture());

		assertThat(statusCaptor.getValue()).containsExactlyInAnyOrder(NotificationStatus.FAILED,
				NotificationStatus.SUCCESS, NotificationStatus.OUTDATED);
	}

	@Test
	void deleteExpiredNotifications_shouldUseCutoffDateDerivedFromRetentionDays() {
		final Instant beforeCall = Instant.now().minus(RETENTION_DAYS, ChronoUnit.DAYS);

		testObj.deleteExpiredNotifications();

		final ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);
		verify(notificationEntityRepositoryMock).deleteByReceptionDateBeforeAndStatusIn(dateCaptor.capture(),
				any(Collection.class));

		final Instant afterCall = Instant.now().minus(RETENTION_DAYS, ChronoUnit.DAYS);
		assertThat(dateCaptor.getValue().toInstant()).isBetween(beforeCall.minusSeconds(1), afterCall.plusSeconds(1));
	}

	@Test
	void deleteExpiredNotifications_shouldDelegateToRepository() {
		testObj.deleteExpiredNotifications();

		verify(notificationEntityRepositoryMock).deleteByReceptionDateBeforeAndStatusIn(any(Date.class),
				any(Collection.class));
	}

}
