package com.paypal.kyc.model;

/**
 * Enum that defines each document category
 */
public enum KYCDocumentCategoryEnum {

	IDENTIFICATION, BUSINESS, ADDRESS, AUTHORIZATION, UNKNOWN;

	public static KYCDocumentCategoryEnum getDocumentCategoryForField(final String fieldName) {
		return switch (fieldName) {
		case KYCConstants.HwDocuments.PROOF_OF_ADDRESS -> ADDRESS;
		case KYCConstants.HwDocuments.PROOF_OF_BUSINESS -> BUSINESS;
		case KYCConstants.HwDocuments.PROOF_OF_AUTHORIZATION -> AUTHORIZATION;
		case KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK, KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT, KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT_BSH1, KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT_BSH2, KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT_BSH3, KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT_BSH4, KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT_BSH5, KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK_BSH1, KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK_BSH2, KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK_BSH3, KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK_BSH4, KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK_BSH5 -> IDENTIFICATION;
		default -> UNKNOWN;
		};
	}

}
