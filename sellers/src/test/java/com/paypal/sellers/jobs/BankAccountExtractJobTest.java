package com.paypal.sellers.jobs;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobAdapterFactory;
import com.paypal.sellers.batchjobs.bankaccount.BankAccountExtractBatchJob;
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

		public MyBankAccountExtractJob(QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
				BankAccountExtractBatchJob bankAccountExtractBatchJob) {
			super(quartzBatchJobAdapterFactory, bankAccountExtractBatchJob);
		}

		@Override
		protected void executeBatchJob(BatchJob batchJob, JobExecutionContext context) throws JobExecutionException {
			super.executeBatchJob(batchJob, context);
		}

	}

}
