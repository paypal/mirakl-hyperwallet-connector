package com.paypal.kyc.service.documents.files.mirakl;

import com.paypal.kyc.model.KYCDocumentSellerInfoModel;

public interface MiraklSellerDocumentDownloadExtractService {

	/**
	 * Populates all KYC documents at seller level attached in Mirakl to object received
	 * as parameter if all documents requried are present
	 * @param kycDocumentSellerInfoModel
	 * @return {@link KYCDocumentSellerInfoModel} that contains all documents attached in
	 * Mirakl for an specific seller
	 */
	KYCDocumentSellerInfoModel getDocumentsSelectedBySeller(KYCDocumentSellerInfoModel kycDocumentSellerInfoModel);

	/**
	 * Populates all KYC documents at seller level attached in Mirakl to object received
	 * as parameter
	 * @param kycDocumentSellerInfoModel
	 * @return {@link KYCDocumentSellerInfoModel} that contains all documents attached in
	 * Mirakl for an specific seller
	 */
	KYCDocumentSellerInfoModel populateMiraklShopDocuments(KYCDocumentSellerInfoModel kycDocumentSellerInfoModel);

}
