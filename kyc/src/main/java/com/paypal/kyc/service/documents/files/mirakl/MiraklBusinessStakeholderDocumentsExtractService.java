package com.paypal.kyc.service.documents.files.mirakl;

import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentsExtractionResult;

import java.util.Date;
import java.util.List;

/**
 * Interface that manages logic of getting business stakeholder information related to KYC
 * in Mirakl
 */
public interface MiraklBusinessStakeholderDocumentsExtractService extends MiraklDocumentsExtractService {

	/**
	 * Obtains valids and requiresKYC flagged business stakeholders since a delta time
	 * @param delta
	 * @return {@link KYCDocumentsExtractionResult<KYCDocumentBusinessStakeHolderInfoModel>}
	 * with documents valids to be sent to other system for KYC verification, including
	 * information about failures during the extraction process
	 */
	KYCDocumentsExtractionResult<KYCDocumentBusinessStakeHolderInfoModel> extractBusinessStakeholderDocuments(
			Date delta);

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
	 * @param correctlySentBusinessStakeholderDocument
	 * {@link KYCDocumentBusinessStakeHolderInfoModel} a business stakeholder to be
	 * unchecked from KYC verification.
	 */
	void setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(
			KYCDocumentBusinessStakeHolderInfoModel correctlySentBusinessStakeholderDocument);

}
