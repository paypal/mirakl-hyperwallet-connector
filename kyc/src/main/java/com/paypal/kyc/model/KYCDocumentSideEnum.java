package com.paypal.kyc.model;

/**
 * Enum that defines possible side for documents
 */
public enum KYCDocumentSideEnum {

	FRONT, BACK;

	public static KYCDocumentSideEnum getDocumentSideByFieldName(final String fieldName) {
		if (fieldName.toLowerCase().endsWith("back")) {
			return BACK;
		}
		else {
			return FRONT;
		}
	}

}
