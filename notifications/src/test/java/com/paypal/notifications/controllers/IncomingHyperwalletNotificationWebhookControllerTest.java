package com.paypal.notifications.controllers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.service.NotificationService;
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
	private NotificationService notificationServiceMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Test
	void receiveIncomingNotification_shouldDelegateToService() {
		testObj.receiveIncomingNotification(hyperwalletWebhookNotificationMock);

		verify(notificationServiceMock).processNotification(hyperwalletWebhookNotificationMock);
	}

}
