package com.paypal.sellers.jobs;

import com.paypal.sellers.batchjobs.individuals.IndividualSellersExtractBatchJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IndividualSellersExtractJobTest {

	@InjectMocks
	private IndividualSellersExtractJob testObj;

	@Mock
	private JobExecutionContext jobContextMock;

	@Mock
	private IndividualSellersExtractBatchJob individualSellersExtractBatchJobMock;

	@Test
	void execute_shouldCallIndividualSellerExtractBatchJob() throws JobExecutionException {
		testObj.execute(jobContextMock);

		verify(individualSellersExtractBatchJobMock).execute(jobContextMock);
	}

}