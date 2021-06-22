package com.paypal.kyc.service.documents.files.mirakl;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Interface that manages logic of getting seller information related to KYC in Mirakl
 */
public interface MiraklSellerDocumentsExtractService {

	/**
	 * Obtains valids and requiresKYC flagged proof of identity/business sellers since a
	 * delta time
	 * @param delta
	 * @return {@link List< KYCDocumentBusinessStakeHolderInfoModel >} valids to be sent
	 * to other system for KYC verification
	 */
	List<KYCDocumentSellerInfoModel> extractProofOfIdentityAndBusinessSellerDocuments(Date delta);

	Optional<MiraklShop> extractMiraklShop(String shopId);

	/**
	 * Sets KYC required verification proof of identity/business flag for seller to false
	 * in Mirakl
	 * @param successFullPushedListOfDocuments {@link List<KYCDocumentSellerInfoModel>} of
	 * all seller to be unchecked from KYC verification
	 * @return {@link Optional<MiraklUpdatedShops>} if everything was fine. Otherwise
	 * empty.
	 */
	Optional<MiraklUpdatedShops> setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(
			List<KYCDocumentSellerInfoModel> successFullPushedListOfDocuments);

}
