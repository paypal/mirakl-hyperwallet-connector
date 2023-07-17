package com.paypal.sellers.individualsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobsupport.support.AbstractBatchJobItem;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;

/**
 * Class that holds the needed information for batch processing {@link SellerModel}
 */
public class IndividualSellersExtractJobItem extends AbstractBatchJobItem<SellerModel> {

	public static final String ITEM_TYPE = "IndividualSeller";

	public IndividualSellersExtractJobItem(final SellerModel item) {
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
	 * Returns the type as {@link String} of the {@link BatchJobItem}
	 * @return {@code IndividualSeller}
	 */
	@Override
	public String getItemType() {
		return ITEM_TYPE;
	}

}
