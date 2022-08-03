package com.paypal.notifications.service.hmc.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.strategy.StrategyExecutor;
import com.paypal.notifications.converter.NotificationConverter;
import com.paypal.notifications.evaluator.NotificationEntityEvaluator;
import com.paypal.notifications.model.entity.NotificationEntity;
import com.paypal.notifications.service.hmc.NotificationEntityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

	@Spy
	@InjectMocks
	private NotificationServiceImpl testObj;

	@Mock
	private NotificationEntityService notificationEntityServiceMock;

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

		final InOrder inOrder = Mockito.inOrder(notificationConverterMock, notificationEntityServiceMock,
				hyperwalletWebhookNotificationSenderStrategyExecutorMock);

		inOrder.verify(notificationConverterMock).convert(hyperwalletWebhookNotification1Mock);
		inOrder.verify(notificationEntityServiceMock).saveNotification(notificationEntityMock);
		verify(hyperwalletWebhookNotificationSenderStrategyExecutorMock).execute(hyperwalletWebhookNotification1Mock);
	}

	@Test
	void processNotification_shouldSaveAndNotProcessIncomingNotificationAndReturnsOK_WhenNotificationIsNotProcessable() {
		when(notificationEntityEvaluatorMock.isProcessable(notificationEntityMock)).thenReturn(false);
		when(notificationConverterMock.convert(hyperwalletWebhookNotification1Mock)).thenReturn(notificationEntityMock);

		testObj.processNotification(hyperwalletWebhookNotification1Mock);

		final InOrder inOrder = Mockito.inOrder(notificationConverterMock, notificationEntityServiceMock,
				hyperwalletWebhookNotificationSenderStrategyExecutorMock);

		inOrder.verify(notificationConverterMock).convert(hyperwalletWebhookNotification1Mock);
		inOrder.verify(notificationEntityServiceMock).saveNotification(notificationEntityMock);
		verify(hyperwalletWebhookNotificationSenderStrategyExecutorMock, never())
				.execute(hyperwalletWebhookNotification1Mock);
	}

}
