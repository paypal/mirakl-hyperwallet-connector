package com.paypal.jobsystem.quartzadapter.jobcontext;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobStatus;
import com.paypal.jobsystem.quartzadapter.jobcontext.QuartzBatchJobContextAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

import java.util.UUID;

import static com.paypal.jobsystem.quartzadapter.jobcontext.QuartzBatchJobContextAdapter.KEY_BATCH_JOB_EXECUTION_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuartzBatchJobContextAdapterTest {

	private static final String KEY_BATCH_JOB_STATUS = "batchJobStatus";

	private static final String KEY_NUMBER_OF_ITEMS_PROCESSED = "numberOfItemsProcessed";

	private static final String KEY_NUMBER_OF_ITEMS_FAILED = "numberOfItemsFailed";

	private static final String KEY_NUMBER_OF_ITEMS_TO_BE_PROCESSED = "numberOfItemsToBeProcessed";

	private static final String KEY_BATCH_JOB_NAME = "batchJobName";

	private static final String JOB_NAME = "JobName";

	public static final int NUMBER_OF_ITEMS_PROCESSED = 22;

	public static final int NUMBER_OF_ITEMS_FAILED = 2;

	public static final int NUMBER_OF_ITEMS_TO_BE_PROCESSED = 24;

	private QuartzBatchJobContextAdapter testObj;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Mock
	private JobDetail jobDetailMock;

	@Mock
	private JobDataMap jobDataMapMock;

	@Mock
	private JobKey jobKeyMock;

	@BeforeEach
	public void setUp() {

		lenient().when(jobExecutionContextMock.getJobDetail()).thenReturn(jobDetailMock);
		lenient().doReturn(BatchJobContext.class).when(jobDetailMock).getJobClass();
		lenient().when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMapMock);
		lenient().when(jobDataMapMock.get(KEY_BATCH_JOB_STATUS)).thenReturn(BatchJobStatus.FINISHED);
		lenient().when(jobDataMapMock.get(KEY_NUMBER_OF_ITEMS_PROCESSED)).thenReturn(NUMBER_OF_ITEMS_PROCESSED);
		lenient().when(jobDataMapMock.get(KEY_NUMBER_OF_ITEMS_FAILED)).thenReturn(NUMBER_OF_ITEMS_FAILED);
		lenient().when(jobDataMapMock.get(KEY_NUMBER_OF_ITEMS_TO_BE_PROCESSED))
				.thenReturn(NUMBER_OF_ITEMS_TO_BE_PROCESSED);
		lenient().when(jobDataMapMock.get(KEY_BATCH_JOB_NAME)).thenReturn(JOB_NAME);

		testObj = new QuartzBatchJobContextAdapter(jobExecutionContextMock);
	}

	@Test
	void getJobName_ShouldReturnJobName() {

		final String result = testObj.getJobName();

		assertThat(result).isEqualTo(JOB_NAME);
	}

	@Test
	void initializeBatchJobUuid_ShouldGenerateUUID() {
		testObj.initializeBatchJobUuid();

		verify(jobDataMapMock).put(eq(KEY_BATCH_JOB_EXECUTION_UUID), argThat(x -> UUID.fromString(x) != null));
	}

	@Test
	void setNumberOfItemsToBeProcessed_ShouldSetNumberOfItemsToBeProcessedInJobDataMap() {

		testObj.setNumberOfItemsToBeProcessed(NUMBER_OF_ITEMS_TO_BE_PROCESSED);

		verify(jobDataMapMock).put(KEY_NUMBER_OF_ITEMS_TO_BE_PROCESSED,
				Integer.valueOf(NUMBER_OF_ITEMS_TO_BE_PROCESSED));
	}

	@Test
	void getNumberOfItemsToBeProcessed_ShouldReturnTheNumberOfItemsToBeProcessed() {

		final int result = testObj.getNumberOfItemsToBeProcessed();

		assertThat(result).isEqualTo(NUMBER_OF_ITEMS_TO_BE_PROCESSED);
	}

	@Test
	void getNumberOfItemsFailed_ShouldReturnTheNumberOfItemsFailed() {

		final int result = testObj.getNumberOfItemsFailed();

		assertThat(result).isEqualTo(NUMBER_OF_ITEMS_FAILED);
	}

	@Test
	void incrementFailedItems_ShouldIncrementFailedItems() {

		testObj.incrementFailedItems();

		verify(jobDataMapMock).put(KEY_NUMBER_OF_ITEMS_FAILED, Integer.valueOf(NUMBER_OF_ITEMS_FAILED + 1));
	}

	@Test
	void getNumberOfItemsProcessed_ShouldReturnTheNumberOfItemsProcessed() {

		final int result = testObj.getNumberOfItemsProcessed();

		assertThat(result).isEqualTo(NUMBER_OF_ITEMS_PROCESSED);
	}

	@Test
	void getNumberOfItemsRemaining_ShouldReturnTheNumberOfItemsRemaining() {

		final int result = testObj.getNumberOfItemsRemaining();

		assertThat(result).isZero();
	}

	@Test
	void incrementProcessedItems_ShouldIncrementProcessedItems() {

		testObj.incrementProcessedItems();

		verify(jobDataMapMock).put(KEY_NUMBER_OF_ITEMS_PROCESSED, Integer.valueOf(NUMBER_OF_ITEMS_PROCESSED + 1));
	}

	@Test
	void setRunningStatus_ShouldSetRunningStatusInJobDataMap() {

		testObj.setRunningStatus();

		verify(jobDataMapMock).put(KEY_BATCH_JOB_STATUS, BatchJobStatus.RUNNING);
	}

	@Test
	void setFinishedStatus_ShouldSetFinishedStatusInJobDataMap() {

		testObj.setFinishedStatus();

		verify(jobDataMapMock).put(KEY_BATCH_JOB_STATUS, BatchJobStatus.FINISHED);
	}

	@Test
	void setFinishedWithFailureStatus_ShouldSetFinishedStatusInJobDataMap() {

		testObj.setFinishedWithFailuresStatus();

		verify(jobDataMapMock).put(KEY_BATCH_JOB_STATUS, BatchJobStatus.FINISHED_WITH_FAILURES);
	}

	@Test
	void setFailedStatus_ShouldSetFailedStatusInJobDataMap() {

		testObj.setFailedStatus();

		verify(jobDataMapMock).put(KEY_BATCH_JOB_STATUS, BatchJobStatus.FAILED);
	}

	@Test
	void getStatus_ShouldReturnRunning_WhenCurrentStatusIsRunningAndNumberOfItemsFailedIsZero() {

		when(jobDataMapMock.get(KEY_NUMBER_OF_ITEMS_FAILED)).thenReturn(0);
		when(jobDataMapMock.get(KEY_BATCH_JOB_STATUS)).thenReturn(BatchJobStatus.RUNNING);

		final BatchJobStatus result = testObj.getStatus();

		assertThat(result).isEqualTo(BatchJobStatus.RUNNING);
	}

	@Test
	void getStatus_ShouldReturnRunningWithFailures_WhenCurrentStatusIsRunningAndNumberOfItemsFailedIsGreaterThanZero() {

		when(jobDataMapMock.get(KEY_NUMBER_OF_ITEMS_FAILED)).thenReturn(NUMBER_OF_ITEMS_FAILED);
		when(jobDataMapMock.get(KEY_BATCH_JOB_STATUS)).thenReturn(BatchJobStatus.RUNNING);

		final BatchJobStatus result = testObj.getStatus();

		assertThat(result).isEqualTo(BatchJobStatus.RUNNING_WITH_FAILURES);
	}

	@Test
	void getStatus_ShouldReturnFinished_WhenCurrentStatusIsFinishedAndNumberOfItemsFailedIsZero() {

		when(jobDataMapMock.get(KEY_NUMBER_OF_ITEMS_FAILED)).thenReturn(0);
		when(jobDataMapMock.get(KEY_BATCH_JOB_STATUS)).thenReturn(BatchJobStatus.FINISHED);

		final BatchJobStatus result = testObj.getStatus();

		assertThat(result).isEqualTo(BatchJobStatus.FINISHED);
	}

	@Test
	void getStatus_ShouldReturnFinishedWithFailures_WhenCurrentStatusIsFinishedAndNumberOfItemsFailedIsGreaterThanZero() {

		when(jobDataMapMock.get(KEY_NUMBER_OF_ITEMS_FAILED)).thenReturn(NUMBER_OF_ITEMS_FAILED);
		when(jobDataMapMock.get(KEY_BATCH_JOB_STATUS)).thenReturn(BatchJobStatus.FINISHED);

		final BatchJobStatus result = testObj.getStatus();

		assertThat(result).isEqualTo(BatchJobStatus.FINISHED_WITH_FAILURES);
	}

	@Test
	void getStatus_ShouldReturnCurrentStatus_WhenCurrentStatusIsNotFinishedOrRunning() {

		when(jobDataMapMock.get(KEY_BATCH_JOB_STATUS)).thenReturn(BatchJobStatus.FAILED);

		final BatchJobStatus result = testObj.getStatus();

		assertThat(result).isEqualTo(BatchJobStatus.FAILED);
	}

	@Test
	void getJobExecutionContext_ShouldReturnJobExecutionContext() {

		final JobExecutionContext result = testObj.getJobExecutionContext();

		assertThat(result).isEqualTo(jobExecutionContextMock);
	}

	@Test
	void resetCounters_ShouldItemsResetCounters() {

		testObj.resetCounters();

		verify(jobDataMapMock).put(KEY_NUMBER_OF_ITEMS_PROCESSED, Integer.valueOf(0));
		verify(jobDataMapMock).put(KEY_NUMBER_OF_ITEMS_FAILED, Integer.valueOf(0));
		verify(jobDataMapMock).put(KEY_NUMBER_OF_ITEMS_TO_BE_PROCESSED, Integer.valueOf(0));

	}

}
