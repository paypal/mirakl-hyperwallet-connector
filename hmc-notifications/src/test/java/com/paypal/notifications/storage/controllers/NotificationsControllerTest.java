package com.paypal.notifications.storage.controllers;

import com.paypal.infrastructure.support.exceptions.DateIntervalException;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.services.NotificationStorageService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationsControllerTest {

	private static final Date FROM_DATE = new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime();

	private static final Date TO_DATE = new GregorianCalendar(2015, Calendar.FEBRUARY, 11).getTime();

	private static final String DATE_INTERVAL_FORMATTED_MESSAGE = "[From] date [2015-02-11T00:00:00] can not be later than [To] date [2014-02-11T00:00:00]";

	@InjectMocks
	private NotificationsController testObj;

	@Mock
	private NotificationStorageService notificationStorageServiceMock;

	@Mock
	private NotificationEntity notificationEntity1Mock, notificationEntity2Mock;

	@Test
	void getAllNotifications_ShouldThrowADateIntervalException_WhenFromDateIsLaterThanToDate() {

		final DateIntervalException exception = assertThrows(DateIntervalException.class,
				() -> testObj.getAllNotifications(TO_DATE, FROM_DATE));

		assertThat(exception.getMessage()).isEqualTo(DATE_INTERVAL_FORMATTED_MESSAGE);
	}

	@Test
	void getAllNotifications_ShouldAnOkStatusAndAllTheNotificationsBetweenFromAndTo_WhenFromDateIsBeforeThanToDate() {

		when(notificationStorageServiceMock.getNotificationsBetween(FROM_DATE, TO_DATE))
				.thenReturn(List.of(notificationEntity1Mock, notificationEntity2Mock));

		final List<NotificationEntity> result = testObj.getAllNotifications(FROM_DATE, TO_DATE);

		assertThat(result).containsExactly(notificationEntity1Mock, notificationEntity2Mock);
	}

	@Test
	void deleteNotificationsBetween_ShouldThrowADateIntervalException_WhenFromDateIsLaterThanToDate() {

		final DateIntervalException exception = assertThrows(DateIntervalException.class,
				() -> testObj.deleteNotificationsBetween(TO_DATE, FROM_DATE));

		assertThat(exception.getMessage()).isEqualTo(DATE_INTERVAL_FORMATTED_MESSAGE);
	}

	@Test
	void deleteNotificationsBetween_ShouldRemoveAllNotificationsBetween() {

		testObj.deleteNotificationsBetween(FROM_DATE, TO_DATE);

		verify(notificationStorageServiceMock).deleteNotificationsBetween(FROM_DATE, TO_DATE);
	}

}
