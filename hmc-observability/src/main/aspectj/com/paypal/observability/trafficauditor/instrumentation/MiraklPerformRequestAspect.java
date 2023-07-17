package com.paypal.observability.trafficauditor.instrumentation;

import com.paypal.observability.trafficauditor.adapters.mirakl.MiraklTrafficAuditorAdapter;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@Aspect
public class MiraklPerformRequestAspect {

	private static final Logger logger = getLogger(MiraklPerformRequestAspect.class);

	@SuppressWarnings("java:S3252")
	@AfterThrowing(pointcut = "execution(* com.mirakl.client.core.AbstractMiraklApiClient.performRequest(..))",
			throwing = "thrown", argNames = "thrown")
	public void exit(final Throwable thrown) {
		try {
			MiraklTrafficAuditorAdapter.get().endTraceCapture(thrown);
		}
		catch (final Exception e) {
			logger.trace("Error while intercepting Mirakl HTTP traffic", e);
		}
	}

}
