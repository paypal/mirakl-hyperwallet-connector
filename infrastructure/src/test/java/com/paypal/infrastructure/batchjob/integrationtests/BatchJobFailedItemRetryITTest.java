package com.paypal.infrastructure.batchjob.integrationtests;

import com.paypal.infrastructure.batchjob.BatchJobFailedItemId;
import com.paypal.infrastructure.batchjob.ExponentialBackOffItemRetryPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("IntegrationTest")
@SpringBootTest(classes = BatchJobFailedItemCacheTestContext.class)
@TestPropertySource(
		locations = { "classpath:infrastructure-test.properties", "classpath:infrastructure-test-db.properties" })
@ExtendWith(SpringExtension.class)
class BatchJobFailedItemRetryITTest extends AbstractBatchJobTestSupport {

	@Autowired
	private ExponentialBackOffItemRetryPolicy exponentialBackOffItemRetryPolicy;

	@BeforeEach
	void resetBatchJobsState() {
		testBatchJobItemExtractor.itemsIdsToExtract = new ArrayList<>();
		testBatchJobItemProcessor.itemsIdsToFail = new HashSet<>();
		testBatchJobItemProcessor.itemsProcessedSuccesfully = new HashSet<>();
		exponentialBackOffItemRetryPolicy.setMinutesPerRetry(ExponentialBackOffItemRetryPolicy.MINUTES_PER_RETRY);
	}

	@Test
	void shouldRetryProcessingItemsReadingFromCache() throws SchedulerException, InterruptedException {
		testBatchJobItemExtractor.itemsIdsToExtract = List.of(1, 2);
		testBatchJobItemProcessor.itemsIdsToFail = Set.of(1);
		exponentialBackOffItemRetryPolicy.setMinutesPerRetry(0L);

		runJob(testJob);
		assertThat(batchJobFailedItemCacheService.retrieveItem(TestBatchJobItem.class,
				TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE, "1")).isPresent();
		assertThat(batchJobFailedItemCacheService.retrieveItem(TestBatchJobItem.class,
				TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE, "2")).isNotPresent();
		assertThat(batchJobFailedItemService.getFailedItemsForRetry(TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE))
				.hasSize(1);
		assertThat(batchJobFailedItemRepository
				.findById(new BatchJobFailedItemId("1", TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE)).get()
				.getNumberOfRetries()).isZero();

		runJob(testRetryJob);
		assertThat(batchJobFailedItemService.getFailedItemsForRetry(TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE))
				.hasSize(1);
		assertThat(batchJobFailedItemRepository
				.findById(new BatchJobFailedItemId("1", TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE)).get()
				.getNumberOfRetries()).isEqualTo(1);

		testBatchJobItemProcessor.itemsIdsToFail = Set.of(0);
		runJob(testRetryJob);
		assertThat(batchJobFailedItemService.getFailedItemsForRetry(TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE))
				.isEmpty();
		assertThat(testBatchJobItemProcessor.itemsProcessedSuccesfully).hasSize(2);
	}

}
