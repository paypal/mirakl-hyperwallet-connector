package com.paypal.sellers.jobs;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobAdapterFactory;
import com.paypal.sellers.batchjobs.individuals.IndividualSellersExtractBatchJob;
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
class IndividualSellersExtractJobTest {

	@InjectMocks
	@Spy
	private MyIndividualSellersExtractJob testObj;

	@Mock
	private IndividualSellersExtractBatchJob individualSellersExtractBatchJobMock;

	@Mock
	private JobExecutionContext jobContextMock;

	@Test
	void execute_shouldCallIndividualSellerExtractBatchJob() throws JobExecutionException {
		doNothing().when(testObj).executeBatchJob(individualSellersExtractBatchJobMock, jobContextMock);
		testObj.execute(jobContextMock);

		verify(testObj).executeBatchJob(individualSellersExtractBatchJobMock, jobContextMock);
	}

	static class MyIndividualSellersExtractJob extends IndividualSellersExtractJob {

		public MyIndividualSellersExtractJob(QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
				IndividualSellersExtractBatchJob individualSellersExtractBatchJob) {
			super(quartzBatchJobAdapterFactory, individualSellersExtractBatchJob);
		}

		@Override
		protected void executeBatchJob(BatchJob batchJob, JobExecutionContext context) throws JobExecutionException {
			super.executeBatchJob(batchJob, context);
		}

	}

}