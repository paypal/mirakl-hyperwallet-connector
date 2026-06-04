package com.paypal.notifications.incoming.services;

import com.paypal.notifications.storage.repositories.entities.NotificationEntity;

/**
 * Service responsible for processing queued notifications.
 */
public interface NotificationProcessingService {

	enum NotificationProcessingStatus {

		SUCCESS, ERROR

	}

	/**
	 * Processes a queued {@link NotificationEntity} by retrieving the full webhook
	 * payload from cache, selecting the appropriate handler, and invoking it.
	 * @param entity the {@link NotificationEntity} to process.
	 * @return {@link NotificationProcessingService.NotificationProcessingStatus#SUCCESS}
	 * if the notification was handled successfully, or
	 * {@link NotificationProcessingService.NotificationProcessingStatus#ERROR} otherwise.
	 */
	NotificationProcessingStatus processNotification(NotificationEntity entity);

}
