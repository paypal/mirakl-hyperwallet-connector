package com.paypal.sellers.stakeholdersextraction.batchjobs;

import com.paypal.jobsystem.batchjobsupport.support.AbstractBatchJobItem;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;

/**
 * Class that holds the needed information for batch processing
 * {@link BusinessStakeHolderModel}
 */
public class BusinessStakeholderExtractJobItem extends AbstractBatchJobItem<BusinessStakeHolderModel> {

	public static final String ITEM_TYPE = "BusinessStakeholder";

	protected BusinessStakeholderExtractJobItem(final BusinessStakeHolderModel item) {
		super(item);
	}

	/**
	 * Returns the client user id and the stakeholder id.
	 * @return the client user id and the stakeholder id.
	 */
	@Override
	public String getItemId() {
		return getItem().getClientUserId() + "-" + getItem().getStkId();
	}

	/**
	 * Returns the business stakeholder item type.
	 * @return BusinessStakeholder as the business stakeholder item type.
	 */
	@Override
	public String getItemType() {
		return ITEM_TYPE;
	}

}
