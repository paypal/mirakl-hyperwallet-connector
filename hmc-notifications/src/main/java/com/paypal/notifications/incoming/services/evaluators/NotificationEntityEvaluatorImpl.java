package com.paypal.notifications.incoming.services.evaluators;

import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Default implementation of {@link NotificationEntityEvaluator}.
 */
@Service
public class NotificationEntityEvaluatorImpl implements NotificationEntityEvaluator {

	private final Set<Predicate<NotificationEntity>> notificationEvaluators;

	public NotificationEntityEvaluatorImpl(final Set<Predicate<NotificationEntity>> notificationEvaluators) {
		this.notificationEvaluators = notificationEvaluators;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isProcessable(final NotificationEntity notificationEntity) {

		return notificationEvaluators.stream().noneMatch(predicate -> predicate.test(notificationEntity));
	}

}
