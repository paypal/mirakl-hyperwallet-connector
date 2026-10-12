package com.paypal.kyc.incomingnotifications.model;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class KYCUserStatusNotificationBodyModel extends KYCNotificationBodyModel {

	protected final HyperwalletUser.LetterOfAuthorizationStatus letterOfAuthorizationStatus;

	protected final transient List<KYCRejectionReasonTypeEnum> reasonsType;

	protected final transient List<KYCDocumentNotificationModel> documents;

	protected final String businessName;

	protected final String businessOperatingName;

	protected final String businessRegistrationId;

	protected final String firstName;

	protected final String lastName;

	protected final String email;

	protected final String phoneNumber;

	protected final String mobileNumber;

	protected final String addressLine1;

	protected final String addressLine2;

	protected final String city;

	protected final String stateProvince;

	protected final String country;

	protected final String postalCode;

}
