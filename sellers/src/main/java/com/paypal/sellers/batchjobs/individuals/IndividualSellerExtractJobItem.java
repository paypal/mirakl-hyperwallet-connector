package com.paypal.sellers.batchjobs.individuals;

import com.paypal.infrastructure.batchjob.AbstractBatchJobItem;
import com.paypal.sellers.sellersextract.model.SellerModel;

/**
 * Class that holds the needed information for batch processing {@link SellerModel}
 */
public class IndividualSellerExtractJobItem extends AbstractBatchJobItem<SellerModel> {

	public IndividualSellerExtractJobItem(SellerModel item) {
		super(item);
	}

	/**
	 * Returns the {@code clientUserId} of a {@link SellerModel}
	 * @return the {@link String} clientUserId
	 */
	@Override
	public String getItemId() {
		return getItem().getClientUserId();
	}

	/**
	 * Returns the type as {@link String} of the
	 * {@link com.paypal.infrastructure.batchjob.BatchJobItem}
	 * @return {@code IndividualSeller}
	 */
	@Override
	public String getItemType() {
		return "IndividualSeller";
	}

}
