package com.paypal.notifications.service.hmc.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import com.paypal.infrastructure.repository.FailedNotificationInformationRepository;
import com.paypal.infrastructure.strategy.StrategyExecutor;
import com.paypal.notifications.repository.NotificationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

	private static final String TOKEN_1 = "token1";

	private static final String TOKEN_2 = "token2";

	private static final String TOKEN_3 = "token3";

	private static final String PROGRAM_TOKEN = "programToken";

	@Spy
	@InjectMocks
	private NotificationServiceImpl testObj;

	@Mock
	private NotificationsRepository notificationsRepositoryMock;

	@Mock
	private FailedNotificationInformationRepository failedNotificationInformationRepositoryMock;

	@Mock
	private StrategyExecutor<HyperwalletWebhookNotification, Void> hyperwalletWebhookNotificationSenderStrategyExecutorMock;

	@Mock
	private NotificationInfoEntity notificationInfoEntity1Mock, notificationInfoEntity2Mock,
			notificationInfoEntity3Mock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotification1Mock, hyperwalletWebhookNotification2Mock;

	@Test
	void processNotification_shouldCallStrategyExecutorWithIncomingNotification() {
		final HyperwalletWebhookNotification incomingNotificationDTO = new HyperwalletWebhookNotification();

		testObj.processNotification(incomingNotificationDTO);

		verify(hyperwalletWebhookNotificationSenderStrategyExecutorMock).execute(incomingNotificationDTO);
	}

	@Test
	void processFailedNotifications_whenFailedNotificationsExist_shouldProcessFailedNotifications_andSkipNotificationsThatCantBeFetched() {
		when(failedNotificationInformationRepositoryMock.findAll()).thenReturn(
				List.of(notificationInfoEntity1Mock, notificationInfoEntity2Mock, notificationInfoEntity3Mock));
		when(notificationInfoEntity1Mock.getNotificationToken()).thenReturn(TOKEN_1);
		when(notificationInfoEntity1Mock.getProgram()).thenReturn(PROGRAM_TOKEN);
		when(notificationInfoEntity2Mock.getNotificationToken()).thenReturn(TOKEN_2);
		when(notificationInfoEntity2Mock.getProgram()).thenReturn(PROGRAM_TOKEN);
		when(notificationInfoEntity3Mock.getNotificationToken()).thenReturn(TOKEN_3);
		when(notificationInfoEntity3Mock.getProgram()).thenReturn(PROGRAM_TOKEN);
		when(notificationsRepositoryMock.getHyperwalletWebhookNotification(PROGRAM_TOKEN, TOKEN_1))
				.thenReturn(hyperwalletWebhookNotification1Mock);
		when(notificationsRepositoryMock.getHyperwalletWebhookNotification(PROGRAM_TOKEN, TOKEN_2))
				.thenReturn(hyperwalletWebhookNotification2Mock);
		when(notificationsRepositoryMock.getHyperwalletWebhookNotification(PROGRAM_TOKEN, TOKEN_3)).thenReturn(null);

		testObj.processFailedNotifications();

		verify(testObj).processNotification(hyperwalletWebhookNotification1Mock);
		verify(testObj).processNotification(hyperwalletWebhookNotification2Mock);
	}

}
