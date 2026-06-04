package com.paypal.notifications.incoming.services;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationStatus;

import java.util.List;
import java.util.Map;

/**
 * Service that manages the notification processing queue.
 */
public interface NotificationProcessingQueueService {

	/**
	 * Validates and enqueues the incoming notification for later processing.
	 * <p>
	 * Duplicate notifications (same {@code webHookToken}) and outdated notifications
	 * (same {@code objectToken} + {@code notificationType} but a newer entry already
	 * exists in the queue) are silently discarded.
	 * @param notification the incoming {@link HyperwalletWebhookNotification}.
	 */
	void enqueue(HyperwalletWebhookNotification notification);

	/**
	 * Returns the next batch of notifications eligible for processing. Eligible
	 * notifications have status {@code PENDING} or {@code RETRYING}, ordered by creation
	 * date ascending (oldest first).
	 * @param batchSize maximum number of notifications to return.
	 * @return a {@link List} of {@link NotificationEntity} ready to be processed.
	 */
	List<NotificationEntity> fetchNextBatch(int batchSize);

	/**
	 * Persists the processing outcome for each notification in the given map and resolves
	 * the resulting {@link NotificationStatus} according to the following rules:
	 * <ul>
	 * <li>{@link NotificationProcessingService.NotificationProcessingStatus#SUCCESS} —
	 * the notification is marked {@link NotificationStatus#SUCCESS} and no further action
	 * is taken.</li>
	 * <li>{@link NotificationProcessingService.NotificationProcessingStatus#ERROR} — the
	 * stored retry counter is incremented. If the new counter is below the configured
	 * maximum the notification is marked {@link NotificationStatus#RETRYING} so it will
	 * be picked up in a future batch; once the maximum is reached it is permanently
	 * marked {@link NotificationStatus#FAILED} and an alert email is sent.</li>
	 * </ul>
	 * @param tokenStatusesMap a map of {@code webHookToken} to the
	 * {@link NotificationProcessingService.NotificationProcessingStatus} returned by the
	 * processing service for that notification.
	 */
	void updateStatus(Map<String, NotificationProcessingService.NotificationProcessingStatus> tokenStatusesMap);

}
