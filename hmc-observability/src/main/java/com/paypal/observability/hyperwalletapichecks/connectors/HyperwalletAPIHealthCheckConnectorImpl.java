package com.paypal.observability.hyperwalletapichecks.connectors;

import com.hyperwallet.clientsdk.model.HyperwalletProgram;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletAPIHealthCheckConnectorImpl implements HyperwalletAPIHealthCheckConnector {

	private final UserHyperwalletSDKService userHyperwalletSDKService;

	public HyperwalletAPIHealthCheckConnectorImpl(final UserHyperwalletSDKService userHyperwalletSDKService) {
		this.userHyperwalletSDKService = userHyperwalletSDKService;
	}

	@Override
	public HyperwalletProgram getProgram() {
		return userHyperwalletSDKService.getRootProgram();
	}

}
