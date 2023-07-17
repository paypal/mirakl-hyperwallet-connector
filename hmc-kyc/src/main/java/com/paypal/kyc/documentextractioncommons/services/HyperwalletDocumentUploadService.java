package com.paypal.kyc.documentextractioncommons.services;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;

import java.util.List;

/**
 * Interface to upload documents to Hyperwallet. It can upload both sellers and business
 * stakeholders documents.
 */
public interface HyperwalletDocumentUploadService {

	/**
	 * Upload a document to Hyperwallet
	 * @param kycDocumentInfoModel Documents to push
	 * @param hyperwalletVerificationDocuments List of documents in Hyperwallet format
	 */
	void uploadDocument(KYCDocumentInfoModel kycDocumentInfoModel,
			List<HyperwalletVerificationDocument> hyperwalletVerificationDocuments);

}
