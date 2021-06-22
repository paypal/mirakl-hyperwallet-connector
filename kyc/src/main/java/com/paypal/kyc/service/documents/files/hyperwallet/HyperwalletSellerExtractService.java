package com.paypal.kyc.service.documents.files.hyperwallet;

import com.paypal.kyc.model.KYCDocumentSellerInfoModel;

import java.util.List;

/**
 * Interface that manages logic of pushing documents to Hyperwallet at seller level
 */
public interface HyperwalletSellerExtractService {

	/**
	 * Pushes proof of identity/business documents into HW and returns the list of
	 * KYCDocumentInfoModel that were correctly uploaded to HW
	 * @param kycDocumentSellerInfoModelList
	 * @return the correct list of {@link KYCDocumentSellerInfoModel} pushed to
	 * hyperwallet
	 */
	List<KYCDocumentSellerInfoModel> pushProofOfIdentityAndBusinessSellerDocuments(
			List<KYCDocumentSellerInfoModel> kycDocumentSellerInfoModelList);

}
