package com.paypal.kyc.service;

import com.hyperwallet.clientsdk.Hyperwallet;

/**
 * Service that provides the correct Hyperwallet instance according to Hyperwallet
 * hierarchy and based on the parameter received
 */
public interface HyperwalletSDKService {

	/**
	 * Return Hyperwallet instance based on the hyperwallet program received as parameter
	 * @param hyperwalletProgram hyperwallet program
	 * @return {@link Hyperwallet} object
	 */
	Hyperwallet getHyperwalletInstance(String hyperwalletProgram);

}
