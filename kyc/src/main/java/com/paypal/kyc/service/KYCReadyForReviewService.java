package com.paypal.kyc.service;

import com.paypal.kyc.model.KYCDocumentInfoModel;

/**
 * Service in charge of sending a notification when business-related, proof of identity or
 * proof of business documents are updated and imported to Hyperwallet
 * <p>
 * This notification allows the user process validation to continue after all the
 * documents are sent
 */
public interface KYCReadyForReviewService {

	void notifyReadyForReview(KYCDocumentInfoModel kycDocumentInfoModel);

}
