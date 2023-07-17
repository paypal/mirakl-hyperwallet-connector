package com.paypal.kyc.sellersdocumentextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.kyc.sellersdocumentextraction.batchjobs.SellersDocumentsExtractBatchJob;
import com.paypal.kyc.sellersdocumentextraction.batchjobs.SellersDocumentsExtractBatchJobItem;
import com.paypal.kyc.sellersdocumentextraction.batchjobs.SellersDocumentsExtractBatchJobItemExtractor;
import com.paypal.kyc.sellersdocumentextraction.batchjobs.SellersDocumentsExtractBatchJobItemProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SellersDocumentsExtractBatchJobTest {

	@InjectMocks
	private SellersDocumentsExtractBatchJob testObj;

	@Mock
	private SellersDocumentsExtractBatchJobItemProcessor sellersDocumentsExtractBatchJobItemProcessorMock;

	@Mock
	private SellersDocumentsExtractBatchJobItemExtractor sellersDocumentsExtractBatchJobItemExtractorMock;

	@Test
	void getBatchJobItemProcessor_ShouldReturnSellersDocumentExtractBatchJobItemProcessor() {

		final BatchJobItemProcessor<BatchJobContext, SellersDocumentsExtractBatchJobItem> result = testObj
				.getBatchJobItemProcessor();

		assertThat(result).isEqualTo(sellersDocumentsExtractBatchJobItemProcessorMock);
	}

	@Test
	void getBatchJobItemsExtractor_ShouldReturnSellersDocumentExtractBatchJobItemsExtractor() {

		final BatchJobItemsExtractor<BatchJobContext, SellersDocumentsExtractBatchJobItem> result = testObj
				.getBatchJobItemsExtractor();

		assertThat(result).isEqualTo(sellersDocumentsExtractBatchJobItemExtractorMock);
	}

}
