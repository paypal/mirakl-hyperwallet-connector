package com.paypal.kyc.model;

import java.util.List;

/**
 * Contains logic that returns how many documents are needed when proof of identity type
 * is received
 */

public interface KYCMiraklFields {

	/**
	 * Obtains a {@link List<String>} of custom parameters defined ini Mirakl for document
	 * based on the proof of identity received
	 * @param prefixFieldName prefix field defined as custom parameter in Mirakl
	 * @param proofIdentityType proof of identity type received
	 * @return {@link List<String>} custom parameters defined in Mirakl for documents
	 */

	static List<String> populateMiraklFields(String prefixFieldName, String proofIdentityType) {
		return switch (proofIdentityType) {
		case "GOVERNMENT_ID", "DRIVERS_LICENSE" -> List.of(prefixFieldName + KYCConstants.PROOF_IDENTITY_SIDE_FRONT,
				prefixFieldName + KYCConstants.PROOF_IDENTITY_SIDE_BACK);
		case "PASSPORT" -> List.of(prefixFieldName + KYCConstants.PROOF_IDENTITY_SIDE_FRONT);
		default -> List.of();
		};

	}

}
