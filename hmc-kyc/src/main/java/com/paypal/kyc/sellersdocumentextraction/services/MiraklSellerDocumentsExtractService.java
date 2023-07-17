package com.paypal.kyc.sellersdocumentextraction.services;

import com.paypal.kyc.documentextractioncommons.services.MiraklDocumentsExtractService;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentsExtractionResult;

import java.util.Date;

/**
 * Interface that manages logic of getting seller information related to KYC in Mirakl
 */
public interface MiraklSellerDocumentsExtractService extends MiraklDocumentsExtractService {

	/**
	 * Obtains valid and requiresKYC flagged proof of identity/business sellers since a
	 * delta time
	 * @param delta
	 * @return {@link KYCDocumentsExtractionResult< KYCDocumentSellerInfoModel >} valids
	 * to be sent to other system for KYC verification, including information about
	 * failures during the extraction process
	 */
	KYCDocumentsExtractionResult<KYCDocumentSellerInfoModel> extractProofOfIdentityAndBusinessSellerDocuments(
			Date delta);

	/**
	 * Retrieves a KYCDocumentInfoModel filled with all the information from a MiraklShop
	 * id
	 * @param shopId Mirakl shop id
	 * @return {@link KYCDocumentInfoModel} Model containing Seller information
	 */
	KYCDocumentInfoModel extractKYCSellerDocuments(final String shopId);

	/**
	 * Sets KYC required verification proof of identity/business flag for seller to false
	 * in Mirakl
	 * @param successFullPushedListOfDocument {@link KYCDocumentSellerInfoModel} seller to
	 * be unchecked from KYC verification
	 */
	void setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(
			KYCDocumentSellerInfoModel successFullPushedListOfDocument);

}
