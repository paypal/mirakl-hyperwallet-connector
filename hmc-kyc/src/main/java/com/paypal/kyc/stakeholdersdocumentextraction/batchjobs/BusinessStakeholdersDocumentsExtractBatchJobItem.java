package com.paypal.kyc.stakeholdersdocumentextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobsupport.support.AbstractBatchJobItem;
import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;

/**
 * Class that holds the needed information for batch processing
 * {@link KYCDocumentBusinessStakeHolderInfoModel}
 */
public class BusinessStakeholdersDocumentsExtractBatchJobItem
		extends AbstractBatchJobItem<KYCDocumentBusinessStakeHolderInfoModel> {

	public BusinessStakeholdersDocumentsExtractBatchJobItem(final KYCDocumentBusinessStakeHolderInfoModel item) {
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
	 * Returns the type as {@link String} of the {@link BatchJobItem}
	 * @return {@code BusinessStakeholderDocument}
	 */
	@Override
	public String getItemType() {
		return "BusinessStakeholderDocument";
	}

}
