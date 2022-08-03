package com.paypal.observability.hyperwalletapichecks.startup;

import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;
import com.paypal.observability.hyperwalletapichecks.services.HyperwalletHealthCheckService;
import com.paypal.observability.hyperwalletapichecks.startup.converters.HyperwalletHealthStartupCheckConverter;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckProvider;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletHealthCheckStartupProvider implements StartupCheckProvider {

	private final HyperwalletHealthCheckService hyperwalletHealthCheckService;

	private final HyperwalletHealthStartupCheckConverter miraklHealthCheckConverter;

	public HyperwalletHealthCheckStartupProvider(final HyperwalletHealthCheckService hyperwalletHealthCheckService,
			final HyperwalletHealthStartupCheckConverter miraklHealthCheckConverter) {
		this.hyperwalletHealthCheckService = hyperwalletHealthCheckService;
		this.miraklHealthCheckConverter = miraklHealthCheckConverter;
	}

	@Override
	public StartupCheck check() {
		final HyperwalletAPICheck hyperwalletAPICheck = hyperwalletHealthCheckService.check();

		return miraklHealthCheckConverter.from(hyperwalletAPICheck);
	}

	@Override
	public String getName() {
		return "hyperwalletHealthCheck";
	}

}
