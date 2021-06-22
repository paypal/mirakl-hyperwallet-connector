package com.paypal.notifications.services.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.strategy.StrategyFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

	@InjectMocks
	private NotificationServiceImpl testObj;

	@Mock
	private StrategyFactory<HyperwalletWebhookNotification, Optional<Void>> hyperwalletWebhookNotificationSenderStrategyFactoryMock;

	@Test
	void processNotification_shouldCallStrategyFactoryWithIncomingNotification() {
		final HyperwalletWebhookNotification incomingNotificationDTO = new HyperwalletWebhookNotification();

		this.testObj.processNotification(incomingNotificationDTO);

		verify(this.hyperwalletWebhookNotificationSenderStrategyFactoryMock).execute(incomingNotificationDTO);
	}

}