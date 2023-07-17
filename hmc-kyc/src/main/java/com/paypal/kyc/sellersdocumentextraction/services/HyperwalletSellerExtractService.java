package com.paypal.kyc.sellersdocumentextraction.services;

import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;

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
