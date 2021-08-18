package com.paypal.kyc.service.documents.files.mirakl;

import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Interface that manages logic of getting business stakeholder information related to KYC
 * in Mirakl
 */
public interface MiraklBusinessStakeholderDocumentsExtractService extends MiraklDocumentsExtractService {

	/**
	 * Obtains valids and requiresKYC flagged business stakeholders since a delta time
	 * @param delta
	 * @return {@link List<KYCDocumentBusinessStakeHolderInfoModel>} valids to be sent to
	 * other system for KYC verification
	 */
	List<KYCDocumentBusinessStakeHolderInfoModel> extractBusinessStakeholderDocuments(Date delta);

	/**
	 * Obtains a list of business stakeholders defined in Mirakl based on a specific
	 * seller id and its business stakeholder HW tokens
	 * @param shopId seller Id
	 * @param businessStakeholderTokens business stakeholder HW tokens
	 * @return {@link List<KYCDocumentBusinessStakeHolderInfoModel>} list of different
	 * business stakeholder custom parameters identified by its number in Mirakl
	 */
	List<String> getKYCCustomValuesRequiredVerificationBusinessStakeholders(String shopId,
			List<String> businessStakeholderTokens);

	/**
	 * Sets KYC required verification flag for business stakeholder to false in Mirakl
	 * @param correctlySentBusinessStakeholderDocuments
	 * {@link List<KYCDocumentBusinessStakeHolderInfoModel>} of all business stakeholder
	 * to be unchecked from KYC verification
	 * @return {@link Optional<MiraklUpdatedShops>} if everything was fine. Otherwise
	 * empty.
	 */
	Optional<MiraklUpdatedShops> setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(
			List<KYCDocumentBusinessStakeHolderInfoModel> correctlySentBusinessStakeholderDocuments);

}
