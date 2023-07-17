package com.paypal.sellers.professionalsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjobsupport.support.AbstractBatchJobItem;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;

/**
 * Class that holds the needed information for batch processing {@link SellerModel}
 */
public class ProfessionalSellerExtractJobItem extends AbstractBatchJobItem<SellerModel> {

	public static final String ITEM_TYPE = "ProfessionalSeller";

	protected ProfessionalSellerExtractJobItem(final SellerModel item) {
		super(item);
	}

	/**
	 * Returns the client user id.
	 * @return the client user id.
	 */
	@Override
	public String getItemId() {
		return getItem().getClientUserId();
	}

	/**
	 * Returns the seller item type.
	 * @return ProfessionalSeller as the seller item type.
	 */
	@Override
	public String getItemType() {
		return ITEM_TYPE;
	}

}
