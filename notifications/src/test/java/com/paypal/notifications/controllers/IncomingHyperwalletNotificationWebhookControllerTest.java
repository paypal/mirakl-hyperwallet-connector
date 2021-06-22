package com.paypal.notifications.controllers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.services.NotificationService;
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
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Mock
	private NotificationService notificationServiceMock;

	@Test
	void receiveIncomingNotification_shouldProcessIncomingNotificationAndReturnsOK() {

		testObj.receiveIncomingNotification(hyperwalletWebhookNotificationMock);

		verify(notificationServiceMock).processNotification(hyperwalletWebhookNotificationMock);

	}

}
