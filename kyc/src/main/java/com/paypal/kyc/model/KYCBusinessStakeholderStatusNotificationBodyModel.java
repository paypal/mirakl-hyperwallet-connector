package com.paypal.kyc.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class KYCBusinessStakeholderStatusNotificationBodyModel extends KYCNotificationBodyModel {

	protected final String token;

	protected final String userToken;

	protected final Boolean isBusinessContact;

	protected final Boolean isDirector;

}
