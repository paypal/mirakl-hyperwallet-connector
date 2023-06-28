package com.paypal.observability.notificationslogging.loggincontext;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.observability.loggingcontext.service.LoggingContextService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NotificationServiceLoggingContextAspect extends AbstractNotificationLoggingContextAspect {

	public NotificationServiceLoggingContextAspect(final LoggingContextService loggingContextService) {
		super(loggingContextService);
	}

	@Around("execution(* com.paypal.notifications.incoming.services.NotificationProcessingServiceImpl.processNotification(..))")
	public void interceptNotificationMethod(final ProceedingJoinPoint pjp) throws Throwable {
		doInterceptNotificationMethod(pjp);
	}

	@Override
	protected HyperwalletWebhookNotification getNotificationObject(final ProceedingJoinPoint pjp) {
		final Object[] args = pjp.getArgs();
		if (args.length > 0) {
			final Object arg = args[0];
			if (arg instanceof HyperwalletWebhookNotification) {
				return (HyperwalletWebhookNotification) arg;
			}
		}
		return null;
	}

}
