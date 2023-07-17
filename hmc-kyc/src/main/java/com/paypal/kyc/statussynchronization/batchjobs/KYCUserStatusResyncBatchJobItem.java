package com.paypal.kyc.statussynchronization.batchjobs;

import com.paypal.jobsystem.batchjobsupport.support.AbstractBatchJobItem;
import com.paypal.kyc.statussynchronization.model.KYCUserStatusInfoModel;

public class KYCUserStatusResyncBatchJobItem extends AbstractBatchJobItem<KYCUserStatusInfoModel> {

	protected KYCUserStatusResyncBatchJobItem(final KYCUserStatusInfoModel item) {
		super(item);
	}

	@Override
	public String getItemId() {
		return getItem().getKycUserStatusNotificationBodyModel().getClientUserId();
	}

	@Override
	public String getItemType() {
		return "KYCUserStatusInfo";
	}

}
