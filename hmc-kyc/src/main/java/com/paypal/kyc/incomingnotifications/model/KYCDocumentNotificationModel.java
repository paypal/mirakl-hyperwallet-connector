package com.paypal.kyc.incomingnotifications.model;

import com.paypal.kyc.documentextractioncommons.model.KYCDocumentCategoryEnum;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@SuperBuilder
public class KYCDocumentNotificationModel {

	private KYCDocumentCategoryEnum documentCategory;

	private KYCDocumentTypeEnum documentType;

	private KYCDocumentStatusEnum documentStatus;

	private List<KYCDocumentRejectedReasonEnum> documentRejectedReasons;

	private LocalDateTime createdOn;

}
