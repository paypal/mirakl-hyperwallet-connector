package com.paypal.observability.notificationslogging.loggincontext;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceLoggingContextAspectTest {

	@Spy
	@InjectMocks
	private NotificationServiceLoggingContextAspect testObj;

	@Mock
	private ProceedingJoinPoint pjpMock;

	@Test
	void interceptNotificationMethod_shouldCallSuper() throws Throwable {
		doNothing().when(testObj).doInterceptNotificationMethod(pjpMock);

		testObj.interceptNotificationMethod(pjpMock);

		verify(testObj).doInterceptNotificationMethod(pjpMock);
	}

	@Test
	void getNotificationObject_shouldReturnNotificationObjectWhenArgIsOfTypeHyperwalletNotification() {
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		when(pjpMock.getArgs()).thenReturn(new Object[] { notification });

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

}
