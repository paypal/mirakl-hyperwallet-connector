package com.paypal.kyc.jobs;

import com.paypal.kyc.batchjobs.businessstakeholders.BusinessStakeholdersDocumentsExtractBatchJob;
import com.paypal.kyc.batchjobs.sellers.SellersDocumentsExtractBatchJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DocumentsExtractJobTest {

	@InjectMocks
	private DocumentsExtractJob testObj;

	@Mock
	private SellersDocumentsExtractBatchJob sellersDocumentsExtractBatchJobMock;

	@Mock
	private BusinessStakeholdersDocumentsExtractBatchJob businessStakeholdersDocumentsExtractBatchJobMock;

	@Mock
	private JobExecutionContext contextMock;

	@Test
	void execute_ShouldCallBusinessStakeholderAndSellersDocumentExtractBatchJob() throws JobExecutionException {
		testObj.execute(contextMock);

		verify(sellersDocumentsExtractBatchJobMock).execute(contextMock);
		verify(businessStakeholdersDocumentsExtractBatchJobMock).execute(contextMock);
	}

}
