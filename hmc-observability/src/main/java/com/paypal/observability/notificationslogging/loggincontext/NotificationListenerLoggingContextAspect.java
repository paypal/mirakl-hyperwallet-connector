package com.paypal.observability.notificationslogging.loggincontext;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.observability.loggingcontext.service.LoggingContextService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NotificationListenerLoggingContextAspect extends AbstractNotificationLoggingContextAspect {

	public NotificationListenerLoggingContextAspect(final LoggingContextService loggingContextService) {
		super(loggingContextService);
	}

	@Around("execution(* com.paypal.notifications.incoming.handlers.NotificationHandler+.process(..))")
	public void interceptNotificationMethod(final ProceedingJoinPoint pjp) throws Throwable {
		doInterceptNotificationMethod(pjp);
	}

	@Override
	protected HyperwalletWebhookNotification getNotificationObject(final ProceedingJoinPoint pjp) {
		final Object[] args = pjp.getArgs();
		if (args.length > 1) {
			final Object arg = args[1];
			if (arg instanceof HyperwalletWebhookNotification notification) {
				return notification;
			}
		}
		return null;
	}

}
