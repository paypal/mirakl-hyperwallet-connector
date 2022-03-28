package com.paypal.notifications.evaluator.predicates;

import com.paypal.notifications.model.entity.NotificationEntity;
import com.paypal.notifications.service.hmc.NotificationEntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

/**
 * Predicate for checking if a {@link NotificationEntity} is duplicated.
 */
@Slf4j
@Service
public class IsDuplicated extends AbstractNotificationPredicate implements Predicate<NotificationEntity> {

	public IsDuplicated(final NotificationEntityService notificationEntityService) {
		super(notificationEntityService);
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

		final boolean isDuplicated = super.notificationEntityService
				.getNotificationsByWebHookToken(notificationEntity.getWebHookToken()).size() > 1;

		if (isDuplicated) {
			log.warn("Duplicated notification: [{}]", notificationEntity.getWebHookToken());
		}

		return isDuplicated;
	}

}
