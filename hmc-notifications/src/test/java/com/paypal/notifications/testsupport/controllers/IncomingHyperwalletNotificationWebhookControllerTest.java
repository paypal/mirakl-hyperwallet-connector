package com.paypal.notifications.testsupport.controllers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.incoming.services.NotificationProcessingService;
import com.paypal.notifications.incoming.controllers.IncomingHyperwalletNotificationWebhookController;
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
	private NotificationProcessingService notificationProcessingServiceMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Test
	void receiveIncomingNotification_shouldDelegateToService() {
		testObj.receiveIncomingNotification(hyperwalletWebhookNotificationMock);

		verify(notificationProcessingServiceMock).processNotification(hyperwalletWebhookNotificationMock);
	}

}
