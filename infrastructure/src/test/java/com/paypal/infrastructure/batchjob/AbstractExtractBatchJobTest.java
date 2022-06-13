package com.paypal.infrastructure.batchjob;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AbstractExtractBatchJobTest {

	@InjectMocks
	private TestExtractBatchJob testObj;

	@Test
	void getType_shouldReturnExtractType() {
		assertThat(testObj.getType()).isEqualTo(BatchJobType.EXTRACT);
	}

	static class TestExtractBatchJob extends AbstractExtractBatchJob<BatchJobContext, BatchJobItem<?>> {

		@Override
		protected BatchJobItemProcessor<BatchJobContext, BatchJobItem<?>> getBatchJobItemProcessor() {
			return null;
		}

		@Override
		protected BatchJobItemsExtractor<BatchJobContext, BatchJobItem<?>> getBatchJobItemsExtractor() {
			return null;
		}

	}

}