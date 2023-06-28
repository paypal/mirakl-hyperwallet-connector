package com.paypal.jobsystem;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItemId;
import com.paypal.jobsystem.batchjobfailures.services.retrypolicies.ExponentialBackOffItemRetryPolicy;
import com.paypal.jobsystem.testsupport.AbstractBatchJobTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

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
		Assertions
				.assertThat(batchJobFailedItemService.getFailedItemsForRetry(TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE))
				.isEmpty();
		Assertions.assertThat(testBatchJobItemProcessor.itemsProcessedSuccesfully).hasSize(2);
	}

	private void runRetryJobAndCheckThatItemThatHasFailedIsRetried() throws SchedulerException, InterruptedException {
		runJob(testRetryJob);
		Assertions
				.assertThat(batchJobFailedItemService.getFailedItemsForRetry(TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE))
				.hasSize(1);
		Assertions.assertThat(batchJobFailedItemRepository
				.findById(new BatchJobFailedItemId("1", TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE)).get()
				.getNumberOfRetries()).isEqualTo(1);
	}

	private void runJobAndCheckThatItemThatFailsIsAddedForRetry() throws SchedulerException, InterruptedException {
		runJob(testJob);
		Assertions.assertThat(batchJobFailedItemCacheService.retrieveItem(TestBatchJobItem.class,
				TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE, "1")).isPresent();
		Assertions.assertThat(batchJobFailedItemCacheService.retrieveItem(TestBatchJobItem.class,
				TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE, "2")).isNotPresent();
		Assertions
				.assertThat(batchJobFailedItemService.getFailedItemsForRetry(TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE))
				.hasSize(1);
		Assertions.assertThat(batchJobFailedItemRepository
				.findById(new BatchJobFailedItemId("1", TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE)).get()
				.getNumberOfRetries()).isZero();
	}

}
