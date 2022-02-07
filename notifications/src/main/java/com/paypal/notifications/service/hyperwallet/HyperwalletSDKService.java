package com.paypal.notifications.service.hyperwallet;

import com.hyperwallet.clientsdk.Hyperwallet;

/**
 * Service that provides the correct Hyperwallet instance according to Hyperwallet
 * hierarchy and based on the parameter received
 */
public interface HyperwalletSDKService {

	/**
	 * Return Hyperwallet instance based on the Hyperwallet program received as parameter
	 * @param hyperwalletProgram Hyperwallet program
	 * @return {@link Hyperwallet} object
	 */
	Hyperwallet getHyperwalletInstance(String hyperwalletProgram);

}
