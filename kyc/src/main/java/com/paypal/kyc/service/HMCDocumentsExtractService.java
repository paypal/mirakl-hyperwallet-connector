package com.paypal.kyc.service;

import com.paypal.kyc.model.KYCDocumentInfoModel;

import java.util.List;

/**
 * Internal and independent logic needed for documents in HMC
 */
public interface HMCDocumentsExtractService {

	/**
	 * Clean all documents from hard disk received as parameter
	 * @param successFullPushedListOfDocuments
	 * @param <T> Class that extends from {@link KYCDocumentInfoModel}
	 */
	<T extends KYCDocumentInfoModel> void cleanUpDocumentsFiles(final List<T> successFullPushedListOfDocuments);

}
