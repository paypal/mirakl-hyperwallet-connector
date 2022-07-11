package com.paypal.observability.miraklapichecks.startup.converters;

import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheckStatus;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MiraklHealthStartupCheckConverterImpl implements MiraklHealthStartupCheckConverter {

	@Override
	public StartupCheck from(final MiraklAPICheck miraklAPICheck) {
		//@formatter:off
		return StartupCheck.builder()
				.status(isHealthy(miraklAPICheck) ?	StartupCheckStatus.READY : StartupCheckStatus.READY_WITH_WARNINGS)
				.statusMessage(isHealthy(miraklAPICheck) ? Optional.of("Mirakl API is accessible") : Optional.of("Mirakl API is not accessible"))
				.detail("status", isHealthy(miraklAPICheck) ? "UP" : "DOWN")
				.detail("location", miraklAPICheck.getLocation())
				.detail("version", getVersion(miraklAPICheck))
				.detail("error", miraklAPICheck.getError())
				.build();
		//@formatter:on
	}

	private String getVersion(MiraklAPICheck miraklAPICheck) {
		return isHealthy(miraklAPICheck) ? miraklAPICheck.getVersion() : null;
	}

	private boolean isHealthy(MiraklAPICheck miraklAPICheck) {
		return miraklAPICheck.getMiraklAPICheckStatus().equals(MiraklAPICheckStatus.UP);
	}

}
