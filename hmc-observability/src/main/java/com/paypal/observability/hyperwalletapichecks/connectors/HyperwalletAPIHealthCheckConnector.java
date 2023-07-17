package com.paypal.observability.hyperwalletapichecks.connectors;

import com.hyperwallet.clientsdk.model.HyperwalletProgram;

public interface HyperwalletAPIHealthCheckConnector {

	HyperwalletProgram getProgram();

}
