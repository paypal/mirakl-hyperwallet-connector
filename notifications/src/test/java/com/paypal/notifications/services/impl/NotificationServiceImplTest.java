package com.paypal.notifications.services.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.strategy.StrategyExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

	@InjectMocks
	private NotificationServiceImpl testObj;

	@Mock
	private StrategyExecutor<HyperwalletWebhookNotification, Void> hyperwalletWebhookNotificationSenderStrategyExecutorMock;

	@Test
	void processNotification_shouldCallStrategyExecutorWithIncomingNotification() {
		final HyperwalletWebhookNotification incomingNotificationDTO = new HyperwalletWebhookNotification();

		testObj.processNotification(incomingNotificationDTO);

		verify(hyperwalletWebhookNotificationSenderStrategyExecutorMock).execute(incomingNotificationDTO);
	}

}
