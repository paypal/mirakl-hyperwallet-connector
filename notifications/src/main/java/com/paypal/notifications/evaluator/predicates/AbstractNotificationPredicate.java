package com.paypal.notifications.evaluator.predicates;

import com.paypal.notifications.service.hmc.NotificationEntityService;

/**
 * Abstract notification predicate for handling common functionality.
 */
public abstract class AbstractNotificationPredicate {

	protected final NotificationEntityService notificationEntityService;

	protected AbstractNotificationPredicate(final NotificationEntityService notificationEntityService) {
		this.notificationEntityService = notificationEntityService;
	}

}
