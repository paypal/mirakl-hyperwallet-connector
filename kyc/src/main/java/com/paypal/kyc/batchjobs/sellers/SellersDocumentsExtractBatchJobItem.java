package com.paypal.kyc.batchjobs.sellers;

import com.paypal.infrastructure.batchjob.AbstractBatchJobItem;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;

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
	 * Returns the type as {@link String} of the
	 * {@link com.paypal.infrastructure.batchjob.BatchJobItem}
	 * @return {@code SellerDocument}
	 */
	@Override
	public String getItemType() {
		return "SellerDocument";
	}

}
