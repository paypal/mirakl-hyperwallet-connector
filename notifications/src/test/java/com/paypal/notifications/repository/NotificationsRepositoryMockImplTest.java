package com.paypal.notifications.repository;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationsRepositoryMockImplTest {

	private static final String HYPERWALLET_NOTIFICATION1_TOKEN = "TEST_TOKEN_1";

	private static final String HYPERWALLET_NOTIFICATION2_TOKEN = "TEST_TOKEN_2";

	private static final String HYPERWALLET_NOTIFICATION3_TOKEN = "TEST_TOKEN_3";

	private static final String HYPERWALLET_NOTIFICATION_PROGRAM = "TEST_PROGRAM";

	@InjectMocks
	private NotificationsRepositoryMockImpl testObj;

	@Mock
	private NotificationsRepositoryImpl HyperwalletNotificationsRepositoryImplMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotification1FromTestSupportMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotification1FromHyperwalletMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotification2FromHyperwalletMock;

	@Mock
	private NotificationsInMemoryStore notificationsInMemoryStoreMock;

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.ERROR)
			.recordForType(NotificationsRepositoryImpl.class);

	@Test
	void getHyperwalletWebhookNotification_shouldReturnFromContext_whenTokenExists_andTokenInContext() {
		prepareMocks();

		final HyperwalletWebhookNotification result = testObj
				.getHyperwalletWebhookNotification(HYPERWALLET_NOTIFICATION_PROGRAM, HYPERWALLET_NOTIFICATION1_TOKEN);

		verify(HyperwalletNotificationsRepositoryImplMock, never())
				.getHyperwalletWebhookNotification(HYPERWALLET_NOTIFICATION_PROGRAM, HYPERWALLET_NOTIFICATION1_TOKEN);
		assertThat(result).isEqualTo(hyperwalletWebhookNotification1FromTestSupportMock);
	}

	@Test
	void getHyperwalletWebhookNotification_shouldReturnFromHyperwallet_whenTokenExists_andNotTokenInContext() {
		prepareMocks();

		final HyperwalletWebhookNotification result = testObj
				.getHyperwalletWebhookNotification(HYPERWALLET_NOTIFICATION_PROGRAM, HYPERWALLET_NOTIFICATION2_TOKEN);

		assertThat(result).isEqualTo(hyperwalletWebhookNotification2FromHyperwalletMock);
	}

	@Test
	void getHyperwalletWebhookNotification_shouldReturnNull_whenTokenNotExists() {
		prepareMocks();

		final HyperwalletWebhookNotification result = testObj
				.getHyperwalletWebhookNotification(HYPERWALLET_NOTIFICATION_PROGRAM, HYPERWALLET_NOTIFICATION3_TOKEN);

		assertThat(result).isNull();
	}

	private void prepareMocks() {
		when(notificationsInMemoryStoreMock.getNotificationByToken(HYPERWALLET_NOTIFICATION1_TOKEN))
				.thenReturn(Optional.of(hyperwalletWebhookNotification1FromTestSupportMock));
		when(notificationsInMemoryStoreMock.getNotificationByToken(HYPERWALLET_NOTIFICATION2_TOKEN))
				.thenReturn(Optional.empty());
		when(notificationsInMemoryStoreMock.getNotificationByToken(HYPERWALLET_NOTIFICATION3_TOKEN))
				.thenReturn(Optional.empty());
		when(HyperwalletNotificationsRepositoryImplMock
				.getHyperwalletWebhookNotification(HYPERWALLET_NOTIFICATION_PROGRAM, HYPERWALLET_NOTIFICATION1_TOKEN))
						.thenReturn(hyperwalletWebhookNotification1FromHyperwalletMock);
		when(HyperwalletNotificationsRepositoryImplMock
				.getHyperwalletWebhookNotification(HYPERWALLET_NOTIFICATION_PROGRAM, HYPERWALLET_NOTIFICATION2_TOKEN))
						.thenReturn(hyperwalletWebhookNotification2FromHyperwalletMock);
		when(HyperwalletNotificationsRepositoryImplMock
				.getHyperwalletWebhookNotification(HYPERWALLET_NOTIFICATION_PROGRAM, HYPERWALLET_NOTIFICATION3_TOKEN))
						.thenReturn(null);
	}

}
