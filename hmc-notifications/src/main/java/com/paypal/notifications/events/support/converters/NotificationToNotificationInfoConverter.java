package com.paypal.notifications.events.support.converters;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.notifications.storage.repositories.entities.NotificationInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class NotificationToNotificationInfoConverter
		implements Converter<HyperwalletWebhookNotification, NotificationInfoEntity> {

	/**
	 * Method that retrieves a {@link HyperwalletWebhookNotification} and returns a
	 * {@link NotificationInfoEntity}
	 * @param source the source object {@link HyperwalletWebhookNotification}
	 * @return the returned object {@link NotificationInfoEntity}
	 */
	@Override
	public NotificationInfoEntity convert(final HyperwalletWebhookNotification source) {
		final NotificationInfoEntity notificationInfoEntityNotification = new NotificationInfoEntity();
		notificationInfoEntityNotification.setNotificationToken(source.getToken());
		notificationInfoEntityNotification.setType(source.getType());
		notificationInfoEntityNotification.setCreationDate(source.getCreatedOn());

		final Object notificationBody = source.getObject();
		if (notificationBody instanceof Map) {
			final Map<String, String> notificationDetails = (Map<String, String>) notificationBody;

			Optional.ofNullable(notificationDetails.get("token"))
					.ifPresent(notificationInfoEntityNotification::setTarget);
			Optional.ofNullable(notificationDetails.get("programToken"))
					.ifPresent(notificationInfoEntityNotification::setProgram);
		}
		else {
			log.error("Notification [{}] has an empty body", source.getToken());
		}

		return notificationInfoEntityNotification;
	}

}
