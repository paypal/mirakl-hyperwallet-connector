package com.paypal.notifications.incoming.services.evaluators.predicates;

import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.services.NotificationStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

/**
 * Predicate for checking if a {@link NotificationEntity} is duplicated.
 */
@Slf4j
@Service
public class IsDuplicated extends AbstractNotificationPredicate implements Predicate<NotificationEntity> {

	public IsDuplicated(final NotificationStorageService notificationStorageService) {
		super(notificationStorageService);
	}

	/**
	 * Checks if there are more than one {@link NotificationEntity} in the database with
	 * the same token.
	 * @param notificationEntity {@link NotificationEntity} to be checked.
	 * @return true if there are more than one notification in the database with the same
	 * token, false otherwise.
	 */
	@Override
	public boolean test(final NotificationEntity notificationEntity) {

		final boolean isDuplicated = !super.notificationStorageService
				.getNotificationsByWebHookToken(notificationEntity.getWebHookToken()).isEmpty();

		if (isDuplicated) {
			log.warn("Duplicated notification: [{}]", notificationEntity.getWebHookToken());
		}

		return isDuplicated;
	}

}
