package com.paypal.kyc.service;

import java.util.Date;

/**
 * Manages document extract logic for KYC verification in HW
 */
public interface DocumentsExtractService {

	/**
	 * Manages input-output logic from Mirakl to Hyperwallet for KYC proof of
	 * identity/business verification for sellers
	 * @param delta
	 */
	void extractProofOfIdentityAndBusinessSellerDocuments(Date delta);

	/**
	 * Manages input-output logic from Mirakl to Hyperwallet for KYC verification for
	 * sellers
	 * @param delta
	 */
	void extractBusinessStakeholderDocuments(Date delta);

}
