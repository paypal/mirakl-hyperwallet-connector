package com.paypal.notifications.controllers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.notifications.evaluator.NotificationEntityEvaluator;
import com.paypal.notifications.model.entity.NotificationEntity;
import com.paypal.notifications.service.NotificationService;
import com.paypal.notifications.service.hmc.NotificationEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncomingHyperwalletNotificationWebhookControllerTest {

	@InjectMocks
	private IncomingHyperwalletNotificationWebhookController testObj;

	@Mock
	private NotificationService notificationServiceMock;

	@Mock
	private NotificationEntityService notificationEntityServiceMock;

	@Mock
	private Converter<HyperwalletWebhookNotification, NotificationEntity> notificationConverterMock;

	@Mock
	private NotificationEntityEvaluator notificationEntityEvaluatorMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Mock
	private NotificationEntity notificationEntityMock;

	@BeforeEach
	public void setUp() {

		when(notificationConverterMock.convert(hyperwalletWebhookNotificationMock)).thenReturn(notificationEntityMock);
	}

	@Test
	void receiveIncomingNotification_shouldSaveAndProcessIncomingNotificationAndReturnsOK_WhenNotificationIsProcessable() {

		when(notificationEntityEvaluatorMock.isProcessable(notificationEntityMock)).thenReturn(true);

		testObj.receiveIncomingNotification(hyperwalletWebhookNotificationMock);

		final InOrder inOrder = Mockito.inOrder(notificationConverterMock, notificationEntityServiceMock,
				notificationServiceMock);

		inOrder.verify(notificationConverterMock).convert(hyperwalletWebhookNotificationMock);
		inOrder.verify(notificationEntityServiceMock).saveNotification(notificationEntityMock);
		inOrder.verify(notificationServiceMock).processNotification(hyperwalletWebhookNotificationMock);
	}

	@Test
	void receiveIncomingNotification_shouldSaveAndNotProcessIncomingNotificationAndReturnsOK_WhenNotificationIsNotProcessable() {

		when(notificationEntityEvaluatorMock.isProcessable(notificationEntityMock)).thenReturn(false);

		testObj.receiveIncomingNotification(hyperwalletWebhookNotificationMock);

		final InOrder inOrder = Mockito.inOrder(notificationConverterMock, notificationEntityServiceMock,
				notificationServiceMock);

		inOrder.verify(notificationConverterMock).convert(hyperwalletWebhookNotificationMock);
		inOrder.verify(notificationEntityServiceMock).saveNotification(notificationEntityMock);
		inOrder.verify(notificationServiceMock, never()).processNotification(hyperwalletWebhookNotificationMock);
	}

}
