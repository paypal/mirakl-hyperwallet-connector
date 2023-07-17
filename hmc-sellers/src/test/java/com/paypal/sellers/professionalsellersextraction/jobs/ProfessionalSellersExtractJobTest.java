package com.paypal.sellers.professionalsellersextraction.jobs;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobAdapterFactory;
import com.paypal.sellers.stakeholdersextraction.batchjobs.BusinessStakeholdersExtractBatchJob;
import com.paypal.sellers.professionalsellersextraction.batchjobs.ProfessionalSellersExtractBatchJob;
import com.paypal.sellers.professionalsellersextraction.jobs.ProfessionalSellersExtractJob;
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
class ProfessionalSellersExtractJobTest {

	@InjectMocks
	@Spy
	private MyProfessionalSellersExtractJob testObj;

	@Mock
	private ProfessionalSellersExtractBatchJob professionalSellersExtractBatchJob;

	@Mock
	private BusinessStakeholdersExtractBatchJob businessStakeholdersExtractBatchJobMock;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Test
	void execute_ShouldCallProfessionalSellersExtractBatchJob() throws JobExecutionException {
		doNothing().when(testObj).executeBatchJob(professionalSellersExtractBatchJob, jobExecutionContextMock);
		doNothing().when(testObj).executeBatchJob(businessStakeholdersExtractBatchJobMock, jobExecutionContextMock);

		testObj.execute(jobExecutionContextMock);

		verify(testObj).executeBatchJob(professionalSellersExtractBatchJob, jobExecutionContextMock);
		verify(testObj).executeBatchJob(businessStakeholdersExtractBatchJobMock, jobExecutionContextMock);
	}

	static class MyProfessionalSellersExtractJob extends ProfessionalSellersExtractJob {

		public MyProfessionalSellersExtractJob(final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
				final ProfessionalSellersExtractBatchJob professionalSellersExtractBatchJob,
				final BusinessStakeholdersExtractBatchJob businessStakeholdersExtractBatchJob) {
			super(quartzBatchJobAdapterFactory, professionalSellersExtractBatchJob,
					businessStakeholdersExtractBatchJob);
		}

		@Override
		protected void executeBatchJob(final BatchJob batchJob, final JobExecutionContext context)
				throws JobExecutionException {
			super.executeBatchJob(batchJob, context);
		}

	}

}
