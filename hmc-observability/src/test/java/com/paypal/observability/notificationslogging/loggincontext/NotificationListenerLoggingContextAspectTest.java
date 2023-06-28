package com.paypal.observability.notificationslogging.loggincontext;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.events.model.HMCEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationListenerLoggingContextAspectTest {

	@Spy
	@InjectMocks
	private NotificationListenerLoggingContextAspect testObj;

	@Mock
	private ProceedingJoinPoint pjpMock;

	@Test
	void getNotificationObject_shouldReturnNotificationObjectWhenArgIsOfTypeHyperwalletNotification() {
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		final HMCEvent hmcEvent = new MyEvent(new Object(), notification);
		when(pjpMock.getArgs()).thenReturn(new Object[] { hmcEvent });

		final HyperwalletWebhookNotification result = testObj.getNotificationObject(pjpMock);

		assertThat(result).isEqualTo(notification);
	}

	@Test
	void getNotificationObject_shouldReturnNullWhenArgumentIsNotOfTypeHyperwalletNotification() {
		final String notification = "";
		when(pjpMock.getArgs()).thenReturn(new Object[] { notification });

		final HyperwalletWebhookNotification result = testObj.getNotificationObject(pjpMock);

		assertThat(result).isNull();
	}

	@Test
	void getNotificationObject_shouldReturnNullWhenArgumentIsNull() {
		when(pjpMock.getArgs()).thenReturn(new Object[] { null });

		final HyperwalletWebhookNotification result = testObj.getNotificationObject(pjpMock);

		assertThat(result).isNull();
	}

	private static class MyEvent extends HMCEvent {

		protected MyEvent(final Object source, final HyperwalletWebhookNotification notification) {
			super(source, notification);
		}

	}

}
