package com.paypal.infrastructure.hyperwallet.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletProgram;

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
	Hyperwallet getHyperwalletInstanceByHyperwalletProgram(String hyperwalletProgram);

	/**
	 * Return Hyperwallet instance based on programToken received as parameter
	 * @param programToken program token
	 * @return {@link Hyperwallet} object
	 */
	Hyperwallet getHyperwalletInstanceByProgramToken(String programToken);

	/**
	 * Return Hyperwallet instance without any associated program token
	 * @return {@link Hyperwallet} object
	 */
	Hyperwallet getHyperwalletInstance();

	HyperwalletProgram getRootProgram();

	/**
	 * Returns a {@link String} program token based on its hyperwallet program associated
	 * @param hyperwalletProgram hyperwallet program
	 * @return {@link String}
	 */
	String getProgramTokenByHyperwalletProgram(String hyperwalletProgram);

}
