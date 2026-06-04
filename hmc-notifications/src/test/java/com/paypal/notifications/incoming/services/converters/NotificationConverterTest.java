package com.paypal.notifications.incoming.services.converters;

import static org.assertj.core.api.Assertions.assertThat;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Date;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class NotificationConverterTest {

	@InjectMocks
	private NotificationConverter testObj;

	@Test
	void convert_shouldMapTokenAndCreationDate() {
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		final Date createdOn = new Date();
		notification.setToken("wbh-token-1");
		notification.setCreatedOn(createdOn);

		final NotificationEntity result = testObj.convert(notification);

		assertThat(result.getWebHookToken()).isEqualTo("wbh-token-1");
		assertThat(result.getCreationDate()).isEqualTo(createdOn);
		assertThat(result.getReceptionDate()).isNotNull();
	}

	@Test
	void convert_shouldDeriveNotificationTypeFromObjectToken() {
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		notification.setToken("wbh-token-1");
		notification.setObject(Map.of("token", "usr-some-user-token"));

		final NotificationEntity result = testObj.convert(notification);

		assertThat(result.getNotificationType()).isEqualTo(NotificationType.USR);
		assertThat(result.getObjectToken()).isEqualTo("usr-some-user-token");
	}

	@Test
	void convert_shouldSetUnknownType_whenObjectTokenPrefixIsUnrecognised() {
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		notification.setToken("wbh-token-1");
		notification.setObject(Map.of("token", "xyz-unknown-token"));

		final NotificationEntity result = testObj.convert(notification);

		assertThat(result.getNotificationType()).isEqualTo(NotificationType.UNK);
	}

	@Test
	void convert_shouldLeaveObjectTokenNull_whenObjectIsNull() {
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		notification.setToken("wbh-token-1");

		final NotificationEntity result = testObj.convert(notification);

		assertThat(result.getObjectToken()).isNull();
		assertThat(result.getNotificationType()).isNull();
	}

}
