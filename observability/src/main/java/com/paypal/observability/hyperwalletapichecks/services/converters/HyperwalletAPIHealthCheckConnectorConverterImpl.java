package com.paypal.observability.hyperwalletapichecks.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletProgram;
import com.paypal.infrastructure.hyperwallet.api.UserHyperwalletApiConfig;
import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;
import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheckStatus;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletAPIHealthCheckConnectorConverterImpl implements HyperwalletAPIHealthCheckConnectorConverter {

	private final String hyperwalletEnvironment;

	public HyperwalletAPIHealthCheckConnectorConverterImpl(final UserHyperwalletApiConfig config) {
		this.hyperwalletEnvironment = config.getServer();
	}

	@Override
	public HyperwalletAPICheck from(final HyperwalletProgram hyperwalletProgram) {
		return HyperwalletAPICheck.builder()
				.hyperwalletAPICheckStatus(
						isHealthy(hyperwalletProgram) ? HyperwalletAPICheckStatus.UP : HyperwalletAPICheckStatus.DOWN)
				.location(hyperwalletEnvironment).error(getError(hyperwalletProgram)).build();
		//@formatter:on
	}

	@Override
	public HyperwalletAPICheck from(final Exception e) {
		//@formatter:off
		return HyperwalletAPICheck.builder()
				.hyperwalletAPICheckStatus(HyperwalletAPICheckStatus.DOWN)
				.location(hyperwalletEnvironment)
				.error(e.getMessage())
				.build();
		//@formatter:on
	}

	private boolean isHealthy(final HyperwalletProgram hyperwalletProgram) {
		return hyperwalletProgram != null && hyperwalletProgram.getName() != null;
	}

	String getError(final HyperwalletProgram hyperwalletProgram) {
		return hyperwalletProgram == null || hyperwalletProgram.getName() == null
				? "Hyperwallet Health Check end point didn't return program info" : null;
	}

}
