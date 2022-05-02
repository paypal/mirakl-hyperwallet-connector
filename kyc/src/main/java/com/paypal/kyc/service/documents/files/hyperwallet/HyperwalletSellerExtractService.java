package com.paypal.kyc.service.documents.files.hyperwallet;

import com.paypal.kyc.model.KYCDocumentSellerInfoModel;

/**
 * Interface that manages logic of pushing documents to Hyperwallet at seller level
 */
public interface HyperwalletSellerExtractService {

	/**
	 * Pushes proof of identity/business documents into HW
	 * @param kycDocumentSellerInfoModel
	 * @return If the document was pushed to HW
	 */
	boolean pushDocuments(KYCDocumentSellerInfoModel kycDocumentSellerInfoModel);

}
