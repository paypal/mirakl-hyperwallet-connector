package com.paypal.observability.hyperwalletapichecks.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletProgram;
import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;

public interface HyperwalletAPIHealthCheckConnectorConverter {

	HyperwalletAPICheck from(HyperwalletProgram hyperwalletProgram);

	HyperwalletAPICheck from(Exception e);

}
