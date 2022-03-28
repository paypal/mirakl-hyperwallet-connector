package com.paypal.notifications.converter;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.notifications.model.entity.NotificationEntity;
import com.paypal.notifications.model.notification.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Notification converter that converts from {@link HyperwalletWebhookNotification} to
 * {@link NotificationEntity}.
 */
@Slf4j
@Service
public class NotificationConverter implements Converter<HyperwalletWebhookNotification, NotificationEntity> {

	private static final String TOKEN_MAP_KEY = "token";

	/**
	 * Retrieves a {@link HyperwalletWebhookNotification} and returns a
	 * {@link NotificationEntity}
	 * @param source the source object {@link HyperwalletWebhookNotification}
	 * @return the returned object {@link NotificationEntity}
	 */
	@Override
	public NotificationEntity convert(final HyperwalletWebhookNotification source) {

		final NotificationEntity target = new NotificationEntity();

		target.setWebHookToken(source.getToken());
		target.setCreationDate(source.getCreatedOn());
		target.setReceptionDate(DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault()));
		populateObjectToken(source, target);

		return target;
	}

	private void populateObjectToken(final HyperwalletWebhookNotification source, final NotificationEntity target) {

		//@formatter:off
        Optional.ofNullable(source.getObject())
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(map -> map.get(TOKEN_MAP_KEY))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(objectToken -> {
                    target.setObjectToken(objectToken);
                    target.setNotificationType(getNotificationType(objectToken));
                });
        //@formatter:on
	}

	private NotificationType getNotificationType(final String objectToken) {

		try {

			return NotificationType.valueOf(objectToken.split("-")[0].toUpperCase());

		}
		catch (final Exception exception) {

			log.warn("Notification with unknown object token [{}] received", objectToken);
			return NotificationType.UNK;
		}
	}

}
