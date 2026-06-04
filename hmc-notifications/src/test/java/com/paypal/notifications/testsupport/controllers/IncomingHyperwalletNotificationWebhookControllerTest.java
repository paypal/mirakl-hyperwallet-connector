package com.paypal.notifications.testsupport.controllers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.incoming.controllers.IncomingHyperwalletNotificationWebhookController;
import com.paypal.notifications.incoming.services.NotificationProcessingQueueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IncomingHyperwalletNotificationWebhookControllerTest {

	@InjectMocks
	private IncomingHyperwalletNotificationWebhookController testObj;

	@Mock
	private NotificationProcessingQueueService notificationProcessingQueueServiceMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Test
	void receiveIncomingNotification_shouldDelegateToQueueService() {
		testObj.receiveIncomingNotification(hyperwalletWebhookNotificationMock);

		verify(notificationProcessingQueueServiceMock).enqueue(hyperwalletWebhookNotificationMock);
	}

}
