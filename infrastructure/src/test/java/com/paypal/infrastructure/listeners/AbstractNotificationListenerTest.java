package com.paypal.infrastructure.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.events.HMCEvent;
import com.paypal.infrastructure.events.PaymentEvent;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import com.paypal.infrastructure.repository.FailedNotificationInformationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractNotificationListenerTest {

	private static final int MAX_RETRIES = 5;

	private static final String TARGET_TOKEN = "targetToken";

	private static final String NOTIFICATION_TYPE = "TestNotification";

	private static final String NOTIFICATION_TOKEN = "notificationToken";

	private static final String EMAIL_SUBJECT = "[HMC] Technical error occurred when processing the notification %s";

	private static final String EMAIL_BODY = "There was an error processing the notification %s and the operation could not be completed. The maximum number of attempts (%d) has been reached, therefore it will not try to re-process the notification anymore."
			+ "\nPlease check the logs for further information.";

	private MyAbstractNotificationListener testObj;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private FailedNotificationInformationRepository failedNotificationInformationRepositoryMock;

	@Mock
	private Converter<HyperwalletWebhookNotification, NotificationInfoEntity> notificationInfoEntityToNotificationConverterMock;

	@Mock
	private PaymentEvent paymentEventMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Mock
	private NotificationInfoEntity notificationInfoEntityMock, oldNotificationEntityMock;

	@BeforeEach
	void setUp() {
		testObj = spy(new MyAbstractNotificationListener(mailNotificationUtilMock,
				failedNotificationInformationRepositoryMock, notificationInfoEntityToNotificationConverterMock));
		when(paymentEventMock.getNotification()).thenReturn(hyperwalletWebhookNotificationMock);
		when(notificationInfoEntityToNotificationConverterMock.convert(hyperwalletWebhookNotificationMock))
				.thenReturn(notificationInfoEntityMock);
		lenient().when(hyperwalletWebhookNotificationMock.getToken()).thenReturn(NOTIFICATION_TOKEN);
		lenient().when(notificationInfoEntityMock.getNotificationToken()).thenReturn(NOTIFICATION_TOKEN);
		lenient().doReturn(MAX_RETRIES).when(testObj).getMaxRetries();
		lenient().doReturn(true).when(testObj).hasEnabledNotificationRetries();
	}

	@Test
	void onApplicationEvent_whenNotificationIsNew_shouldProcessNotificationWithoutDeletingOrSavingNotifications() {
		testObj.onApplicationEvent(paymentEventMock);

		verify(testObj).processNotification(hyperwalletWebhookNotificationMock);
		verify(failedNotificationInformationRepositoryMock, never()).delete(any());
		verify(failedNotificationInformationRepositoryMock, never()).save(any());
	}

	@Test
	void onApplicationEvent_whenNotificationFailsDuringProcess_shouldSaveNotificationIncreasingRetryCounter() {
		doThrow(RuntimeException.class).when(testObj).processNotification(hyperwalletWebhookNotificationMock);

		testObj.onApplicationEvent(paymentEventMock);

		final InOrder inOrder = inOrder(testObj, notificationInfoEntityMock,
				failedNotificationInformationRepositoryMock);
		inOrder.verify(testObj).processNotification(hyperwalletWebhookNotificationMock);
		inOrder.verify(notificationInfoEntityMock).setRetryCounter(1);
		inOrder.verify(failedNotificationInformationRepositoryMock).save(notificationInfoEntityMock);
	}

	@Test
	void onApplicationEvent_whenNotificationAlreadyExistedInTheDatabase_shouldRemoveStoredNotification_andProcessNewNotification() {
		when(failedNotificationInformationRepositoryMock.findByNotificationToken(NOTIFICATION_TOKEN))
				.thenReturn(notificationInfoEntityMock);

		testObj.onApplicationEvent(paymentEventMock);

		final InOrder inOrder = inOrder(testObj, failedNotificationInformationRepositoryMock);
		inOrder.verify(failedNotificationInformationRepositoryMock).delete(notificationInfoEntityMock);
		inOrder.verify(testObj).processNotification(hyperwalletWebhookNotificationMock);
	}

	@Test
	void onApplicationEvent_whenNotificationExistedInTheDatabase_andNotificationFailsDuringProcess_andNotificationHasFailedLessThanTheMaxAmountOfAllowedRetries_shouldSaveNotificationIncreasingRetryCounter() {
		when(failedNotificationInformationRepositoryMock.findByNotificationToken(NOTIFICATION_TOKEN))
				.thenReturn(notificationInfoEntityMock);
		when(notificationInfoEntityMock.getRetryCounter()).thenReturn(1);
		doThrow(RuntimeException.class).when(testObj).processNotification(hyperwalletWebhookNotificationMock);

		testObj.onApplicationEvent(paymentEventMock);

		final InOrder inOrder = inOrder(testObj, notificationInfoEntityMock,
				failedNotificationInformationRepositoryMock);
		inOrder.verify(testObj).processNotification(hyperwalletWebhookNotificationMock);
		inOrder.verify(notificationInfoEntityMock).setRetryCounter(2);
		inOrder.verify(failedNotificationInformationRepositoryMock).save(notificationInfoEntityMock);
	}

	@Test
	void onApplicationEvent_whenReceivedNotificationIsNewerThanStoredNotification_shouldRemoveStoredNotification_andProcessNewNotification() {
		final Instant present = Instant.now();
		final Instant past = present.minus(2, ChronoUnit.DAYS);
		when(notificationInfoEntityMock.getType()).thenReturn(NOTIFICATION_TYPE);
		when(notificationInfoEntityMock.getTarget()).thenReturn(TARGET_TOKEN);
		when(notificationInfoEntityMock.getCreationDate()).thenReturn(Date.from(present));
		when(oldNotificationEntityMock.getCreationDate()).thenReturn(Date.from(past));
		when(failedNotificationInformationRepositoryMock.findByTypeAndTarget(NOTIFICATION_TYPE, TARGET_TOKEN))
				.thenReturn(oldNotificationEntityMock);

		testObj.onApplicationEvent(paymentEventMock);

		final InOrder inOrder = inOrder(testObj, failedNotificationInformationRepositoryMock);
		inOrder.verify(failedNotificationInformationRepositoryMock).delete(oldNotificationEntityMock);
		inOrder.verify(testObj).processNotification(hyperwalletWebhookNotificationMock);
	}

	@Test
	void onApplicationEvent_whenReceivedNotificationIsOlderThanStoredNotification_shouldNotRemoveStoredNotification_andNotProcessNewNotification() {
		final Instant present = Instant.now();
		final Instant past = present.minus(2, ChronoUnit.DAYS);
		when(notificationInfoEntityMock.getType()).thenReturn(NOTIFICATION_TYPE);
		when(notificationInfoEntityMock.getTarget()).thenReturn(TARGET_TOKEN);
		when(notificationInfoEntityMock.getCreationDate()).thenReturn(Date.from(past));
		when(oldNotificationEntityMock.getCreationDate()).thenReturn(Date.from(present));
		when(failedNotificationInformationRepositoryMock.findByTypeAndTarget(NOTIFICATION_TYPE, TARGET_TOKEN))
				.thenReturn(oldNotificationEntityMock);

		testObj.onApplicationEvent(paymentEventMock);

		verify(failedNotificationInformationRepositoryMock, never()).delete(oldNotificationEntityMock);
		verify(testObj, never()).processNotification(hyperwalletWebhookNotificationMock);
	}

	@Test
	void onApplicationEvent_whenNotitficationFailsDuringProcess_andNotificationHasFailedMaxAmountOfRetries_shouldSaveNotificationIncreasingRetryCounter() {
		when(failedNotificationInformationRepositoryMock.findByNotificationToken(NOTIFICATION_TOKEN))
				.thenReturn(notificationInfoEntityMock);
		when(notificationInfoEntityMock.getRetryCounter()).thenReturn(MAX_RETRIES + 20);
		doThrow(RuntimeException.class).when(testObj).processNotification(hyperwalletWebhookNotificationMock);

		testObj.onApplicationEvent(paymentEventMock);

		final InOrder inOrder = inOrder(failedNotificationInformationRepositoryMock, testObj, mailNotificationUtilMock);
		inOrder.verify(failedNotificationInformationRepositoryMock).delete(notificationInfoEntityMock);
		inOrder.verify(testObj).processNotification(hyperwalletWebhookNotificationMock);
		inOrder.verify(mailNotificationUtilMock).sendPlainTextEmail(String.format(EMAIL_SUBJECT, NOTIFICATION_TOKEN),
				String.format(EMAIL_BODY, NOTIFICATION_TOKEN, MAX_RETRIES));
		verify(failedNotificationInformationRepositoryMock, never()).save(any());
	}

	@Test
	void onApplicationEvent_whenNotitficationFailsDuringProcess_andNotificationRetriesAreDisabled_shouldNotSaveNotification() {
		doThrow(RuntimeException.class).when(testObj).processNotification(hyperwalletWebhookNotificationMock);
		doReturn(false).when(testObj).hasEnabledNotificationRetries();

		testObj.onApplicationEvent(paymentEventMock);

		verify(testObj).processNotification(hyperwalletWebhookNotificationMock);
		verify(failedNotificationInformationRepositoryMock, never()).save(any());
	}

	private static class MyAbstractNotificationListener extends AbstractNotificationListener<HMCEvent> {

		private MyAbstractNotificationListener(final MailNotificationUtil mailNotificationUtil,
				final FailedNotificationInformationRepository failedNotificationInformationRepository,
				final Converter<HyperwalletWebhookNotification, NotificationInfoEntity> notificationInfoEntityToNotificationConverter) {
			super(mailNotificationUtil, failedNotificationInformationRepository,
					notificationInfoEntityToNotificationConverter);
		}

		@Override
		protected void processNotification(final HyperwalletWebhookNotification notification) {
			// Do nothing
		}

		@Override
		protected String getNotificationType() {
			return NOTIFICATION_TYPE;
		}

	}

}
