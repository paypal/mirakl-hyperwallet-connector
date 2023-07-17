package com.paypal.kyc.sellersdocumentextraction.model;

import com.paypal.kyc.documentextractioncommons.model.KYCConstants;

import java.util.List;
import java.util.Objects;

/**
 * Enum that defines proof of identity types for seller
 */

public enum KYCProofOfAddressEnum {

	PASSPORT, BANK_STATEMENT, CREDIT_CARD_STATEMENT, OFFICIAL_GOVERNMENT_LETTER, PROPERTY_TAX_ASSESSMENT, TAX_RETURN, UTILITY_BILL;

	public static List<String> getMiraklFields() {
		return List.of(KYCConstants.HwDocuments.PROOF_OF_ADDRESS);
	}

	public static List<String> getMiraklFields(final KYCProofOfAddressEnum kycProofOfAddressEnum,
			final int businessStakeholderMiraklNumber) {
		if (Objects.isNull(kycProofOfAddressEnum)) {
			return List.of();
		}

		return List.of(KYCConstants.HYPERWALLET_PREFIX + KYCConstants.BUSINESS_STAKEHOLDER_PREFIX
				+ businessStakeholderMiraklNumber + "-" + KYCConstants.PROOF_ADDRESS_SUFFIX);
	}

}
