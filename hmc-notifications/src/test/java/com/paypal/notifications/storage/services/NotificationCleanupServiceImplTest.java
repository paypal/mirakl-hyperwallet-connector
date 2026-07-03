package com.paypal.notifications.storage.services;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

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

		verify(notificationEntityRepositoryMock).deleteByReceptionDateBeforeAndStatusIn(any(Date.class),
				argThat((Collection<NotificationStatus> statuses) -> statuses.contains(NotificationStatus.FAILED)
						&& statuses.contains(NotificationStatus.SUCCESS)
						&& statuses.contains(NotificationStatus.OUTDATED) && statuses.size() == 3));
	}

	@Test
	void deleteExpiredNotifications_shouldUseCutoffDateDerivedFromRetentionDays() {
		final Instant beforeCall = Instant.now().minus(RETENTION_DAYS, ChronoUnit.DAYS);

		testObj.deleteExpiredNotifications();

		final ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);
		verify(notificationEntityRepositoryMock).deleteByReceptionDateBeforeAndStatusIn(dateCaptor.capture(), any());

		final Instant afterCall = Instant.now().minus(RETENTION_DAYS, ChronoUnit.DAYS);
		assertThat(dateCaptor.getValue().toInstant()).isBetween(beforeCall.minusSeconds(1), afterCall.plusSeconds(1));
	}

	@Test
	void deleteExpiredNotifications_shouldDelegateToRepository() {
		testObj.deleteExpiredNotifications();

		verify(notificationEntityRepositoryMock).deleteByReceptionDateBeforeAndStatusIn(any(Date.class), any());
	}

}
