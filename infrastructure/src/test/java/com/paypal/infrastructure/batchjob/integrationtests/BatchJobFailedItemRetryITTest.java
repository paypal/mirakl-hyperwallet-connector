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

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("IntegrationTest")
@SpringBootTest(classes = BatchJobTestContext.class)
@TestPropertySource(
		locations = { "classpath:infrastructure-test.properties", "classpath:infrastructure-test-db.properties" })
@ExtendWith(SpringExtension.class)
class BatchJobFailedItemRetryITTest extends AbstractBatchJobTestSupport {

	@Autowired
	private ExponentialBackOffItemRetryPolicy exponentialBackOffItemRetryPolicy;

	@BeforeEach
	void resetBatchJobsPolicy() {
		exponentialBackOffItemRetryPolicy.setMinutesPerRetry(ExponentialBackOffItemRetryPolicy.MINUTES_PER_RETRY);
	}

	@Test
	void shouldRetryProcessingItemsReadingFromCache() throws SchedulerException, InterruptedException {
		prepareItemsToExtractAndMarkOneForFailWhileProcessing();

		runJobAndCheckThatItemThatFailsIsAddedForRetry();

		runRetryJobAndCheckThatItemThatHasFailedIsRetried();

		cleanItemsMarkedForFailWhileProcessing();

		runRetryJobAndCheckThatItemThatPreviouslyFailedIsProcessedSuccesfully();
	}

	@Test
	void shouldRetryProcessingItemsThatFailedOnValidation() throws SchedulerException, InterruptedException {
		prepareItemsToExtractAndMarkOneForFailDuringValidation();

		runJobAndCheckThatItemThatFailsIsAddedForRetry();

		runRetryJobAndCheckThatItemThatHasFailedIsRetried();

		cleanItemsMarkedForFailDuringValidation();

		runRetryJobAndCheckThatItemThatPreviouslyFailedIsProcessedSuccesfully();
	}

	private void prepareItemsToExtractAndMarkOneForFailDuringValidation() {
		testBatchJobItemExtractor.itemsIdsToExtract = List.of(1, 2);
		testBatchJobItemValidator.itemsIdsToReject = Set.of(1);
		exponentialBackOffItemRetryPolicy.setMinutesPerRetry(0L);
	}

	private void prepareItemsToExtractAndMarkOneForFailWhileProcessing() {
		testBatchJobItemExtractor.itemsIdsToExtract = List.of(1, 2);
		testBatchJobItemProcessor.itemsIdsToFail = Set.of(1);
		exponentialBackOffItemRetryPolicy.setMinutesPerRetry(0L);
	}

	private void cleanItemsMarkedForFailWhileProcessing() {
		testBatchJobItemProcessor.itemsIdsToFail = Set.of(0);
	}

	private void cleanItemsMarkedForFailDuringValidation() {
		testBatchJobItemValidator.itemsIdsToReject = Set.of();
	}

	private void runRetryJobAndCheckThatItemThatPreviouslyFailedIsProcessedSuccesfully()
			throws SchedulerException, InterruptedException {
		runJob(testRetryJob);
		assertThat(batchJobFailedItemService.getFailedItemsForRetry(TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE))
				.isEmpty();
		assertThat(testBatchJobItemProcessor.itemsProcessedSuccesfully).hasSize(2);
	}

	private void runRetryJobAndCheckThatItemThatHasFailedIsRetried() throws SchedulerException, InterruptedException {
		runJob(testRetryJob);
		assertThat(batchJobFailedItemService.getFailedItemsForRetry(TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE))
				.hasSize(1);
		assertThat(batchJobFailedItemRepository
				.findById(new BatchJobFailedItemId("1", TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE)).get()
				.getNumberOfRetries()).isEqualTo(1);
	}

	private void runJobAndCheckThatItemThatFailsIsAddedForRetry() throws SchedulerException, InterruptedException {
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
	}

}
