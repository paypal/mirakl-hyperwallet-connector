package com.paypal.sellers.bankaccountextraction.batchjobs;

import com.paypal.jobsystem.batchjobsupport.support.AbstractBatchJobItem;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;

/**
 * Class that holds the needed information for processing a bank account job.
 */
public class BankAccountExtractJobItem extends AbstractBatchJobItem<SellerModel> {

	public static final String ITEM_TYPE = "BankAccount";

	protected static final String BATCH_JOB_ITEM_EMPTY_ID = "empty";

	public BankAccountExtractJobItem(final SellerModel item) {
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
	 * @return {@code BankAccount}
	 */
	@Override
	public String getItemType() {
		return ITEM_TYPE;
	}

}
