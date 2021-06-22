package com.paypal.kyc.model;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.model.NotificationBodyModel;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Model for KYC Notification objects received
 */

@Getter
@SuperBuilder
public class KYCNotificationBodyModel implements NotificationBodyModel {

	protected final HyperwalletUser.VerificationStatus verificationStatus;

	protected final HyperwalletUser.BusinessStakeholderVerificationStatus businessStakeholderVerificationStatus;

	protected final String clientUserId;

	protected final HyperwalletUser.ProfileType profileType;

}
