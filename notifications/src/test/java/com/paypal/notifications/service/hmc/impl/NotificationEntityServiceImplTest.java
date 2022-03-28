package com.paypal.notifications.service.hmc.impl;

import com.paypal.notifications.model.entity.NotificationEntity;
import com.paypal.notifications.repository.NotificationEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationEntityServiceImplTest {

	private static final String WEB_HOOK_TOKEN = "webHookToken";

	private static final String OBJECT_TOKEN = "objectToken";

	private static final Date CREATION_DATE = new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime();

	@InjectMocks
	private NotificationEntityServiceImpl testObj;

	@Mock
	private NotificationEntityRepository notificationEntityRepositoryMock;

	@Mock
	private NotificationEntity notificationEntityMock;

	@Mock
	private Date fromDateMock, toDateMock;

	@Test
	void saveNotification_ShouldSaveTheNotification() {

		testObj.saveNotification(notificationEntityMock);

		verify(notificationEntityRepositoryMock).save(notificationEntityMock);
	}

	@Test
	void getNotificationBetween_ShouldGetAllNotificationsByTheGivenDate() {

		testObj.getNotificationsBetween(fromDateMock, toDateMock);

		verify(notificationEntityRepositoryMock).findNotificationsBetween(fromDateMock, toDateMock);
	}

	@Test
	void deleteNotificationsBetween_ShouldRemoveNotificationsBetweenTheGivenDates() {

		testObj.deleteNotificationsBetween(fromDateMock, toDateMock);

		verify(notificationEntityRepositoryMock).deleteNotificationsBetween(fromDateMock, toDateMock);
	}

	@Test
	void getNotificationsByWebHookToken_ShouldGetAllNotificationsByTheGivenWebHookToken() {

		testObj.getNotificationsByWebHookToken(WEB_HOOK_TOKEN);

		verify(notificationEntityRepositoryMock).findNotificationsByWebHookToken(WEB_HOOK_TOKEN);
	}

	@Test
	void getNotificationsByObjectTokenAndAndCreationDateAfter_ShouldGetAllNotificationsByTheGivensObjectTokenAndCreationDate() {

		testObj.getNotificationsByObjectTokenAndAndCreationDateAfter(OBJECT_TOKEN, CREATION_DATE);

		verify(notificationEntityRepositoryMock).findNotificationsByObjectTokenAndAndCreationDateAfter(OBJECT_TOKEN,
				CREATION_DATE);
	}

}
