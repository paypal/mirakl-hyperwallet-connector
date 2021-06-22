package com.paypal.kyc.model;

/**
 * Enum that defines each document category
 */
public enum KYCDocumentCategoryEnum {

	IDENTIFICATION, BUSINESS, ADDRESS, AUTHORIZATION, UNKNOWN;

	public static KYCDocumentCategoryEnum getDocumentCategoryForField(final String fieldName) {
		switch (fieldName) {
		case KYCConstants.HwDocuments.PROOF_OF_ADDRESS:
			return ADDRESS;
		case KYCConstants.HwDocuments.PROOF_OF_BUSINESS:
			return BUSINESS;
		case KYCConstants.HwDocuments.PROOF_OF_AUTHORIZATION:
			return AUTHORIZATION;
		case KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK:
		case KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT:
		case KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT_BSH1:
		case KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT_BSH2:
		case KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT_BSH3:
		case KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT_BSH4:
		case KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT_BSH5:
		case KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK_BSH1:
		case KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK_BSH2:
		case KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK_BSH3:
		case KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK_BSH4:
		case KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK_BSH5:
			return IDENTIFICATION;
		default:
			return UNKNOWN;
		}
	}

}
