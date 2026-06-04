package com.paypal.observability.notificationslogging.loggincontext;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationListenerLoggingContextAspectTest {

	@Spy
	@InjectMocks
	private NotificationListenerLoggingContextAspect testObj;

	@Mock
	private ProceedingJoinPoint pjpMock;

	@Test
	void getNotificationObject_shouldReturnNotificationObjectWhenSecondArgIsHyperwalletWebhookNotification() {
		final NotificationEntity entity = mock(NotificationEntity.class);
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		when(pjpMock.getArgs()).thenReturn(new Object[] { entity, notification });

		final HyperwalletWebhookNotification result = testObj.getNotificationObject(pjpMock);

		assertThat(result).isEqualTo(notification);
	}

	@Test
	void getNotificationObject_shouldReturnNullWhenSecondArgumentIsNotHyperwalletWebhookNotification() {
		final NotificationEntity entity = mock(NotificationEntity.class);
		when(pjpMock.getArgs()).thenReturn(new Object[] { entity, "not-a-notification" });

		final HyperwalletWebhookNotification result = testObj.getNotificationObject(pjpMock);

		assertThat(result).isNull();
	}

	@Test
	void getNotificationObject_shouldReturnNullWhenArgsHasLessThanTwoElements() {
		when(pjpMock.getArgs()).thenReturn(new Object[] { mock(NotificationEntity.class) });

		final HyperwalletWebhookNotification result = testObj.getNotificationObject(pjpMock);

		assertThat(result).isNull();
	}

	@Test
	void getNotificationObject_shouldReturnNullWhenArgsIsEmpty() {
		when(pjpMock.getArgs()).thenReturn(new Object[] {});

		final HyperwalletWebhookNotification result = testObj.getNotificationObject(pjpMock);

		assertThat(result).isNull();
	}

}
