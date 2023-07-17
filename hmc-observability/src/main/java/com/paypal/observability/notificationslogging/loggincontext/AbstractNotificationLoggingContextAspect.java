package com.paypal.observability.notificationslogging.loggincontext;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.observability.loggingcontext.service.LoggingContextService;
import com.paypal.observability.notificationslogging.model.NotificationLoggingTransaction;
import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.InvocationTargetException;

public abstract class AbstractNotificationLoggingContextAspect {

	private final LoggingContextService loggingContextService;

	protected AbstractNotificationLoggingContextAspect(final LoggingContextService loggingContextService) {
		this.loggingContextService = loggingContextService;
	}

	protected void doInterceptNotificationMethod(final ProceedingJoinPoint pjp) throws Throwable {
		final HyperwalletWebhookNotification notification = getNotificationObject(pjp);
		if (notification != null) {
			addContextualLoggingToNotification(pjp, notification);
		}
		else {
			pjp.proceed();
		}
	}

	protected void addContextualLoggingToNotification(final ProceedingJoinPoint pjp,
			final HyperwalletWebhookNotification hyperwalletWebhookNotification) throws Throwable {
		final NotificationLoggingTransaction notificationLoggingTransaction = getNotificationsLoggingTransaction(
				hyperwalletWebhookNotification);
		loggingContextService.executeInLoggingContext(pjp::proceed, notificationLoggingTransaction);
	}

	protected NotificationLoggingTransaction getNotificationsLoggingTransaction(
			final HyperwalletWebhookNotification notification) {
		final NotificationLoggingTransaction notificationLoggingTransaction = new NotificationLoggingTransaction(
				notification.getToken(), notification.getType());

		notificationLoggingTransaction.setTargetToken(getPropertyIfExists(notification.getObject(), "token"));
		notificationLoggingTransaction.setMiraklShopId(getPropertyIfExists(notification.getObject(), "clientUserId"));
		notificationLoggingTransaction
				.setClientPaymentId(getPropertyIfExists(notification.getObject(), "clientPaymentId"));

		return notificationLoggingTransaction;
	}

	protected String getPropertyIfExists(final Object object, final String value) {
		try {
			return BeanUtils.getProperty(object, value);
		}
		catch (final IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return null;
		}
	}

	protected abstract HyperwalletWebhookNotification getNotificationObject(ProceedingJoinPoint pjp);

}
