package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.infrastructure.batchjob.AbstractBatchJobItem;
import com.paypal.sellers.sellersextract.model.SellerModel;

/**
 * Class that holds the needed information for processing a bank account job.
 */
public class BankAccountExtractJobItem extends AbstractBatchJobItem<SellerModel> {

	public BankAccountExtractJobItem(final SellerModel item) {
		super(item);
	}

	/**
	 * Returns the {@code clientUserId} of a {@link SellerModel}
	 * @return the {@link String} clientUserId
	 */
	@Override
	public String getItemId() {
		return getItem().getClientUserId() + "-" + getItem().getBankAccountDetails().getBankAccountNumber();
	}

	/**
	 * Returns the type as {@link String} of the
	 * {@link com.paypal.infrastructure.batchjob.BatchJobItem}
	 * @return {@code BankAccount}
	 */
	@Override
	public String getItemType() {
		return "BankAccount";
	}

}
