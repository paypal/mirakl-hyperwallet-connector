package com.paypal.observability.trafficauditor.instrumentation;

import cc.protea.util.http.Response;
import com.hyperwallet.clientsdk.util.Request;
import com.paypal.observability.trafficauditor.adapters.TrafficAuditorAdapter;
import com.paypal.observability.trafficauditor.adapters.hyperwallet.HyperwalletTrafficAuditorAdapter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@Aspect
public class HyperwalletRequestAspect {

	private static final Logger logger = getLogger(HyperwalletRequestAspect.class);

	@Around("execution(* com.hyperwallet.clientsdk.util.Request.getResource(..))")
	public Object auditGetResource(final ProceedingJoinPoint pjp) throws Throwable {
		return auditOperation(pjp);
	}

	@Around("execution(* com.hyperwallet.clientsdk.util.Request.postResource(..))")
	public Object auditPostResource(final ProceedingJoinPoint pjp) throws Throwable {
		return auditOperation(pjp);
	}

	@Around("execution(* com.hyperwallet.clientsdk.util.Request.putResource(..))")
	public Object auditPutResource(final ProceedingJoinPoint pjp) throws Throwable {
		return auditOperation(pjp);
	}

	@SuppressWarnings("java:S3252")
	private Response auditOperation(final ProceedingJoinPoint pjp) throws Throwable {
		final TrafficAuditorAdapter<Request, Response> trafficAuditorAdapter = HyperwalletTrafficAuditorAdapter.get();

		final Request self = (Request) pjp.getThis();
		try {
			final Response result = (Response) pjp.proceed();

			executeWithoutFailing(() -> trafficAuditorAdapter.startTraceCapture(self));
			executeWithoutFailing(() -> trafficAuditorAdapter.endTraceCapture(result));
			return result;
		}
		catch (final Throwable e) {
			executeWithoutFailing(() -> trafficAuditorAdapter.endTraceCapture(e));
			throw e;
		}
	}

	private void executeWithoutFailing(final Runnable runnable) {
		try {
			runnable.run();
		}
		catch (final Exception e) {
			logger.trace("Error while intercepting Hyperwallet traffic", e);
		}
	}

}
