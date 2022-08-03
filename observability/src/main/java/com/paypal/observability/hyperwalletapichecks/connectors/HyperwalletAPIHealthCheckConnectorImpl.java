package com.paypal.observability.hyperwalletapichecks.connectors;

import com.hyperwallet.clientsdk.model.HyperwalletProgram;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKService;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletAPIHealthCheckConnectorImpl implements HyperwalletAPIHealthCheckConnector {

	private final HyperwalletSDKService hyperwalletSDKService;

	public HyperwalletAPIHealthCheckConnectorImpl(final HyperwalletSDKService hyperwalletSDKService) {
		this.hyperwalletSDKService = hyperwalletSDKService;
	}

	@Override
	public HyperwalletProgram getProgram() {
		return hyperwalletSDKService.getRootProgram();
	}

}
