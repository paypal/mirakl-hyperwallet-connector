package com.paypal.sellers.jobs;

import com.paypal.sellers.batchjobs.bstk.BusinessStakeholdersExtractBatchJob;
import com.paypal.sellers.batchjobs.professionals.ProfessionalSellersExtractBatchJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfessionalSellersExtractJobTest {

	@InjectMocks
	private ProfessionalSellersExtractJob testObj;

	@Mock
	private ProfessionalSellersExtractBatchJob professionalSellersExtractBatchJob;

	@Mock
	private BusinessStakeholdersExtractBatchJob businessStakeholdersExtractBatchJobMock;

	@Mock
	private JobExecutionContext contextMock;

	@Test
	void execute_ShouldCallProfessionalSellersExtractBatchJob() throws JobExecutionException {
		testObj.execute(contextMock);

		verify(professionalSellersExtractBatchJob).execute(contextMock);
		verify(businessStakeholdersExtractBatchJobMock).execute(contextMock);
	}

}
