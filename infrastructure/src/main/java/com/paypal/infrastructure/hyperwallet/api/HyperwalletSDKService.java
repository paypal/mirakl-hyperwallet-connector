package com.paypal.infrastructure.hyperwallet.api;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletProgram;

public interface HyperwalletSDKService {

	Hyperwallet getHyperwalletInstance(String hyperwalletProgram);

	HyperwalletProgram getRootProgram();

}
