package com.paypal.kyc.service;

import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Manages document extract logic for KYC verification in HW
 */
public interface DocumentsExtractService {

	/**
	 * Manages input-output logic from Mirakl to Hyperwallet for KYC proof of
	 * identity/business verification for sellers
	 * @param delta
	 * @return
	 */
	List<KYCDocumentSellerInfoModel> extractProofOfIdentityAndBusinessSellerDocuments(Date delta);

	/**
	 * Manages input-output logic from Mirakl to Hyperwallet for KYC verification for
	 * sellers
	 * @param delta
	 * @return
	 */
	Collection<KYCDocumentBusinessStakeHolderInfoModel> extractBusinessStakeholderDocuments(Date delta);

	/**
	 * Clean all documents from hard disk received as parameter
	 * @param successFullPushedListOfDocuments
	 * @param <T> Class that extends from {@link KYCDocumentInfoModel}
	 */
	<T extends KYCDocumentInfoModel> void cleanUpDocumentsFiles(final List<T> successFullPushedListOfDocuments);

}
