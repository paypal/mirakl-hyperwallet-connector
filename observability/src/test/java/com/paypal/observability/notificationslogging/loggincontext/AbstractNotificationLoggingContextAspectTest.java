package com.paypal.observability.notificationslogging.loggincontext;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.observability.loggingcontext.service.LoggingContextService;
import com.paypal.observability.notificationslogging.model.NotificationLoggingTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractNotificationLoggingContextAspectTest {

	@Spy
	@InjectMocks
	private MyNotificationLoggingContextAspect testObj;

	@Mock
	private ProceedingJoinPoint pjpMock;

	@Mock
	private LoggingContextService loggingContextServiceMock;

	@Captor
	private ArgumentCaptor<NotificationLoggingTransaction> argumentCaptorLoggingNotification;

	@Test
	void interceptNotificationEventListeners_shouldCallProceedWhenNotificationIsNullAndZeroInteractionsWithLoggingContextService()
			throws Throwable {
		when(testObj.getNotificationObject(pjpMock)).thenReturn(null);

		testObj.doInterceptNotificationMethod(pjpMock);

		verify(pjpMock).proceed();
		verifyNoInteractions(loggingContextServiceMock);
	}

	@Test
	void interceptNotificationEventListeners_shouldAddContextualLoggingInformationBasedOnNotificationReceivedOngetNotificationObject()
			throws Throwable {
		testObj.doInterceptNotificationMethod(pjpMock);

		verify(loggingContextServiceMock).executeInLoggingContext(any(), argumentCaptorLoggingNotification.capture());
		final NotificationLoggingTransaction notificationLoggingTransaction = argumentCaptorLoggingNotification
				.getValue();
		assertThat(notificationLoggingTransaction.getId()).isEqualTo("token");
		assertThat(notificationLoggingTransaction.getType()).isEqualTo("Notification");
		assertThat(notificationLoggingTransaction.getSubtype()).isEqualTo("USER.PAYMENT.MOCKED");
		assertThat(notificationLoggingTransaction.getTargetToken()).isEqualTo("target-token");
		assertThat(notificationLoggingTransaction.getMiraklShopId()).isEqualTo("clientUserId");
		assertThat(notificationLoggingTransaction.getClientPaymentId()).isEqualTo("clientPaymentId");
	}

	public static class MyNotificationLoggingContextAspect extends AbstractNotificationLoggingContextAspect {

		protected MyNotificationLoggingContextAspect(final LoggingContextService loggingContextService) {
			super(loggingContextService);
		}

		@Override
		protected HyperwalletWebhookNotification getNotificationObject(final ProceedingJoinPoint pjp) {
			final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
			notification.setToken("token");
			notification.setType("USER.PAYMENT.MOCKED");
			notification.setObject(new InnerNotificationObject("target-token", "clientUserId", "clientPaymentId"));

			return notification;
		}

		public class InnerNotificationObject {

			private final String token;

			private final String clientUserId;

			private final String clientPaymentId;

			public InnerNotificationObject(final String token, final String clientUserId,
					final String clientPaymentId) {
				this.token = token;
				this.clientUserId = clientUserId;
				this.clientPaymentId = clientPaymentId;
			}

			public String getToken() {
				return token;
			}

			public String getClientUserId() {
				return clientUserId;
			}

			public String getClientPaymentId() {
				return clientPaymentId;
			}

		}

	}

}
