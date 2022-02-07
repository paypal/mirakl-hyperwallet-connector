package com.paypal.notifications.controllers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.repository.NotificationsInMemoryStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.SchedulerException;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OverrideHyperwalletWebhookNotificationsControllerMockTest {

	@InjectMocks
	private OverrideHyperwalletWebhookNotificationsMockController testObj;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotification1Mock, hyperwalletWebhookNotification2Mock;

	@Mock
	private NotificationsInMemoryStore notificationsInMemoryStoreMock;

	@Test
	void storeNotifications_shouldStoreNotificationsInMemory() throws SchedulerException {
		List<HyperwalletWebhookNotification> notifications = List.of(hyperwalletWebhookNotification1Mock,
				hyperwalletWebhookNotification2Mock);
		testObj.storeNotifications(notifications);

		verify(notificationsInMemoryStoreMock).clear();
		verify(notificationsInMemoryStoreMock).addNotifications(notifications);
	}

}
