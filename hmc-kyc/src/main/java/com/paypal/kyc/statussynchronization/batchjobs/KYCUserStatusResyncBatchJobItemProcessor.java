package com.paypal.kyc.statussynchronization.batchjobs;

import com.paypal.infrastructure.mirakl.settings.MiraklClientSettings;
import com.paypal.infrastructure.mirakl.settings.MiraklClientSettingsExecutor;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.kyc.incomingnotifications.services.KYCUserDocumentFlagsExecutor;
import com.paypal.kyc.incomingnotifications.services.KYCUserStatusExecutor;
import com.paypal.kyc.statussynchronization.model.KYCUserStatusInfoModel;
import org.springframework.stereotype.Component;

/**
 * It takes the info in a Hyperwallet user and processes it as a status notification.
 */
@Component
public class KYCUserStatusResyncBatchJobItemProcessor
		implements BatchJobItemProcessor<BatchJobContext, KYCUserStatusResyncBatchJobItem> {

	private final KYCUserStatusExecutor kyCUserStatusExecutor;

	private final KYCUserDocumentFlagsExecutor kycUserDocumentFlagsExecutor;

	protected boolean useStaging = true;

	public KYCUserStatusResyncBatchJobItemProcessor(final KYCUserStatusExecutor kyCUserStatusExecutor,
			final KYCUserDocumentFlagsExecutor kycUserDocumentFlagsExecutor) {
		this.kyCUserStatusExecutor = kyCUserStatusExecutor;
		this.kycUserDocumentFlagsExecutor = kycUserDocumentFlagsExecutor;
	}

	@Override
	public void processItem(final BatchJobContext ctx, final KYCUserStatusResyncBatchJobItem jobItem) {
		final KYCUserStatusInfoModel kYCUserStatusInfoModel = jobItem.getItem();
		MiraklClientSettingsExecutor.runWithSettings(new MiraklClientSettings(useStaging),
				() -> processKYCUserStatusInfoModel(kYCUserStatusInfoModel));
	}

	private void processKYCUserStatusInfoModel(final KYCUserStatusInfoModel kYCUserStatusInfoModel) {
		kyCUserStatusExecutor.execute(kYCUserStatusInfoModel.getKycUserStatusNotificationBodyModel());
		kycUserDocumentFlagsExecutor.execute(kYCUserStatusInfoModel.getKycUserDocumentFlagsNotificationBodyModel());
	}

}
