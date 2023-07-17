package com.paypal.kyc.stakeholdersdocumentextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.kyc.stakeholdersdocumentextraction.batchjobs.BusinessStakeholdersDocumentsExtractBatchJob;
import com.paypal.kyc.stakeholdersdocumentextraction.batchjobs.BusinessStakeholdersDocumentsExtractBatchJobItem;
import com.paypal.kyc.stakeholdersdocumentextraction.batchjobs.BusinessStakeholdersDocumentsExtractBatchJobItemExtractor;
import com.paypal.kyc.stakeholdersdocumentextraction.batchjobs.BusinessStakeholdersDocumentsExtractBatchJobItemProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BusinessStakeholdersDocumentsExtractBatchJobTest {

	@InjectMocks
	private BusinessStakeholdersDocumentsExtractBatchJob testObj;

	@Mock
	private BusinessStakeholdersDocumentsExtractBatchJobItemProcessor businessStakeholdersDocumentsExtractBatchJobItemProcessor;

	@Mock
	private BusinessStakeholdersDocumentsExtractBatchJobItemExtractor businessStakeholdersDocumentsExtractBatchJobItemExtractor;

	@Test
	void getBatchJobItemProcessor_ShouldReturnBusinessStakeholdersDocumentExtractBatchJobItemProcessor() {

		final BatchJobItemProcessor<BatchJobContext, BusinessStakeholdersDocumentsExtractBatchJobItem> result = testObj
				.getBatchJobItemProcessor();

		assertThat(result).isEqualTo(businessStakeholdersDocumentsExtractBatchJobItemProcessor);
	}

	@Test
	void getBatchJobItemsExtractor_ShouldReturnBusinessStakeholdersDocumentExtractBatchJobItemsExtractor() {

		final BatchJobItemsExtractor<BatchJobContext, BusinessStakeholdersDocumentsExtractBatchJobItem> result = testObj
				.getBatchJobItemsExtractor();

		assertThat(result).isEqualTo(businessStakeholdersDocumentsExtractBatchJobItemExtractor);
	}

}
