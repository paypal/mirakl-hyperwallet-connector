package com.paypal.kyc.model;

import lombok.Getter;

@Getter
/**
 * Controls different KYC rejection reasons types and their messages to send to Mirakl
 */
public enum KYCRejectionReasonTypeEnum {

	//@formatter:off
    VERIFICATIONSTATUS_IND_REQUIRED("<li>Filled all your account details correctly.</li><li>Submitted your Proof of Identity and Proof of Address documents.</li>"),
    VERIFICATIONSTATUS_PROF_REQUIRED("<li>Filled all your account details correctly.</li><li>Submitted Certificate of incorporation.</li>"),
    BUSINESS_STAKEHOLDER_REQUIRED("<li>Filled Business Stakeholder details.</li><li>Submitted their Proof of Identity documents.</li>"),
    LETTER_OF_AUTHORIZATION_REQUIRED("<li>Provided a Letter of Authorization document for the nominated Business Contact who is not a Director.</li>"),
    UNKWOWN("<li>Unknown issue.</li>");
	//@formatter:on

	private String reason;

	private static final String HEADER = "There is an issue with verifying your details in Hyperwallet. Please ensure that you: <br /><ul>";

	private static final String FOOTER = "</ul><br />For more information on document requirements please refer to the <a href=\"https://docs.hyperwallet.com/content/payee-requirements/v1/payee-verification/required-data\">Hyperwallet guidelines</a>.";

	private KYCRejectionReasonTypeEnum(final String reason) {
		this.reason = reason;
	}

	/**
	 * Gets the common error header for each message
	 * @return {@link String} with the error header
	 */
	public static String getReasonHeader() {
		return HEADER;
	}

	/**
	 * Gets the common error footer for each message
	 * @return {@link String} with the error footer
	 */
	public static String getReasonFooter() {
		return FOOTER;
	}

}
