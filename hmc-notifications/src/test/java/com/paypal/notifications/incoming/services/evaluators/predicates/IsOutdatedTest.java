package com.paypal.notifications.incoming.services.evaluators.predicates;

import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.services.NotificationStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IsOutdatedTest {

	private static final String OBJECT_TOKEN = "objectToken";

	private static final Date CREATION_DATE = new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime();

	@InjectMocks
	private IsOutdated testObj;

	@Mock
	private NotificationStorageService notificationStorageService;

	@Mock
	private NotificationEntity notificationEntityMock;

	@BeforeEach
	public void setUp() {

		when(notificationEntityMock.getObjectToken()).thenReturn(OBJECT_TOKEN);
		when(notificationEntityMock.getCreationDate()).thenReturn(CREATION_DATE);
	}

	@Test
	void test_ShouldReturnFalse_WhenThereAreNotNotificationsWithTheSameObjectTokenAndALaterCreationTime() {

		when(notificationStorageService.getNotificationsByObjectTokenAndAndCreationDateAfter(OBJECT_TOKEN,
				CREATION_DATE)).thenReturn(List.of());

		final boolean result = testObj.test(notificationEntityMock);

		assertThat(result).isFalse();
	}

	@Test
	void test_ShouldReturnTrue_WhenThereAreNotificationsWithTheSameObjectTokenAndALaterCreationTime() {

		when(notificationStorageService.getNotificationsByObjectTokenAndAndCreationDateAfter(OBJECT_TOKEN,
				CREATION_DATE)).thenReturn(List.of(new NotificationEntity()));

		final boolean result = testObj.test(notificationEntityMock);

		assertThat(result).isTrue();
	}

}
