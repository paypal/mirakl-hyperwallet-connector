package com.paypal.observability.trafficauditor.instrumentation;

import com.paypal.observability.trafficauditor.interceptors.mirakl.ApacheHttpRequestInterceptor;
import com.paypal.observability.trafficauditor.interceptors.mirakl.ApacheHttpResponseInterceptor;
import org.apache.http.impl.client.HttpClientBuilder;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@Aspect
public class MiraklHttpClientConfigurationAspect {

	private static final Logger logger = getLogger(MiraklHttpClientConfigurationAspect.class);
	@AfterReturning(
			pointcut = "execution(* com.mirakl.client.core.AbstractMiraklApiClient.setDefaultConfiguration(..)) && args(httpClientBuilder,..)",
			argNames = "httpClientBuilder")
	public void exit(final HttpClientBuilder httpClientBuilder) {
		try {
			httpClientBuilder.addInterceptorLast(ApacheHttpRequestInterceptor.get())
					.addInterceptorLast(ApacheHttpResponseInterceptor.get());
		}
		catch (final Exception e) {
			logger.trace("Error while intercepting Mirakl SDK instantiation", e);
		}
	}

}
