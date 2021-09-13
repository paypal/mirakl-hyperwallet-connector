package com.paypal.kyc.model;

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

}
