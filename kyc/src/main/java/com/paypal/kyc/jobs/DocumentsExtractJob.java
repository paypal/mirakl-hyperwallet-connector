package com.paypal.kyc.jobs;

import com.paypal.infrastructure.job.AbstractDeltaInfoJob;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.service.DocumentsExtractService;
import com.paypal.kyc.service.KYCReadyForReviewService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Extract documents job for extracting Mirakl sellers documents data and populate them on
 * HyperWallet for KYC validation
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DocumentsExtractJob extends AbstractDeltaInfoJob {

	@Resource
	private DocumentsExtractService documentsExtractService;

	@Resource
	private KYCReadyForReviewService kycReadyForReviewService;

	/**
	 * {@inheritDoc} Fetches all the proof of identity, business documents and business
	 * stakeholders and exports this information to Hyperwallet
	 * <p>
	 * If at least one of them was successfully sent, it also sets the customer to Ready
	 * for Review in Hyperwallet
	 */
	@Override
	public void execute(final JobExecutionContext context) {
		final Date delta = getDelta(context);
		final List<KYCDocumentInfoModel> documents = new ArrayList<>();

		documents.addAll(documentsExtractService.extractProofOfIdentityAndBusinessSellerDocuments(delta));
		documents.addAll(documentsExtractService.extractBusinessStakeholderDocuments(delta));

		kycReadyForReviewService.notifyReadyForReview(documents);

		documentsExtractService.cleanUpDocumentsFiles(documents);
	}

}
