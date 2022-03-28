package com.paypal.notifications.evaluator.impl;

import com.paypal.notifications.model.entity.NotificationEntity;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationEntityEvaluatorImplTest {

	private NotificationEntityEvaluatorImpl testObj;

	private final Predicate<NotificationEntity> isDuplicated = notificationEntity -> true;

	private final Predicate<NotificationEntity> isNotDuplicated = notificationEntity -> false;

	private final Predicate<NotificationEntity> isOutdated = notificationEntity -> true;

	private final Predicate<NotificationEntity> isNotOutdated = notificationEntity -> false;

	@Test
	void isProcessable_ShouldReturnFalse_WhenAllThePredicatesReturnTrue() {

		testObj = new NotificationEntityEvaluatorImpl(Set.of(isDuplicated, isOutdated));

		final boolean result = testObj.isProcessable(new NotificationEntity());

		assertThat(result).isFalse();
	}

	@Test
	void isProcessable_ShouldReturnFalse_WhenOnePredicateReturnsTrue() {

		testObj = new NotificationEntityEvaluatorImpl(Set.of(isDuplicated, isNotOutdated));

		final boolean result = testObj.isProcessable(new NotificationEntity());

		assertThat(result).isFalse();
	}

	@Test
	void isProcessable_ShouldReturnTrue_WhenAllThePredicatesReturnFalse() {

		testObj = new NotificationEntityEvaluatorImpl(Set.of(isNotDuplicated, isNotOutdated));

		final boolean result = testObj.isProcessable(new NotificationEntity());

		assertThat(result).isTrue();
	}

}
