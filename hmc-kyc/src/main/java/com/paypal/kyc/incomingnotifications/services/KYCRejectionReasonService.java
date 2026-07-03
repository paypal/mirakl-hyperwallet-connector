package com.paypal.kyc.incomingnotifications.services;

import com.paypal.kyc.incomingnotifications.model.KYCRejectionReasonTypeEnum;

import java.util.List;

/**
 * Service that manages KYC Reasons
 */
public interface KYCRejectionReasonService {

	/**
	 * Receives a notification object based on a {@link java.util.Map<String,String>} and
	 * returns verificationStatus, businessStakeholderVerificationStatus,
	 * letterOfAuthorizationStatus {@link KYCRejectionReasonTypeEnum} types
	 * @param notificationObject - the object that contains the notification
	 * @return {@link List<KYCRejectionReasonTypeEnum>}
	 */
	List<KYCRejectionReasonTypeEnum> getReasonTypes(final Object notificationObject);

	/**
	 * Generates a {@link String} with the error message to send to other systems.
	 * @param reasonsType {@link List<KYCRejectionReasonTypeEnum>} with all rejection
	 * reasons.
	 * @return {@link String} Error descriptions with all rejection reasons received
	 */

	String getRejectionReasonDescriptions(List<KYCRejectionReasonTypeEnum> reasonsType);

}
