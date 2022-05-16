package com.paypal.kyc.jobs;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobAdapterFactory;
import com.paypal.kyc.batchjobs.businessstakeholders.BusinessStakeholdersDocumentsExtractBatchJob;
import com.paypal.kyc.batchjobs.sellers.SellersDocumentsExtractBatchJob;
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

		public MyDocumentsExtractJob(QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
				SellersDocumentsExtractBatchJob sellersDocumentsExtractBatchJob,
				BusinessStakeholdersDocumentsExtractBatchJob businessStakeholdersDocumentsExtractBatchJob) {
			super(quartzBatchJobAdapterFactory, sellersDocumentsExtractBatchJob,
					businessStakeholdersDocumentsExtractBatchJob);
		}

		@Override
		protected void executeBatchJob(BatchJob batchJob, JobExecutionContext context) throws JobExecutionException {
			super.executeBatchJob(batchJob, context);
		}

	}

}
