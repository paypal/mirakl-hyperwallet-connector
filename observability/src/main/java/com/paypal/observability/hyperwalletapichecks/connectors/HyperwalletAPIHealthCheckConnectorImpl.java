package com.paypal.observability.hyperwalletapichecks.connectors;

import com.hyperwallet.clientsdk.model.HyperwalletProgram;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletAPIHealthCheckConnectorImpl implements HyperwalletAPIHealthCheckConnector {

	private final HyperwalletSDKUserService hyperwalletSDKUserService;

	public HyperwalletAPIHealthCheckConnectorImpl(final HyperwalletSDKUserService hyperwalletSDKUserService) {
		this.hyperwalletSDKUserService = hyperwalletSDKUserService;
	}

	@Override
	public HyperwalletProgram getProgram() {
		return hyperwalletSDKUserService.getRootProgram();
	}

}
