package com.paypal.kyc.model;

import java.util.List;

/**
 * Enum that defines proof of identity types for seller
 */

public enum KYCProofOfAddressEnum {

	PASSPORT, BANK_STATEMENT, CREDIT_CARD_STATEMENT, OFFICIAL_GOVERNMENT_LETTER, PROPERTY_TAX_ASSESSMENT, TAX_RETURN, UTILITY_BILL;

	public static List<String> getMiraklFields() {
		return List.of(KYCConstants.HwDocuments.PROOF_OF_ADDRESS);
	}

}
