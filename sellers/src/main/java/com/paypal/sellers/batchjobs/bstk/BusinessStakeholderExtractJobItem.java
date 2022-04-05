package com.paypal.sellers.batchjobs.bstk;

import com.paypal.infrastructure.batchjob.AbstractBatchJobItem;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;

/**
 * Class that holds the needed information for batch processing
 * {@link BusinessStakeHolderModel}
 */
public class BusinessStakeholderExtractJobItem extends AbstractBatchJobItem<BusinessStakeHolderModel> {

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
		return "BusinessStakeholder";
	}

}
