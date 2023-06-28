package com.paypal.jobsystem.testsupport;

import com.paypal.infrastructure.support.date.TimeMachine;
import com.paypal.jobsystem.batchjob.model.*;
import com.paypal.jobsystem.batchjobfailures.repositories.BatchJobFailedItemRepository;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import com.paypal.jobsystem.batchjobfailures.services.cache.BatchJobFailedItemCacheService;
import com.paypal.jobsystem.batchjobfailures.support.AbstractOnlyCachedFailedItemsBatchJobItemsExtractor;
import com.paypal.jobsystem.batchjobsupport.model.*;
import com.paypal.jobsystem.batchjobsupport.support.AbstractBatchJob;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobBuilder;
import com.paypal.testsupport.AbstractIntegrationTest;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.quartz.*;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;

public abstract class AbstractBatchJobTestSupport extends AbstractIntegrationTest {

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

	@Autowired
	protected TestBatchJobPreProcessor testBatchJobPreProcessor;

	@Autowired
	protected TestBatchJobItemValidator testBatchJobItemValidator;

	@Autowired
	protected TestBatchJobItemEnricher testBatchJobItemEnricher;

	protected static final AtomicBoolean jobRunning = new AtomicBoolean(false);

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

	@BeforeEach
	void resetBatchJobsState() {
		testBatchJobItemExtractor.itemsIdsToExtract = new ArrayList<>();
		testBatchJobItemProcessor.itemsIdsToFail = new HashSet<>();
		testBatchJobItemProcessor.itemsProcessedSuccesfully = new HashSet<>();
	}

	@PostConstruct
	public void jobJobExecutionInformationListenerInit() throws SchedulerException {
		scheduler.getListenerManager().addJobListener(new JobRunningListener());
	}

	protected void runJob(final JobDetail job) throws SchedulerException, InterruptedException {
		//@formatter:off
		final Trigger trigger =  TriggerBuilder.newTrigger()
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

	protected static abstract class AbstractTestBatchJob extends AbstractBatchJob<BatchJobContext, TestBatchJobItem> {

		private final TestBatchJobItemEnricher testBatchJobItemEnricher;

		private final TestBatchJobItemValidator testBatchJobItemValidator;

		private final TestBatchJobPreProcessor testBatchJobPreProcessor;

		protected AbstractTestBatchJob(final TestBatchJobItemEnricher testBatchJobItemEnricher,
				final TestBatchJobItemValidator testBatchJobItemValidator,
				final TestBatchJobPreProcessor testBatchJobPreProcessor) {
			this.testBatchJobItemEnricher = testBatchJobItemEnricher;
			this.testBatchJobItemValidator = testBatchJobItemValidator;
			this.testBatchJobPreProcessor = testBatchJobPreProcessor;
		}

		@Override
		protected Optional<BatchJobItemValidator<BatchJobContext, TestBatchJobItem>> getBatchJobItemValidator() {
			return Optional.of(testBatchJobItemValidator);
		}

		@Override
		protected Optional<BatchJobPreProcessor<BatchJobContext, TestBatchJobItem>> getBatchJobPreProcessor() {
			return Optional.of(testBatchJobPreProcessor);
		}

		@Override
		protected Optional<BatchJobItemEnricher<BatchJobContext, TestBatchJobItem>> getBatchJobItemEnricher() {
			return Optional.of(testBatchJobItemEnricher);
		}

	}

	protected static class TestRetryBatchJob extends AbstractTestBatchJob {

		private final TestRetryBatchJobItemExtractor testRetryBatchJobItemExtractor;

		private final TestBatchJobItemProcessor testBatchJobItemProcessor;

		TestRetryBatchJob(final TestRetryBatchJobItemExtractor testRetryBatchJobItemExtractor,
				final TestBatchJobItemProcessor testBatchJobItemProcessor,
				final TestBatchJobItemEnricher testBatchJobItemEnricher,
				final TestBatchJobItemValidator testBatchJobItemValidator,
				final TestBatchJobPreProcessor testBatchJobPreProcessor) {
			super(testBatchJobItemEnricher, testBatchJobItemValidator, testBatchJobPreProcessor);
			this.testRetryBatchJobItemExtractor = testRetryBatchJobItemExtractor;
			this.testBatchJobItemProcessor = testBatchJobItemProcessor;
		}

		@Override
		protected BatchJobItemProcessor<BatchJobContext, TestBatchJobItem> getBatchJobItemProcessor() {
			return testBatchJobItemProcessor;
		}

		@Override
		protected BatchJobItemsExtractor<BatchJobContext, TestBatchJobItem> getBatchJobItemsExtractor() {
			return testRetryBatchJobItemExtractor;
		}

		@Override
		public BatchJobType getType() {
			return BatchJobType.RETRY;
		}

	}

	protected static class TestBatchJob extends AbstractTestBatchJob {

		private final TestBatchJobItemExtractor testBatchJobItemExtractor;

		private final TestBatchJobItemProcessor testBatchJobItemProcessor;

		TestBatchJob(final TestBatchJobItemExtractor testBatchJobItemExtractor,
				final TestBatchJobItemProcessor testBatchJobItemProcessor,
				final TestBatchJobItemEnricher testBatchJobItemEnricher,
				final TestBatchJobItemValidator testBatchJobItemValidator,
				final TestBatchJobPreProcessor testBatchJobPreProcessor) {
			super(testBatchJobItemEnricher, testBatchJobItemValidator, testBatchJobPreProcessor);
			this.testBatchJobItemExtractor = testBatchJobItemExtractor;
			this.testBatchJobItemProcessor = testBatchJobItemProcessor;
		}

		@Override
		protected BatchJobItemProcessor<BatchJobContext, TestBatchJobItem> getBatchJobItemProcessor() {
			return testBatchJobItemProcessor;
		}

		@Override
		protected BatchJobItemsExtractor<BatchJobContext, TestBatchJobItem> getBatchJobItemsExtractor() {
			return testBatchJobItemExtractor;
		}

		@Override
		public BatchJobType getType() {
			return BatchJobType.EXTRACT;
		}

	}

	protected static class TestRetryBatchJobItemExtractor
			extends AbstractOnlyCachedFailedItemsBatchJobItemsExtractor<BatchJobContext, TestBatchJobItem> {

		protected TestRetryBatchJobItemExtractor(final Class<TestBatchJobItem> batchJobItemClass, final String itemType,
				final BatchJobFailedItemService batchJobFailedItemService,
				final BatchJobFailedItemCacheService batchJobFailedItemCacheService) {
			super(batchJobItemClass, itemType, batchJobFailedItemService, batchJobFailedItemCacheService);
		}

	}

	protected static class TestBatchJobItemProcessor
			implements BatchJobItemProcessor<BatchJobContext, TestBatchJobItem> {

		public Set<Integer> itemsIdsToFail = new HashSet<>();

		public Set<TestBatchJobItem> itemsProcessedSuccesfully = new HashSet<>();

		@Override
		public void processItem(final BatchJobContext ctx, final TestBatchJobItem jobItem) {
			if (itemsIdsToFail.contains(Integer.valueOf(jobItem.getItemId()))) {
				throw new RuntimeException("Item Failed");
			}
			else {
				itemsProcessedSuccesfully.add(jobItem);
			}
		}

	}

	protected static class TestBatchJobItemExtractor
			implements BatchJobItemsExtractor<BatchJobContext, TestBatchJobItem> {

		public List<Integer> itemsIdsToExtract = Collections.emptyList();

		@Override
		public Collection<TestBatchJobItem> getItems(final BatchJobContext ctx) {
			return itemsIdsToExtract.stream().map(TestBatchJobItem::new).collect(Collectors.toList());
		}

	}

	protected static class TestBatchJobPreProcessor implements BatchJobPreProcessor<BatchJobContext, TestBatchJobItem> {

		public final Set<Integer> itemsIdsToFail = new HashSet<>();

		public final Set<TestBatchJobItem> itemsProcessedSuccesfully = new HashSet<>();

		@Override
		public void prepareForProcessing(final BatchJobContext ctx,
				final Collection<TestBatchJobItem> itemsToBeProcessed) {
			if (itemsToBeProcessed.stream().map(BatchJobItem::getItemId)
					.anyMatch(itemId -> itemsIdsToFail.contains(itemId))) {
				throw new RuntimeException("Item Failed");
			}
			else {
				itemsProcessedSuccesfully.addAll(itemsToBeProcessed);
			}
		}

	}

	protected static class TestBatchJobItemValidator
			implements BatchJobItemValidator<BatchJobContext, TestBatchJobItem> {

		public final Set<Integer> itemsIdsToFail = new HashSet<>();

		public final Set<Integer> itemsIdsToWarn = new HashSet<>();

		public Set<Integer> itemsIdsToReject = new HashSet<>();

		@Override
		public BatchJobItemValidationResult validateItem(final BatchJobContext ctx, final TestBatchJobItem jobItem) {
			final Integer itemId = Integer.valueOf(jobItem.getItemId());
			if (itemsIdsToReject.contains(itemId)) {
				return BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.INVALID).build();
			}
			else if (itemsIdsToWarn.contains(itemId)) {
				return BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.WARNING).build();
			}
			else if (itemsIdsToFail.contains(itemId)) {
				throw new RuntimeException("Item validation exception");
			}
			else {
				return BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.VALID).build();
			}
		}

	}

	protected static class TestBatchJobItemEnricher implements BatchJobItemEnricher<BatchJobContext, TestBatchJobItem> {

		public final Function<String, String> enrichmentFunction = Function.identity();

		@Override
		public TestBatchJobItem enrichItem(final BatchJobContext ctx, final TestBatchJobItem jobItem) {
			return new TestBatchJobItem(jobItem.getItemId(), enrichmentFunction.apply(jobItem.getItem()));
		}

	}

	protected static class TestBatchJobItem implements BatchJobItem<String> {

		private final String id;

		private final String value;

		public static final String TEST_BATCH_JOB_ITEM_TYPE = "test";

		public TestBatchJobItem(final String id, final String value) {
			this.id = id;
			this.value = value;
		}

		public TestBatchJobItem(final int i) {
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

	protected static class JobRunningListener extends JobListenerSupport {

		@Override
		public String getName() {
			return "jobRunningListener";
		}

		@Override
		public void jobWasExecuted(final JobExecutionContext context, final JobExecutionException jobException) {
			jobRunning.set(false);
		}

	}

	@TestConfiguration
	protected static class AbstractBatchJobTestSupportConfig {

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
		public TestBatchJobItemValidator testBatchJobItemValidator() {
			return new TestBatchJobItemValidator();
		}

		@Bean
		public TestBatchJobItemEnricher testBatchJobItemEnricher() {
			return new TestBatchJobItemEnricher();
		}

		@Bean
		public TestBatchJobPreProcessor testBatchJobPreProcessor() {
			return new TestBatchJobPreProcessor();
		}

		@Bean
		public TestRetryBatchJob testRetryBatchJob(final TestRetryBatchJobItemExtractor testRetryBatchJobItemExtractor,
				final TestBatchJobItemProcessor testBatchJobItemProcessor,
				final TestBatchJobItemEnricher testBatchJobItemEnricher,
				final TestBatchJobItemValidator testBatchJobItemValidator,
				final TestBatchJobPreProcessor testBatchJobPreProcessor) {
			return new TestRetryBatchJob(testRetryBatchJobItemExtractor, testBatchJobItemProcessor,
					testBatchJobItemEnricher, testBatchJobItemValidator, testBatchJobPreProcessor);
		}

		@Bean
		public TestBatchJob testBatchJob(final TestBatchJobItemExtractor testBatchJobItemExtractor,
				final TestBatchJobItemProcessor testBatchJobItemProcessor,
				final TestBatchJobItemEnricher testBatchJobItemEnricher,
				final TestBatchJobItemValidator testBatchJobItemValidator,
				final TestBatchJobPreProcessor testBatchJobPreProcessor) {
			return new TestBatchJob(testBatchJobItemExtractor, testBatchJobItemProcessor, testBatchJobItemEnricher,
					testBatchJobItemValidator, testBatchJobPreProcessor);
		}

		@Bean
		public JobDetail testJob(final TestBatchJob testBatchJob) {
			//@formatter:off
			return QuartzBatchJobBuilder.newJob(testBatchJob)
					.withIdentity("TEST")
					.storeDurably()
					.build();
			//@formatter:on
		}

		@Bean
		public JobDetail testRetryJob(final TestRetryBatchJob testRetryBatchJob) {
			//@formatter:off
			return QuartzBatchJobBuilder.newJob(testRetryBatchJob)
					.withIdentity("TEST-RETRY")
					.storeDurably()
					.build();
			//@formatter:on
		}

	}

}
