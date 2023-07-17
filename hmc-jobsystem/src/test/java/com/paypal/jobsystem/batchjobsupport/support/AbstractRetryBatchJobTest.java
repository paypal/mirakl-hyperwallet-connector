package com.paypal.jobsystem.batchjobsupport.support;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjob.model.BatchJobType;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.jobsystem.batchjobsupport.support.AbstractRetryBatchJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AbstractRetryBatchJobTest {

	@InjectMocks
	private TestRetryBatchJob testObj;

	@Test
	void getType_shouldReturnRetryType() {
		assertThat(testObj.getType()).isEqualTo(BatchJobType.RETRY);
	}

	static class TestRetryBatchJob extends AbstractRetryBatchJob<BatchJobContext, BatchJobItem<?>> {

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
