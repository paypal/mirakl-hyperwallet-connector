package com.paypal.observability.trafficauditor.interceptors;

import com.paypal.infrastructure.mirakl.client.DirectMiraklClient;
import com.paypal.infrastructure.mirakl.client.StageChangesMiraklClient;
import com.paypal.observability.trafficauditor.interceptors.mirakl.ApacheHttpRequestInterceptor;
import com.paypal.observability.trafficauditor.interceptors.mirakl.ApacheHttpResponseInterceptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class TrafficAuditorInterceptorsInitializer implements InitializingBean {

	private final DirectMiraklClient directMiraklClient;

	private final StageChangesMiraklClient stageChangesMiraklClient;

	// Field not used but required to ensure that mirakl SDK client configuration
	// is reloaded after the interceptor is initialized
	@SuppressWarnings("java:S1068")
	private final ApacheHttpRequestInterceptor apacheHttpRequestInterceptor;

	@SuppressWarnings("java:S1068")
	private final ApacheHttpResponseInterceptor apacheHttpResponseInterceptor;

	public TrafficAuditorInterceptorsInitializer(final DirectMiraklClient directMiraklClient,
			final StageChangesMiraklClient stageChangesMiraklClient,
			final ApacheHttpRequestInterceptor apacheHttpRequestInterceptor,
			final ApacheHttpResponseInterceptor apacheHttpResponseInterceptor) {
		this.directMiraklClient = directMiraklClient;
		this.stageChangesMiraklClient = stageChangesMiraklClient;
		this.apacheHttpRequestInterceptor = apacheHttpRequestInterceptor;
		this.apacheHttpResponseInterceptor = apacheHttpResponseInterceptor;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		directMiraklClient.reloadHttpConfiguration();
		stageChangesMiraklClient.reloadHttpConfiguration();
	}

}
