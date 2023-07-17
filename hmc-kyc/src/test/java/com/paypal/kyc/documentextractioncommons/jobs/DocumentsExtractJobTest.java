package com.paypal.kyc.documentextractioncommons.jobs;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobAdapterFactory;
import com.paypal.kyc.documentextractioncommons.jobs.DocumentsExtractJob;
import com.paypal.kyc.stakeholdersdocumentextraction.batchjobs.BusinessStakeholdersDocumentsExtractBatchJob;
import com.paypal.kyc.sellersdocumentextraction.batchjobs.SellersDocumentsExtractBatchJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DocumentsExtractJobTest {

	@InjectMocks
	@Spy
	private MyDocumentsExtractJob testObj;

	@Mock
	private SellersDocumentsExtractBatchJob sellersDocumentsExtractBatchJobMock;

	@Mock
	private BusinessStakeholdersDocumentsExtractBatchJob businessStakeholdersDocumentsExtractBatchJobMock;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Test
	void execute_ShouldCallBusinessStakeholderAndSellersDocumentExtractBatchJob() throws JobExecutionException {
		doNothing().when(testObj).executeBatchJob(sellersDocumentsExtractBatchJobMock, jobExecutionContextMock);
		doNothing().when(testObj).executeBatchJob(businessStakeholdersDocumentsExtractBatchJobMock,
				jobExecutionContextMock);

		testObj.execute(jobExecutionContextMock);

		verify(testObj).executeBatchJob(sellersDocumentsExtractBatchJobMock, jobExecutionContextMock);
		verify(testObj).executeBatchJob(businessStakeholdersDocumentsExtractBatchJobMock, jobExecutionContextMock);
	}

	static class MyDocumentsExtractJob extends DocumentsExtractJob {

		public MyDocumentsExtractJob(final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
				final SellersDocumentsExtractBatchJob sellersDocumentsExtractBatchJob,
				final BusinessStakeholdersDocumentsExtractBatchJob businessStakeholdersDocumentsExtractBatchJob) {
			super(quartzBatchJobAdapterFactory, sellersDocumentsExtractBatchJob,
					businessStakeholdersDocumentsExtractBatchJob);
		}

		@Override
		protected void executeBatchJob(final BatchJob batchJob, final JobExecutionContext context)
				throws JobExecutionException {
			super.executeBatchJob(batchJob, context);
		}

	}

}
