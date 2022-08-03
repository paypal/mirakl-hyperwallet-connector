package com.paypal.observability.hyperwalletapichecks.services;

import com.hyperwallet.clientsdk.model.HyperwalletProgram;
import com.paypal.observability.hyperwalletapichecks.connectors.HyperwalletAPIHealthCheckConnector;
import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;
import com.paypal.observability.hyperwalletapichecks.services.converters.HyperwalletAPIHealthCheckConnectorConverter;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletHealthCheckServiceImpl implements HyperwalletHealthCheckService {

	private final HyperwalletAPIHealthCheckConnector hyperwalletAPIHealthCheckConnector;

	private final HyperwalletAPIHealthCheckConnectorConverter hyperwalletAPIHealthCheckConnectorConverter;

	public HyperwalletHealthCheckServiceImpl(
			final HyperwalletAPIHealthCheckConnector hyperwalletAPIHealthCheckConnector,
			final HyperwalletAPIHealthCheckConnectorConverter hyperwalletAPIHealthCheckConnectorConverter) {
		this.hyperwalletAPIHealthCheckConnector = hyperwalletAPIHealthCheckConnector;
		this.hyperwalletAPIHealthCheckConnectorConverter = hyperwalletAPIHealthCheckConnectorConverter;
	}

	@Override
	public HyperwalletAPICheck check() {
		try {
			final HyperwalletProgram hyperwalletProgram = hyperwalletAPIHealthCheckConnector.getProgram();
			return hyperwalletAPIHealthCheckConnectorConverter.from(hyperwalletProgram);
		}
		catch (final Exception e) {
			return hyperwalletAPIHealthCheckConnectorConverter.from(e);
		}
	}

}
