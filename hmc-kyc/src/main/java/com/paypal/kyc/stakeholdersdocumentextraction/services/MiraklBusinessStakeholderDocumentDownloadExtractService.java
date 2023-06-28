package com.paypal.kyc.stakeholdersdocumentextraction.services;

import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;

/**
 * Interface that manages logic of downloading business stakeholder KYC files from Mirakl
 */
public interface MiraklBusinessStakeholderDocumentDownloadExtractService {

	/**
	 * Populates all KYC documents at business stakeholder lever attached in Mirakl to
	 * object received as parameter
	 * @param kycBusinessStakeHolderInfoModel
	 * @return {@link KYCDocumentBusinessStakeHolderInfoModel} that contains all documents
	 * attached in Mirakl for an specific business stakeholder
	 */
	KYCDocumentBusinessStakeHolderInfoModel getBusinessStakeholderDocumentsSelectedBySeller(
			KYCDocumentBusinessStakeHolderInfoModel kycBusinessStakeHolderInfoModel);

}
