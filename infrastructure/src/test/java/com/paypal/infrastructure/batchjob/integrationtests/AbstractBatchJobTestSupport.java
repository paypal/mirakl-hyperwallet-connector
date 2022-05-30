package com.paypal.infrastructure.batchjob.integrationtests;

import com.paypal.infrastructure.batchjob.*;
import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheService;
import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobBuilder;
import com.paypal.infrastructure.util.TimeMachine;
import org.junit.jupiter.api.BeforeEach;
import org.quartz.*;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;

abstract class AbstractBatchJobTestSupport {

	@Autowired
	protected CacheManager cacheManager;

	@Autowired
	protected Scheduler scheduler;

	@Autowired
	protected BatchJobFailedItemService batchJobFailedItemService;

	@Autowired
	protected BatchJobFailedItemCacheService batchJobFailedItemCacheService;

	@Autowired
	protected BatchJobFailedItemRepository batchJobFailedItemRepository;

	@Autowired
	protected JobDetail testJob;

	@Autowired
	protected JobDetail testRetryJob;

	@Autowired
	protected TestBatchJobItemExtractor testBatchJobItemExtractor;

	@Autowired
	protected TestBatchJobItemProcessor testBatchJobItemProcessor;

	protected static AtomicBoolean jobRunning = new AtomicBoolean(false);

	@BeforeEach
	void cleanCache() {
		cacheManager.getCacheNames().stream().map(cacheManager::getCache).forEach(Cache::clear);
	}

	@BeforeEach
	void cleanRepositories() {
		batchJobFailedItemRepository.deleteAll();
	}

	@BeforeEach
	void cleanTimeMachineState() {
		TimeMachine.useSystemDefaultZoneClock();
	}

	@PostConstruct
	public void jobJobExecutionInformationListenerInit() throws SchedulerException {
		scheduler.getListenerManager().addJobListener(new JobRunningListener());
	}

	protected void runJob(JobDetail job) throws SchedulerException, InterruptedException {
		//@formatter:off
		Trigger trigger =  TriggerBuilder.newTrigger()
				.forJob(job)
				.withIdentity("TRIGGER-" + job.getKey().getName())
				.startNow()
				.build();
		//@formatter:on

		jobRunning.set(true);
		scheduler.scheduleJob(trigger);
		waitForJobsToFinish();
	}

	protected void waitForJobsToFinish() {
		await().atMost(2, TimeUnit.SECONDS).until(() -> !jobRunning.get());
	}

	static class TestRetryBatchJob extends AbstractBatchJob<BatchJobContext, TestBatchJobItem> {

		private final BatchJobFailedItemRetryITTest.TestRetryBatchJobItemExtractor testRetryBatchJobItemExtractor;

		private final BatchJobFailedItemRetryITTest.TestBatchJobItemProcessor testBatchJobItemProcessor;

		TestRetryBatchJob(BatchJobFailedItemRetryITTest.TestRetryBatchJobItemExtractor testRetryBatchJobItemExtractor,
				BatchJobFailedItemRetryITTest.TestBatchJobItemProcessor testBatchJobItemProcessor) {
			this.testRetryBatchJobItemExtractor = testRetryBatchJobItemExtractor;
			this.testBatchJobItemProcessor = testBatchJobItemProcessor;
		}

		@Override
		protected BatchJobItemProcessor<BatchJobContext, TestBatchJobItem> getBatchJobItemProcessor() {
			return testBatchJobItemProcessor;
		}

		@Override
		protected BatchJobItemsExtractor<BatchJobContext, BatchJobFailedItemRetryITTest.TestBatchJobItem> getBatchJobItemsExtractor() {
			return testRetryBatchJobItemExtractor;
		}

	}

	static class TestBatchJob
			extends AbstractBatchJob<BatchJobContext, BatchJobFailedItemRetryITTest.TestBatchJobItem> {

		private final BatchJobFailedItemRetryITTest.TestBatchJobItemExtractor testBatchJobItemExtractor;

		private final BatchJobFailedItemRetryITTest.TestBatchJobItemProcessor testBatchJobItemProcessor;

		TestBatchJob(BatchJobFailedItemRetryITTest.TestBatchJobItemExtractor testBatchJobItemExtractor,
				BatchJobFailedItemRetryITTest.TestBatchJobItemProcessor testBatchJobItemProcessor) {
			this.testBatchJobItemExtractor = testBatchJobItemExtractor;
			this.testBatchJobItemProcessor = testBatchJobItemProcessor;
		}

		@Override
		protected BatchJobItemProcessor<BatchJobContext, BatchJobFailedItemRetryITTest.TestBatchJobItem> getBatchJobItemProcessor() {
			return testBatchJobItemProcessor;
		}

		@Override
		protected BatchJobItemsExtractor<BatchJobContext, BatchJobFailedItemRetryITTest.TestBatchJobItem> getBatchJobItemsExtractor() {
			return testBatchJobItemExtractor;
		}

	}

	static class TestRetryBatchJobItemExtractor extends
			AbstractOnlyCachedFailedItemsBatchJobItemsExtractor<BatchJobContext, BatchJobFailedItemRetryITTest.TestBatchJobItem> {

		protected TestRetryBatchJobItemExtractor(Class<TestBatchJobItem> batchJobItemClass, String itemType,
				BatchJobFailedItemService batchJobFailedItemService,
				BatchJobFailedItemCacheService batchJobFailedItemCacheService) {
			super(batchJobItemClass, itemType, batchJobFailedItemService, batchJobFailedItemCacheService);
		}

	}

	static class TestBatchJobItemProcessor
			implements BatchJobItemProcessor<BatchJobContext, BatchJobFailedItemRetryITTest.TestBatchJobItem> {

		Set<Integer> itemsIdsToFail = new HashSet<>();

		Set<BatchJobFailedItemRetryITTest.TestBatchJobItem> itemsProcessedSuccesfully = new HashSet<>();

		@Override
		public void processItem(BatchJobContext ctx, BatchJobFailedItemRetryITTest.TestBatchJobItem jobItem) {
			if (itemsIdsToFail.contains(Integer.valueOf(jobItem.getItemId()))) {
				throw new RuntimeException("Item Failed");
			}
			else {
				itemsProcessedSuccesfully.add(jobItem);
			}
		}

	}

	static class TestBatchJobItemExtractor
			implements BatchJobItemsExtractor<BatchJobContext, BatchJobFailedItemRetryITTest.TestBatchJobItem> {

		List<Integer> itemsIdsToExtract = Collections.emptyList();

		@Override
		public Collection<BatchJobFailedItemRetryITTest.TestBatchJobItem> getItems(BatchJobContext ctx) {
			return itemsIdsToExtract.stream().map(BatchJobFailedItemRetryITTest.TestBatchJobItem::new)
					.collect(Collectors.toList());
		}

	}

	static class TestBatchJobItem implements BatchJobItem<String> {

		private final String id;

		private final String value;

		public static final String TEST_BATCH_JOB_ITEM_TYPE = "test";

		TestBatchJobItem(String id, String value) {
			this.id = id;
			this.value = value;
		}

		TestBatchJobItem(int i) {
			this(String.valueOf(i), "value-" + i);
		}

		@Override
		public String getItemId() {
			return id;
		}

		@Override
		public String getItemType() {
			return TEST_BATCH_JOB_ITEM_TYPE;
		}

		@Override
		public String getItem() {
			return value;
		}

	}

	static class JobRunningListener extends JobListenerSupport {

		@Override
		public String getName() {
			return "jobRunningListener";
		}

		@Override
		public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
			jobRunning.set(false);
		}

	}

	@TestConfiguration
	static class AbstractBatchJobTestSupportConfig {

		@Autowired
		private BatchJobFailedItemService batchJobFailedItemService;

		@Autowired
		private BatchJobFailedItemCacheService batchJobFailedItemCacheService;

		@Bean
		public TestRetryBatchJobItemExtractor testRetryBatchJobItemExtractor() {
			return new TestRetryBatchJobItemExtractor(TestBatchJobItem.class, "test", batchJobFailedItemService,
					batchJobFailedItemCacheService);
		}

		@Bean
		public TestBatchJobItemExtractor testBatchJobItemExtractor() {
			return new TestBatchJobItemExtractor();
		}

		@Bean
		public TestBatchJobItemProcessor testBatchJobItemProcessor() {
			return new TestBatchJobItemProcessor();
		}

		@Bean
		public TestRetryBatchJob testRetryBatchJob(TestRetryBatchJobItemExtractor testRetryBatchJobItemExtractor,
				TestBatchJobItemProcessor testBatchJobItemProcessor) {
			return new TestRetryBatchJob(testRetryBatchJobItemExtractor, testBatchJobItemProcessor);
		}

		@Bean
		public TestBatchJob testBatchJob(TestBatchJobItemExtractor testBatchJobItemExtractor,
				TestBatchJobItemProcessor testBatchJobItemProcessor) {
			return new TestBatchJob(testBatchJobItemExtractor, testBatchJobItemProcessor);
		}

		@Bean
		public JobDetail testJob(TestBatchJob testBatchJob) {
			//@formatter:off
			return QuartzBatchJobBuilder.newJob(testBatchJob)
					.withIdentity("TEST")
					.storeDurably()
					.build();
			//@formatter:on
		}

		@Bean
		public JobDetail testRetryJob(TestRetryBatchJob testRetryBatchJob) {
			//@formatter:off
			return QuartzBatchJobBuilder.newJob(testRetryBatchJob)
					.withIdentity("TEST-RETRY")
					.storeDurably()
					.build();
			//@formatter:on
		}

	}

}
