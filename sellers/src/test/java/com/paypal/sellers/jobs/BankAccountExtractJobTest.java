package com.paypal.sellers.jobs;

import com.paypal.sellers.batchjobs.bankaccount.BankAccountExtractBatchJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankAccountExtractJobTest {

	@InjectMocks
	private BankAccountExtractJob testObj;

	@Mock
	private BankAccountExtractBatchJob bankAccountExtractBatchJobMock;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Test
	void execute_ShouldExecuteBankAccountExtractBatchJob() throws JobExecutionException {
		testObj.execute(jobExecutionContextMock);

		verify(bankAccountExtractBatchJobMock).execute(jobExecutionContextMock);
	}

}
