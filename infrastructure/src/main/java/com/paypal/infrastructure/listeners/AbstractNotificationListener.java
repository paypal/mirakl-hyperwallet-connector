package com.paypal.infrastructure.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.events.HMCEvent;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import com.paypal.infrastructure.repository.FailedNotificationInformationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;

/**
 * Abstract notification listener that encapsulates the logic to store failed
 * notifications and reprocess them if need be
 *
 * @param <T> Type of the event to be processed
 */
@Slf4j
public abstract class AbstractNotificationListener<T extends HMCEvent> implements ApplicationListener<T> {

	private static final String MSG_NOTIFICATION_RESTARTED = "Notification [{}] failed previously and will be restarted";

	private static final String MSG_FAILED_NOTIFICATION_STORED_FOR_RETRY = "Failed notification with token [{}] will be stored to be restarted in the future.";

	private static final String MSG_OUTDATED_NOTIFICATION = "Received outdated notification [{}] targeting the same type and token target as a more up-to-date, stored notification [{}]. The outdated notification will be skipped";

	private static final String MSG_MORE_RECENT_NOTIFICATION = "Received notification [{}] targets the same type and token target as an outdated stored notification [{}]. Since it is more recent, the latter will be removed from the database";

	private static final String MSG_EXCEEDED_NUM_OF_RETRIES = "Failed notification with token [{}] exceeded its max amount of retries and could not be processed by the HMC - It will be removed from the database. This incident will be notified via email";

	private static final String EMAIL_SUBJECT = "[HMC] Technical error occurred when processing the notification %s";

	private static final String EMAIL_BODY = "There was an error processing the notification %s and the operation could not be completed. The maximum number of attempts (%d) has been reached, therefore it will not try to re-process the notification anymore."
			+ "\nPlease check the logs for further information.";

	@Value("#{'${notifications.retry}'}")
	private boolean retryNotifications;

	@Value("${notifications.max.retries}")
	private int maxRetries;

	private final MailNotificationUtil mailNotificationUtil;

	private final FailedNotificationInformationRepository failedNotificationInformationRepository;

	private final Converter<HyperwalletWebhookNotification, NotificationInfoEntity> notificationInfoEntityToNotificationConverter;

	protected AbstractNotificationListener(final MailNotificationUtil mailNotificationUtil,
			final FailedNotificationInformationRepository failedNotificationInformationRepository,
			final Converter<HyperwalletWebhookNotification, NotificationInfoEntity> notificationInfoEntityToNotificationConverter) {
		this.mailNotificationUtil = mailNotificationUtil;
		this.failedNotificationInformationRepository = failedNotificationInformationRepository;
		this.notificationInfoEntityToNotificationConverter = notificationInfoEntityToNotificationConverter;
	}

	/**
	 * Processes recently received notifications from Hyperwallet or the HMC notification
	 * retry job. It attempts to find any notification that failed in the past by checking
	 * in the database in case there's any notification with...
	 * <ul>
	 * <li>The same token - In which case it retries a failed notification Failed
	 * notifications can be retried up to X amount of times (can be configured for each
	 * listener) before they are discarded and a mail is sent to notify any integrator of
	 * the issue that has happened.</li>
	 * <li>The same type and target token, but a different notification token - In which
	 * case it treats the recently received notification as an update of the one that was
	 * stored</li>
	 * </ul>
	 * If such a notification is found, the HMC will remove it from the database. If none
	 * is found, the HMC will process any newly received notification as usual.
	 * <p>
	 * If a notification fails during its process, it will be stored in the database to be
	 * retried in the future, such as by running the notification retry job.
	 * @param event Event that is being processed
	 */
	@Override
	public final void onApplicationEvent(final T event) {
		final HyperwalletWebhookNotification notification = event.getNotification();
		log.info("Processing incoming {} notification [{}] ", getNotificationType(), notification.getToken());

		final NotificationInfoEntity notificationInfoEntity = notificationInfoEntityToNotificationConverter
				.convert(notification);

		final Integer retryCount = getRetryCountForNotification(notificationInfoEntity);

		if (retryCount != null) {
			try {
				processNotification(notification);
			}
			catch (final RuntimeException ex) {
				handleNotificationFailure(notificationInfoEntity, retryCount, ex);
			}
		}
	}

	/**
	 * Gets the retry counter of the notification that is being processed To do so, it
	 * checks for any notifications that are stored it the database like so:
	 * <ul>
	 * <li>The received notification has the same token as a failed notification If one is
	 * found, it implies that the received notification and the stored one are the same.
	 * The system will retry that notification in hopes that it can be processed correctly
	 * this time around.
	 * <p>
	 * (Returns the number of retries left in the stored notification)</li>
	 * <li>The received notification has the same type and target token as a failed
	 * notification If one is found, it implies that the notification that is received
	 * serves the same purpose as the one that was stored, but has different data. We can
	 * tell if it's an <b>update</b> or is <b>outdated</b> based on its creation date.
	 * <ul>
	 * <li>The received notification is an update if was created after the one that was
	 * stored in the HMC. In that case, the stored notification is removed and the update
	 * is processed
	 * <p>
	 * (Returns 0, stating that the notification is being processed for the first
	 * time)</li>
	 * <li>The received notification is outdated if was created before the one that was
	 * stored in the HMC. In that case, the stored notification is kept in the system for
	 * retry. Since the one that was received is outdated, it can be skipped
	 * <p>
	 * (Returns null, stating that it'll be skipped)</li>
	 * </ul>
	 *
	 * </li>
	 * </ul>
	 * If no notifications are found, it returns a zero by default.
	 * <p>
	 * @param notification The notification used to check if there are any coincidences in
	 * the DB
	 */
	private Integer getRetryCountForNotification(final NotificationInfoEntity notification) {
		final String notificationToken = notification.getNotificationToken();

		final NotificationInfoEntity notificationWithSameToken = failedNotificationInformationRepository
				.findByNotificationToken(notificationToken);
		if (notificationWithSameToken != null) {
			log.info(MSG_NOTIFICATION_RESTARTED, notificationToken);
			failedNotificationInformationRepository.delete(notificationWithSameToken);
			return notificationWithSameToken.getRetryCounter();
		}

		final NotificationInfoEntity notificationWithSamePurpose = failedNotificationInformationRepository
				.findByTypeAndTarget(notification.getType(), notification.getTarget());
		if (notificationWithSamePurpose != null) {
			final String storedNotificationToken = notificationWithSamePurpose.getNotificationToken();

			if (notificationWithSamePurpose.getCreationDate().after(notification.getCreationDate())) {
				log.info(MSG_OUTDATED_NOTIFICATION, notification.getNotificationToken(), storedNotificationToken);
				return null;
			}
			else {
				log.info(MSG_MORE_RECENT_NOTIFICATION, notification.getNotificationToken(), storedNotificationToken);
				failedNotificationInformationRepository.delete(notificationWithSamePurpose);
			}
		}

		return 0;
	}

	private void handleNotificationFailure(final NotificationInfoEntity notificationInfoEntity, final int retries,
			final RuntimeException ex) {
		final String token = notificationInfoEntity.getNotificationToken();
		log.error("Notification [{}] could not be processed. Reason: [{}]", token, ex.getMessage());

		if (hasEnabledNotificationRetries()) {
			if (retries + 1 < getMaxRetries()) {
				log.error(MSG_FAILED_NOTIFICATION_STORED_FOR_RETRY, token);
				notificationInfoEntity.setRetryCounter(retries + 1);
				failedNotificationInformationRepository.save(notificationInfoEntity);
			}
			else {
				log.error(MSG_EXCEEDED_NUM_OF_RETRIES, token);
				mailNotificationUtil.sendPlainTextEmail(String.format(EMAIL_SUBJECT, token),
						String.format(EMAIL_BODY, token, getMaxRetries()));
			}
		}
	}

	protected boolean hasEnabledNotificationRetries() {
		return retryNotifications;
	}

	protected int getMaxRetries() {
		return maxRetries;
	}

	protected abstract void processNotification(final HyperwalletWebhookNotification notification);

	protected abstract String getNotificationType();

}
