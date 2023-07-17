package com.paypal.kyc.sellersdocumentextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobsupport.support.AbstractBatchJobItem;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;

/**
 * Class that holds the needed information for batch processing
 * {@link KYCDocumentSellerInfoModel}
 */
public class SellersDocumentsExtractBatchJobItem extends AbstractBatchJobItem<KYCDocumentSellerInfoModel> {

	protected SellersDocumentsExtractBatchJobItem(final KYCDocumentSellerInfoModel item) {
		super(item);
	}

	/**
	 * Returns the {@code clientUserId} of a {@link KYCDocumentSellerInfoModel}
	 * @return the {@link String} as the item identifier.
	 */
	@Override
	public String getItemId() {
		return getItem().getClientUserId();
	}

	/**
	 * Returns the type as {@link String} of the {@link BatchJobItem}
	 * @return {@code SellerDocument}
	 */
	@Override
	public String getItemType() {
		return "SellerDocument";
	}

}
