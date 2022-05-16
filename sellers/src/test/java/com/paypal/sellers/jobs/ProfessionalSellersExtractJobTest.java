package com.paypal.sellers.jobs;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobAdapterFactory;
import com.paypal.sellers.batchjobs.bstk.BusinessStakeholdersExtractBatchJob;
import com.paypal.sellers.batchjobs.professionals.ProfessionalSellersExtractBatchJob;
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

		public MyProfessionalSellersExtractJob(QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
				ProfessionalSellersExtractBatchJob professionalSellersExtractBatchJob,
				BusinessStakeholdersExtractBatchJob businessStakeholdersExtractBatchJob) {
			super(quartzBatchJobAdapterFactory, professionalSellersExtractBatchJob,
					businessStakeholdersExtractBatchJob);
		}

		@Override
		protected void executeBatchJob(BatchJob batchJob, JobExecutionContext context) throws JobExecutionException {
			super.executeBatchJob(batchJob, context);
		}

	}

}
