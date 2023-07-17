package com.paypal.notifications.incoming.services;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.support.strategy.StrategyExecutor;
import com.paypal.notifications.incoming.services.converters.NotificationConverter;
import com.paypal.notifications.incoming.services.evaluators.NotificationEntityEvaluator;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.services.NotificationStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationProcessingServiceImplTest {

	@Spy
	@InjectMocks
	private NotificationProcessingServiceImpl testObj;

	@Mock
	private NotificationStorageService notificationStorageServiceMock;

	@Mock
	private NotificationConverter notificationConverterMock;

	@Mock
	private NotificationEntityEvaluator notificationEntityEvaluatorMock;

	@Mock
	private StrategyExecutor<HyperwalletWebhookNotification, Void> hyperwalletWebhookNotificationSenderStrategyExecutorMock;

	@Mock
	private NotificationEntity notificationEntityMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotification1Mock;

	@Test
	void processNotification_shouldSaveAndProcessIncomingNotificationAndReturnsOK_WhenNotificationIsProcessable() {
		when(notificationEntityEvaluatorMock.isProcessable(notificationEntityMock)).thenReturn(true);
		when(notificationConverterMock.convert(hyperwalletWebhookNotification1Mock)).thenReturn(notificationEntityMock);

		testObj.processNotification(hyperwalletWebhookNotification1Mock);

		final InOrder inOrder = Mockito.inOrder(notificationConverterMock, notificationStorageServiceMock,
				hyperwalletWebhookNotificationSenderStrategyExecutorMock);

		inOrder.verify(notificationConverterMock).convert(hyperwalletWebhookNotification1Mock);
		inOrder.verify(notificationStorageServiceMock).saveNotification(notificationEntityMock);
		verify(hyperwalletWebhookNotificationSenderStrategyExecutorMock).execute(hyperwalletWebhookNotification1Mock);
	}

	@Test
	void processNotification_shouldSaveAndNotProcessIncomingNotificationAndReturnsOK_WhenNotificationIsNotProcessable() {
		when(notificationEntityEvaluatorMock.isProcessable(notificationEntityMock)).thenReturn(false);
		when(notificationConverterMock.convert(hyperwalletWebhookNotification1Mock)).thenReturn(notificationEntityMock);

		testObj.processNotification(hyperwalletWebhookNotification1Mock);

		final InOrder inOrder = Mockito.inOrder(notificationConverterMock, notificationStorageServiceMock,
				hyperwalletWebhookNotificationSenderStrategyExecutorMock);

		inOrder.verify(notificationConverterMock).convert(hyperwalletWebhookNotification1Mock);
		inOrder.verify(notificationStorageServiceMock).saveNotification(notificationEntityMock);
		verify(hyperwalletWebhookNotificationSenderStrategyExecutorMock, never())
				.execute(hyperwalletWebhookNotification1Mock);
	}

}
