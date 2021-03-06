package com.paypal.notifications.repository;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.service.hyperwallet.HyperwalletSDKService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationsRepositoryImplTest {

	private static final String HYPERWALLET_NOTIFICATION_TOKEN = "TEST_TOKEN";

	private static final String HYPERWALLET_NOTIFICATION_PROGRAM = "TEST_PROGRAM";

	private static final String MSG_ERROR = "An error has occurred";

	@InjectMocks
	private NotificationsRepositoryImpl testObj;

	@Mock
	private HyperwalletSDKService hyperwalletSDKService;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Mock
	private Hyperwallet hyperwalletInstanceMock;

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.ERROR)
			.recordForType(NotificationsRepositoryImpl.class);

	@Test
	void getHyperwalletWebhookNotification_shouldReturnAnHyperwalletNotification_WhenTokenExists() {
		when(hyperwalletSDKService.getHyperwalletInstance(HYPERWALLET_NOTIFICATION_PROGRAM))
				.thenReturn(hyperwalletInstanceMock);
		when(hyperwalletInstanceMock.getWebhookEvent(HYPERWALLET_NOTIFICATION_TOKEN))
				.thenReturn(hyperwalletWebhookNotificationMock);

		final HyperwalletWebhookNotification result = testObj
				.getHyperwalletWebhookNotification(HYPERWALLET_NOTIFICATION_PROGRAM, HYPERWALLET_NOTIFICATION_TOKEN);

		assertThat(result).isEqualTo(hyperwalletWebhookNotificationMock);
	}

	@Test
	void getHyperwalletWebhookNotification_shouldReturnNull_WhenTokenNotExists() {
		when(hyperwalletSDKService.getHyperwalletInstance(HYPERWALLET_NOTIFICATION_PROGRAM))
				.thenReturn(hyperwalletInstanceMock);
		when(hyperwalletInstanceMock.getWebhookEvent(HYPERWALLET_NOTIFICATION_TOKEN)).thenReturn(null);

		final HyperwalletWebhookNotification result = testObj
				.getHyperwalletWebhookNotification(HYPERWALLET_NOTIFICATION_PROGRAM, HYPERWALLET_NOTIFICATION_TOKEN);

		assertThat(result).isNull();
	}

	@Test
	void getHyperwalletWebhookNotification_shouldReturnNull_andLogException_WhenExceptionIsThrown() {
		when(hyperwalletSDKService.getHyperwalletInstance(HYPERWALLET_NOTIFICATION_PROGRAM))
				.thenReturn(hyperwalletInstanceMock);
		when(hyperwalletInstanceMock.getWebhookEvent(HYPERWALLET_NOTIFICATION_TOKEN))
				.thenThrow(new HyperwalletException(MSG_ERROR));

		final HyperwalletWebhookNotification result = testObj
				.getHyperwalletWebhookNotification(HYPERWALLET_NOTIFICATION_PROGRAM, HYPERWALLET_NOTIFICATION_TOKEN);

		assertThat(result).isNull();
		assertThat(logTrackerStub.contains(String.format("Could not fetch notification [%s] due to reason [%s]",
				HYPERWALLET_NOTIFICATION_TOKEN, MSG_ERROR))).isTrue();
	}

}
