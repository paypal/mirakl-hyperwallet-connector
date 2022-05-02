package com.paypal.kyc.batchjobs.businessstakeholders;

import com.paypal.infrastructure.batchjob.AbstractBatchJobItem;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;

/**
 * Class that holds the needed information for batch processing
 * {@link KYCDocumentBusinessStakeHolderInfoModel}
 */
public class BusinessStakeholdersDocumentsExtractBatchJobItem
		extends AbstractBatchJobItem<KYCDocumentBusinessStakeHolderInfoModel> {

	protected BusinessStakeholdersDocumentsExtractBatchJobItem(final KYCDocumentBusinessStakeHolderInfoModel item) {
		super(item);
	}

	/**
	 * Returns the {@code clientUserId}, {@code BusinessStakeholderMiraklNumber} and
	 * {@code token} of a {@link KYCDocumentBusinessStakeHolderInfoModel}
	 * @return the {@link String} as the item identifier.
	 */
	@Override
	public String getItemId() {
		return getItem().getClientUserId() + "-" + getItem().getBusinessStakeholderMiraklNumber() + "-"
				+ getItem().getToken();
	}

	/**
	 * Returns the type as {@link String} of the
	 * {@link com.paypal.infrastructure.batchjob.BatchJobItem}
	 * @return {@code BusinessStakeholderDocument}
	 */
	@Override
	public String getItemType() {
		return "BusinessStakeholderDocument";
	}

}
