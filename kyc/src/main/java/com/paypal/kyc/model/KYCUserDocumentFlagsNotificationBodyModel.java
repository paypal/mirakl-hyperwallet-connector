package com.paypal.kyc.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Model that manages notifications used to flag a seller as KYC-verification needed
 */
@Getter
@SuperBuilder
public class KYCUserDocumentFlagsNotificationBodyModel extends KYCNotificationBodyModel {

	private final String userToken;

	private final String hyperwalletProgram;

}
