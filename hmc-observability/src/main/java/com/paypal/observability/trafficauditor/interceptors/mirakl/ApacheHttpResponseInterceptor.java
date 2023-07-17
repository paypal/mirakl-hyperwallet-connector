package com.paypal.observability.trafficauditor.interceptors.mirakl;

import com.paypal.observability.trafficauditor.adapters.mirakl.MiraklTrafficAuditorAdapter;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class ApacheHttpResponseInterceptor implements HttpResponseInterceptor, InitializingBean {

	private static ApacheHttpResponseInterceptor instance;

	private final MiraklTrafficAuditorAdapter miraklTrafficAuditorAdapter;

	public ApacheHttpResponseInterceptor(final MiraklTrafficAuditorAdapter miraklTrafficAuditorAdapter) {
		this.miraklTrafficAuditorAdapter = miraklTrafficAuditorAdapter;
	}

	@Override
	public void process(final HttpResponse response, final HttpContext context) {
		miraklTrafficAuditorAdapter.endTraceCapture(response);
	}

	@SuppressWarnings("java:S2696")
	@Override
	public void afterPropertiesSet() {
		instance = this;
	}

	public static ApacheHttpResponseInterceptor get() {
		return instance;
	}

}
