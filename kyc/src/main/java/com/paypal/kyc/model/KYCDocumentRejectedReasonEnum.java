package com.paypal.kyc.model;

public enum KYCDocumentRejectedReasonEnum {

	DOCUMENT_EXPIRED("Document is expired or not issued within specified timeframe."), DOCUMENT_NOT_RELATED_TO_PROFILE(
			"Document does not match account information."), DOCUMENT_NOT_READABLE(
					"Document is not readable."), DOCUMENT_NOT_DECISIVE(
							"Decision cannot be made based on document. Alternative document required."), DOCUMENT_NOT_COMPLETE(
									"Document is incomplete."), DOCUMENT_CORRECTION_REQUIRED(
											"Document requires correction."), DOCUMENT_NOT_VALID_WITH_NOTES(
													"Document is invalid and rejection details are noted on the account."), DOCUMENT_TYPE_NOT_VALID(
															"Document type does not apply.");

	private final String reason;

	KYCDocumentRejectedReasonEnum(final String reason) {
		this.reason = reason;
	}

}
