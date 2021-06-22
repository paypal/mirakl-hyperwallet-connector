package com.paypal.kyc.model;

import java.util.List;
import java.util.Objects;

/**
 * Enum that defines proof of identity types
 */
public enum KYCProofOfIdentityEnum implements KYCMiraklFields {

	DRIVERS_LICENSE, GOVERNMENT_ID, PASSPORT;

	public static List<String> getMiraklFields(KYCProofOfIdentityEnum kycProofOfIdentityEnum) {
		if (Objects.isNull(kycProofOfIdentityEnum)) {
			return List.of();
		}

		final String prefixFieldName = KYCConstants.HYPERWALLET_PREFIX + KYCConstants.INDIVIDUAL_PREFIX
				+ KYCConstants.PROOF_IDENTITY_PREFIX;

		return KYCMiraklFields.populateMiraklFields(prefixFieldName, kycProofOfIdentityEnum.name());
	}

	public static List<String> getMiraklFields(final KYCProofOfIdentityEnum kycProofOfIdentityEnum,
			final int businessStakeholderMiraklNumber) {
		if (Objects.isNull(kycProofOfIdentityEnum)) {
			return List.of();
		}

		final String prefixFieldName = KYCConstants.HYPERWALLET_PREFIX + KYCConstants.BUSINESS_STAKEHOLDER_PREFIX
				+ businessStakeholderMiraklNumber + "-" + KYCConstants.PROOF_IDENTITY_PREFIX;

		return KYCMiraklFields.populateMiraklFields(prefixFieldName, kycProofOfIdentityEnum.name());
	}

}
