package com.paypal.sellers.individualsellersextraction.jobs;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobAdapterFactory;
import com.paypal.sellers.individualsellersextraction.batchjobs.IndividualSellersExtractBatchJob;
import com.paypal.sellers.individualsellersextraction.jobs.IndividualSellersExtractJob;
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

		public MyIndividualSellersExtractJob(final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
				final IndividualSellersExtractBatchJob individualSellersExtractBatchJob) {
			super(quartzBatchJobAdapterFactory, individualSellersExtractBatchJob);
		}

		@Override
		protected void executeBatchJob(final BatchJob batchJob, final JobExecutionContext context)
				throws JobExecutionException {
			super.executeBatchJob(batchJob, context);
		}

	}

}
