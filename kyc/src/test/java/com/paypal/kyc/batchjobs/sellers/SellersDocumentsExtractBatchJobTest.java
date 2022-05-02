package com.paypal.kyc.batchjobs.sellers;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.batchjob.BatchJobItemsExtractor;
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
