package com.paypal.kyc.documentextractioncommons.services;

import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;

import java.util.List;

/**
 * Interface that manages documents attached to sellers in Mirakl
 */
public interface MiraklDocumentsExtractService {

	/**
	 * Deletes all Mirakl documents from the seller
	 * @param successFullPushedListOfDocuments {@link List<KYCDocumentInfoModel>}
	 */
	void deleteAllDocumentsFromSeller(List<KYCDocumentInfoModel> successFullPushedListOfDocuments);

	/**
	 * Deletes Mirakl documents from the seller
	 * @param documentsToBeDeleted
	 */
	void deleteDocuments(final List<MiraklShopDocument> documentsToBeDeleted);

}
