package com.paypal.kyc.documentextractioncommons.model;

import java.util.List;

/**
 * Defines constants needed and defined base on custom parameters in Mirakl
 */
public final class KYCConstants {

	private KYCConstants() {
	}

	public static final String HYPERWALLET_PREFIX = "hw-";

	public static final String INDIVIDUAL_PREFIX = "ind-";

	public static final String BUSINESS_STAKEHOLDER_PREFIX = "bsh";

	public static final String STAKEHOLDER_PREFIX = "stakeholder-";

	public static final String STAKEHOLDER_COUNTRY_PREFIX = "ctry-";

	public static final String STAKEHOLDER_TOKEN_PREFIX = "token-";

	public static final String REQUIRED_PROOF_IDENTITY = "req-proof-identity-";

	public static final String PROOF_IDENTITY_PREFIX = "proof-identity-";

	public static final String PROOF_ADDRESS_SUFFIX = "proof-address";

	public static final String PROOF_AUTHORIZATION_PREFIX = "proof-authorization-";

	public static final String CONTACT = "business-contact-";

	public static final String STAKEHOLDER_PROOF_IDENTITY = "proof-identity-type-";

	public static final String PROOF_IDENTITY_SIDE_FRONT = "front";

	public static final String PROOF_IDENTITY_SIDE_BACK = "back";

	public static final String HW_PROGRAM = "hw-program";

	public static final String HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD = "hw-kyc-req-proof-identity-business";

	public static final String HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD = "hw-kyc-req-proof-authorization";

	public static final String HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_FIELD = "hw-ind-proof-identity-type";

	public static final String HYPERWALLET_KYC_PROF_PROOF_OF_BUSINESS_FIELD = "hw-prof-proof-business-type";

	public static final String HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_COUNTRY_ISOCODE = "hw-ind-proof-identity-country";

	public static final String HYPERWALLET_KYC_IND_PROOF_OF_ADDRESS_FIELD = "hw-ind-proof-address-type";

	public static final String HYPERWALLET_USER_TOKEN_FIELD = "hw-user-token";

	public static final class HwDocuments {

		private HwDocuments() {
		}

		public static final String PROOF_OF_IDENTITY_FRONT = "hw-ind-proof-identity-front";

		public static final String PROOF_OF_IDENTITY_BACK = "hw-ind-proof-identity-back";

		public static final String PROOF_OF_BUSINESS = "hw-prof-proof-business-front";

		public static final String PROOF_OF_ADDRESS = "hw-ind-proof-address";

		public static final String PROOF_OF_IDENTITY_FRONT_BSH1 = "hw-bsh1-proof-identity-front";

		public static final String PROOF_OF_IDENTITY_FRONT_BSH2 = "hw-bsh2-proof-identity-front";

		public static final String PROOF_OF_IDENTITY_FRONT_BSH3 = "hw-bsh3-proof-identity-front";

		public static final String PROOF_OF_IDENTITY_FRONT_BSH4 = "hw-bsh4-proof-identity-front";

		public static final String PROOF_OF_IDENTITY_FRONT_BSH5 = "hw-bsh5-proof-identity-front";

		public static final String PROOF_OF_IDENTITY_BACK_BSH1 = "hw-bsh1-proof-identity-back";

		public static final String PROOF_OF_IDENTITY_BACK_BSH2 = "hw-bsh2-proof-identity-back";

		public static final String PROOF_OF_IDENTITY_BACK_BSH3 = "hw-bsh3-proof-identity-back";

		public static final String PROOF_OF_IDENTITY_BACK_BSH4 = "hw-bsh4-proof-identity-back";

		public static final String PROOF_OF_IDENTITY_BACK_BSH5 = "hw-bsh5-proof-identity-back";

		public static final String PROOF_OF_AUTHORIZATION = "hw-bsh-letter-authorization";

		public static List<String> getAllDocumentsTypes() {
			return List.of(PROOF_OF_IDENTITY_FRONT, PROOF_OF_IDENTITY_BACK, PROOF_OF_ADDRESS);
		}

	}

	public static final class HwWebhookNotificationType {

		private HwWebhookNotificationType() {

		}

		public static final String USERS_BUSINESS_STAKEHOLDERS_CREATED = "USERS.BUSINESS_STAKEHOLDERS.CREATED";

		public static final String USERS_BUSINESS_STAKEHOLDERS_VERIFICATION_STATUS = "USERS.BUSINESS_STAKEHOLDERS.UPDATED.VERIFICATION_STATUS";

	}

}
