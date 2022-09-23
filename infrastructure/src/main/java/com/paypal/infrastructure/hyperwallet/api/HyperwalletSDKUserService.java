package com.paypal.infrastructure.hyperwallet.api;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletProgram;

/**
 * Service that provides the correct Hyperwallet instance according to Hyperwallet
 * hierarchy and based on the parameter received
 */
public interface HyperwalletSDKUserService {

	/**
	 * Return Hyperwallet instance based on the hyperwallet program received as parameter
	 * @param hyperwalletProgram hyperwallet program
	 * @return {@link Hyperwallet} object
	 */
	Hyperwallet getHyperwalletInstanceByHyperwalletProgram(String hyperwalletProgram);

	/**
	 * Return Hyperwallet instance based on a program token received as parameter
	 * @param programToken program token
	 * @return {@link Hyperwallet} object
	 */
	Hyperwallet getHyperwalletInstanceByProgramToken(String programToken);

	HyperwalletProgram getRootProgram();

}
