package com.paypal.kyc.service.documents.files.hyperwallet;

import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;

import java.util.List;

/**
 * Interface that manages logic of pushing documents to Hyperwallet at business
 * stakeholder level
 */
public interface HyperwalletBusinessStakeholderExtractService {

	/**
	 * Obtains List of required verification business stakeholders based on a user token
	 * and an hyperwalletProgram
	 * @param hyperwalletProgram Program for which the verification will be obtained
	 * @param userToken Token of the user
	 * @return List of {@link String} with required verification business stakeholders
	 * tokens
	 */
	List<String> getKYCRequiredVerificationBusinessStakeHolders(String hyperwalletProgram, String userToken);

	/**
	 * Pushes the documents into HW
	 * @param kycBusinessStakeHolderInfoModel Business stakeholders documents to push
	 * @return If the document was pushed to HW
	 */
	boolean pushDocuments(final KYCDocumentBusinessStakeHolderInfoModel kycBusinessStakeHolderInfoModel);

}
