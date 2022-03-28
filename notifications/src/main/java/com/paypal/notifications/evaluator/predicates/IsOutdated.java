package com.paypal.notifications.evaluator.predicates;

import com.paypal.notifications.model.entity.NotificationEntity;
import com.paypal.notifications.service.hmc.NotificationEntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.function.Predicate;

/**
 * Predicate for checking if a {@link NotificationEntity} is outdated.
 */
@Slf4j
@Service
public class IsOutdated extends AbstractNotificationPredicate implements Predicate<NotificationEntity> {

	public IsOutdated(final NotificationEntityService notificationEntityService) {
		super(notificationEntityService);
	}

	/**
	 * Checks if there is any {@link NotificationEntity} in the database with the same
	 * object token and a later creation date.
	 * @param notificationEntity {@link NotificationEntity} to be checked.
	 * @return true if there are notifications in the database with the same object token
	 * and a later creation date, false otherwise.
	 */
	@Override
	public boolean test(final NotificationEntity notificationEntity) {

		final boolean isOutdated = !CollectionUtils
				.isEmpty(super.notificationEntityService.getNotificationsByObjectTokenAndAndCreationDateAfter(
						notificationEntity.getObjectToken(), notificationEntity.getCreationDate()));

		if (isOutdated) {
			log.warn("Outdated notification: [{}]", notificationEntity.getWebHookToken());
		}

		return isOutdated;
	}

}
