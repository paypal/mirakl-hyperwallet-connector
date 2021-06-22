package com.paypal.invoices.invoicesextract.service.hyperwallet;

import com.hyperwallet.clientsdk.Hyperwallet;

/**
 * Service that provides the correct Hyperwallet instance according to Hyperwallet
 * hierarchy and based on the parameter received
 */
public interface HyperwalletSDKService {

	/**
	 * Return Hyperwallet instance based on programToken received as parameter
	 * @param programToken program token
	 * @return {@link Hyperwallet} object
	 */
	Hyperwallet getHyperwalletInstanceWithProgramToken(String programToken);

	/**
	 * Return Hyperwallet instance based on the hyperwallet program received as parameter
	 * @param hyperwalletProgram hyperwallet program
	 * @return {@link Hyperwallet} object
	 */
	Hyperwallet getHyperwalletInstanceByHyperwalletProgram(String hyperwalletProgram);

	/**
	 * Returns a {@link String} program token based on its hyperwallet program associated
	 * @param hyperwalletProgram hyperwallet program
	 * @return {@link String}
	 */
	String getProgramTokenByHyperwalletProgram(String hyperwalletProgram);

}
