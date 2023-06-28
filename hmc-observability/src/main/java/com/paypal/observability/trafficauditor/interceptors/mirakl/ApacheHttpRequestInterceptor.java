package com.paypal.observability.trafficauditor.interceptors.mirakl;

import com.paypal.observability.trafficauditor.adapters.mirakl.MiraklTrafficAuditorAdapter;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class ApacheHttpRequestInterceptor implements HttpRequestInterceptor, InitializingBean {

	private static ApacheHttpRequestInterceptor instance;

	private final MiraklTrafficAuditorAdapter miraklTrafficAuditorAdapter;

	public ApacheHttpRequestInterceptor(final MiraklTrafficAuditorAdapter miraklTrafficAuditorAdapter) {
		this.miraklTrafficAuditorAdapter = miraklTrafficAuditorAdapter;
	}

	@Override
	public void process(final HttpRequest request, final HttpContext context) {
		miraklTrafficAuditorAdapter.startTraceCapture(request);
	}

	@SuppressWarnings("java:S2696")
	@Override
	public void afterPropertiesSet() {
		instance = this;
	}

	public static ApacheHttpRequestInterceptor get() {
		return instance;
	}

}
