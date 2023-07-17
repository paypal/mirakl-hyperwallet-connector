package com.paypal.kyc.statussynchronization.model;

import com.paypal.kyc.incomingnotifications.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.incomingnotifications.model.KYCUserStatusNotificationBodyModel;
import lombok.Value;

@Value
public class KYCUserStatusInfoModel {

	private KYCUserStatusNotificationBodyModel kycUserStatusNotificationBodyModel;

	private KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel;

}
