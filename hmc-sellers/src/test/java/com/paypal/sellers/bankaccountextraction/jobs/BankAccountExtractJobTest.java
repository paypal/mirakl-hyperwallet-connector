package com.paypal.sellers.bankaccountextraction.jobs;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobAdapterFactory;
import com.paypal.sellers.bankaccountextraction.batchjobs.BankAccountExtractBatchJob;
import com.paypal.sellers.bankaccountextraction.jobs.BankAccountExtractJob;
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
class BankAccountExtractJobTest {

	@InjectMocks
	@Spy
	private MyBankAccountExtractJob testObj;

	@Mock
	private BankAccountExtractBatchJob bankAccountExtractBatchJobMock;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Test
	void execute_ShouldExecuteBankAccountExtractBatchJob() throws JobExecutionException {
		doNothing().when(testObj).executeBatchJob(bankAccountExtractBatchJobMock, jobExecutionContextMock);
		testObj.execute(jobExecutionContextMock);

		verify(testObj).executeBatchJob(bankAccountExtractBatchJobMock, jobExecutionContextMock);
	}

	static class MyBankAccountExtractJob extends BankAccountExtractJob {

		public MyBankAccountExtractJob(final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
				final BankAccountExtractBatchJob bankAccountExtractBatchJob) {
			super(quartzBatchJobAdapterFactory, bankAccountExtractBatchJob);
		}

		@Override
		protected void executeBatchJob(final BatchJob batchJob, final JobExecutionContext context)
				throws JobExecutionException {
			super.executeBatchJob(batchJob, context);
		}

	}

}
