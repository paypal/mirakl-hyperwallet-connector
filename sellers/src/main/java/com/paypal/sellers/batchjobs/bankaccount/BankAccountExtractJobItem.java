package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.infrastructure.batchjob.AbstractBatchJobItem;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.apache.commons.lang3.StringUtils;

/**
 * Class that holds the needed information for processing a bank account job.
 */
public class BankAccountExtractJobItem extends AbstractBatchJobItem<SellerModel> {

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

		if (getItem().getBankAccountDetails() != null
				&& StringUtils.isNotBlank(getItem().getBankAccountDetails().getBankAccountNumber())) {

			return getItem().getClientUserId() + "-" + "*****"
					+ StringUtils.right(getItem().getBankAccountDetails().getBankAccountNumber(), 4);

		}
		else {

			return getItem().getClientUserId() + "-" + BATCH_JOB_ITEM_EMPTY_ID;
		}
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
