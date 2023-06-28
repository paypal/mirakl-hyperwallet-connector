package com.paypal.observability.hyperwalletapichecks.startup.converters;

import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;
import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheckStatus;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HyperwalletHealthStartupCheckConverterImpl implements HyperwalletHealthStartupCheckConverter {

	@Override
	public StartupCheck from(final HyperwalletAPICheck hyperwalletAPICheck) {
		//@formatter:off
		return StartupCheck.builder()
				.status(isHealthy(hyperwalletAPICheck) ?	StartupCheckStatus.READY : StartupCheckStatus.READY_WITH_WARNINGS)
				.statusMessage(isHealthy(hyperwalletAPICheck) ? Optional.of("Hyperwallet API is accessible") : Optional.of("Hyperwallet API is not accessible"))
				.detail("status", isHealthy(hyperwalletAPICheck) ? "UP" : "DOWN")
				.detail("location", hyperwalletAPICheck.getLocation())
				.detail("error", hyperwalletAPICheck.getError())
				.build();
		//@formatter:on
	}

	private boolean isHealthy(final HyperwalletAPICheck hyperwalletAPICheck) {
		return hyperwalletAPICheck.getHyperwalletAPICheckStatus().equals(HyperwalletAPICheckStatus.UP);
	}

}
