package com.paypal.kyc.service.documents.files.mirakl;

import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;

/**
 * Interface that manages logic of downloading business stakeholder KYC files from Mirakl
 */
public interface MiraklBusinessStakeholderDocumentDownloadExtractService {

	/**
	 * Populates all KYC documents at business stakeholder lever attached in Mirakl to
	 * object received as parameter
	 * @param kycBusinesStakeHolderInfoModel
	 * @return {@link KYCDocumentBusinessStakeHolderInfoModel} that contains all documents
	 * attached in Mirakl for an specific business stakeholder
	 */
	KYCDocumentBusinessStakeHolderInfoModel getBusinessStakeholderDocumentsSelectedBySeller(
			KYCDocumentBusinessStakeHolderInfoModel kycBusinesStakeHolderInfoModel);

}
