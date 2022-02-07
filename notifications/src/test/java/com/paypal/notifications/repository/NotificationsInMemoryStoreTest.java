package com.paypal.notifications.repository;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationsInMemoryStoreTest {

	private static final String HYPERWALLET_NOTIFICATION1_TOKEN = "TEST_TOKEN_1";

	private static final String HYPERWALLET_NOTIFICATION2_TOKEN = "TEST_TOKEN_2";

	@InjectMocks
	private NotificationsInMemoryStore testObj;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotification1Mock, hyperwalletWebhookNotification2Mock;

	@Test
	void addNotifications_shouldAddCollectionOfNotifications() {
		when(hyperwalletWebhookNotification1Mock.getToken()).thenReturn(HYPERWALLET_NOTIFICATION1_TOKEN);
		when(hyperwalletWebhookNotification2Mock.getToken()).thenReturn(HYPERWALLET_NOTIFICATION2_TOKEN);

		List<HyperwalletWebhookNotification> notifications = List.of(hyperwalletWebhookNotification1Mock,
				hyperwalletWebhookNotification2Mock);
		testObj.addNotifications(notifications);

		Optional<HyperwalletWebhookNotification> result1 = testObj
				.getNotificationByToken(HYPERWALLET_NOTIFICATION1_TOKEN);
		Optional<HyperwalletWebhookNotification> result2 = testObj
				.getNotificationByToken(HYPERWALLET_NOTIFICATION2_TOKEN);

		assertThat(result1).hasValue(hyperwalletWebhookNotification1Mock);
		assertThat(result2).hasValue(hyperwalletWebhookNotification2Mock);
	}

	@Test
	void addNotification_shouldAddNotification() {
		when(hyperwalletWebhookNotification1Mock.getToken()).thenReturn(HYPERWALLET_NOTIFICATION1_TOKEN);
		testObj.addNotification(hyperwalletWebhookNotification1Mock);

		Optional<HyperwalletWebhookNotification> result = testObj
				.getNotificationByToken(HYPERWALLET_NOTIFICATION1_TOKEN);

		assertThat(result).hasValue(hyperwalletWebhookNotification1Mock);
	}

	@Test
	void getNotificationByToken_shouldReturnEmpty_whenTokenNotExists() {
		Optional<HyperwalletWebhookNotification> result = testObj
				.getNotificationByToken(HYPERWALLET_NOTIFICATION1_TOKEN);

		assertThat(result).isEmpty();
	}

	@Test
	void clear_shouldRemoveAllNotifications() {
		when(hyperwalletWebhookNotification1Mock.getToken()).thenReturn(HYPERWALLET_NOTIFICATION1_TOKEN);
		when(hyperwalletWebhookNotification2Mock.getToken()).thenReturn(HYPERWALLET_NOTIFICATION2_TOKEN);

		List<HyperwalletWebhookNotification> notifications = List.of(hyperwalletWebhookNotification1Mock,
				hyperwalletWebhookNotification2Mock);
		testObj.addNotifications(notifications);
		testObj.clear();

		Optional<HyperwalletWebhookNotification> result1 = testObj
				.getNotificationByToken(HYPERWALLET_NOTIFICATION1_TOKEN);
		Optional<HyperwalletWebhookNotification> result2 = testObj
				.getNotificationByToken(HYPERWALLET_NOTIFICATION2_TOKEN);

		assertThat(result1).isEmpty();
		assertThat(result2).isEmpty();
	}

}
