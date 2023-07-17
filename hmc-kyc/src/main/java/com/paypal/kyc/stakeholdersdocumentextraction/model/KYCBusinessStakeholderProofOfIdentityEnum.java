package com.paypal.kyc.stakeholdersdocumentextraction.model;

import com.paypal.kyc.documentextractioncommons.model.KYCConstants;
import com.paypal.kyc.documentextractioncommons.model.KYCMiraklFields;

import java.util.List;

/**
 * Enum that defines each proof of identity type for business stakeholders
 */
public enum KYCBusinessStakeholderProofOfIdentityEnum {

	DRIVERS_LICENSE, GOVERNMENT_ID, PASSPORT;

	/**
	 * Obtains a {@link List<String>} of custom parameters defined ini Mirakl for document
	 * based on the proof of identity received
	 * @param kycBusinessStakeholderProofOfIdentityEnum proof of identity
	 * @param businessStakeholderNumber business stakeholder position in Mirakl seller
	 * @return {@link List<String>} containing all required document custom parameters
	 */
	public static List<String> getMiraklFields(
			final KYCBusinessStakeholderProofOfIdentityEnum kycBusinessStakeholderProofOfIdentityEnum,
			final int businessStakeholderNumber) {
		final String prefixFieldName = KYCConstants.HYPERWALLET_PREFIX + KYCConstants.STAKEHOLDER_PREFIX
				+ KYCConstants.REQUIRED_PROOF_IDENTITY + businessStakeholderNumber;

		return KYCMiraklFields.populateMiraklFields(prefixFieldName, kycBusinessStakeholderProofOfIdentityEnum.name());
	}

}
