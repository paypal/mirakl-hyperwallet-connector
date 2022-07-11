package com.paypal.observability.miraklapichecks.startup;

import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import com.paypal.observability.miraklapichecks.services.MiraklHealthCheckService;
import com.paypal.observability.miraklapichecks.startup.converters.MiraklHealthStartupCheckConverter;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckProvider;
import org.springframework.stereotype.Component;

@Component
public class MiraklHealthCheckStartupProvider implements StartupCheckProvider {

	private final MiraklHealthCheckService miraklHealthCheckService;

	private final MiraklHealthStartupCheckConverter miraklHealthCheckConverter;

	public MiraklHealthCheckStartupProvider(final MiraklHealthCheckService miraklHealthCheckService,
			final MiraklHealthStartupCheckConverter miraklHealthCheckConverter) {
		this.miraklHealthCheckService = miraklHealthCheckService;
		this.miraklHealthCheckConverter = miraklHealthCheckConverter;
	}

	@Override
	public StartupCheck check() {
		final MiraklAPICheck miraklAPICheck = miraklHealthCheckService.check();

		return miraklHealthCheckConverter.from(miraklAPICheck);
	}

	@Override
	public String getName() {
		return "miraklHealthCheck";
	}

}
