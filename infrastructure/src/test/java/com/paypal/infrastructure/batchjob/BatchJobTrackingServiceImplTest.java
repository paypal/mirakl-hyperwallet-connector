package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.batchjob.entities.BatchJobItemTrackInfoEntity;
import com.paypal.infrastructure.batchjob.entities.BatchJobItemTrackingInfoId;
import com.paypal.infrastructure.batchjob.entities.BatchJobTrackInfoEntity;
import com.paypal.infrastructure.batchjob.repository.BatchJobItemTrackingRepository;
import com.paypal.infrastructure.batchjob.repository.BatchJobTrackingRepository;
import com.paypal.infrastructure.util.TimeMachine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.paypal.infrastructure.batchjob.BatchJobTrackingServiceImpl.ITEM_NOT_FINISHED_STATUSES;
import static com.paypal.infrastructure.batchjob.BatchJobTrackingServiceImpl.JOB_NOT_FINISHED_STATUSES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchJobTrackingServiceImplTest {

	public static final String JOB_ID = "1234";

	public static final String JOB_TYPE = "sellers";

	public static final String BATCH_JOB_ITEM_ID = "0001";

	@InjectMocks
	private BatchJobTrackingServiceImpl testObj;

	@Mock
	private BatchJobTrackingRepository batchJobTrackingRepositoryMock;

	@Mock
	private BatchJobItemTrackingRepository batchJobItemTrackingRepositoryMock;

	@Captor
	private ArgumentCaptor<BatchJobTrackInfoEntity> batchJobTrackInfoEntityArgumentCaptor;

	@Captor
	private ArgumentCaptor<BatchJobItemTrackingInfoId> batchJobItemTrackingInfoIdArgumentCaptor;

	@Captor
	private ArgumentCaptor<List<BatchJobItemTrackInfoEntity>> batchJobItemTrackInfoEntitiesArgumentCaptor;

	@Mock
	private BatchJobTrackInfoEntity batchJobTrackInfoEntityMock;

	@Mock
	private BatchJobTrackInfoEntity.BatchJobTrackInfoEntityBuilder batchJobTrackInfoEntityBuilderMock;

	@Mock
	private BatchJobItemTrackInfoEntity.BatchJobItemTrackInfoEntityBuilder batchJobItemTrackInfoEntityBuilderMock;

	@Mock
	private BatchJobItem<?> batchJobItemMock;

	@Mock
	private BatchJobItemTrackInfoEntity batchJobItemTrackInfoEntityMock;

	@Test
	void trackJobStart_ShouldSaveABatchJobTrackInfoEntityWithRunningStatusAndStartTimeAsNow() {

		final LocalDateTime now = TimeMachine.now();
		TimeMachine.useFixedClockAt(now);

		testObj.trackJobStart(JOB_ID, JOB_TYPE);

		verify(batchJobTrackingRepositoryMock).save(batchJobTrackInfoEntityArgumentCaptor.capture());

		assertThat(batchJobTrackInfoEntityArgumentCaptor.getValue()).isEqualTo(BatchJobTrackInfoEntity.builder()
				.batchJobId(JOB_ID).batchJobType(JOB_TYPE).startTime(now).status(BatchJobStatus.RUNNING).build());
	}

	@Test
	void trackJobFinished_ShouldSetJobAsFinishedAndFinishTimeAsNow_WhenSuccessful() {

		final LocalDateTime now = TimeMachine.now();
		TimeMachine.useFixedClockAt(now);

		when(batchJobTrackingRepositoryMock.getById(JOB_ID)).thenReturn(batchJobTrackInfoEntityMock);

		testObj.trackJobFinished(JOB_ID, true);

		verify(batchJobTrackInfoEntityMock).setFinishTime(now);
		verify(batchJobTrackInfoEntityMock).setStatus(BatchJobStatus.FINISHED);

		verify(batchJobTrackingRepositoryMock).save(batchJobTrackInfoEntityMock);
	}

	@Test
	void trackJobFinished_ShouldSetJobAsFinishedWithFailuresAndFinishTimeAsNow_WhenNotSuccessful() {

		final LocalDateTime now = TimeMachine.now();
		TimeMachine.useFixedClockAt(now);

		when(batchJobTrackingRepositoryMock.getById(JOB_ID)).thenReturn(batchJobTrackInfoEntityMock);

		testObj.trackJobFinished(JOB_ID, false);

		verify(batchJobTrackInfoEntityMock).setFinishTime(now);
		verify(batchJobTrackInfoEntityMock).setStatus(BatchJobStatus.FINISHED_WITH_FAILURES);

		verify(batchJobTrackingRepositoryMock).save(batchJobTrackInfoEntityMock);
	}

	@Test
	void trackJobFailure_ShouldMarkJobAsFailed_WhenJobIsFound() {

		final LocalDateTime now = TimeMachine.now();
		TimeMachine.useFixedClockAt(now);

		when(batchJobTrackingRepositoryMock.findById(JOB_ID)).thenReturn(Optional.of(batchJobTrackInfoEntityMock));

		when(batchJobTrackInfoEntityMock.toBuilder()).thenReturn(batchJobTrackInfoEntityBuilderMock);

		when(batchJobTrackInfoEntityBuilderMock.status(BatchJobStatus.FAILED))
				.thenReturn(batchJobTrackInfoEntityBuilderMock);
		when(batchJobTrackInfoEntityBuilderMock.finishTime(now)).thenReturn(batchJobTrackInfoEntityBuilderMock);
		when(batchJobTrackInfoEntityBuilderMock.build()).thenReturn(batchJobTrackInfoEntityMock);

		testObj.trackJobFailure(JOB_ID, JOB_TYPE);

		verify(batchJobTrackingRepositoryMock).save(batchJobTrackInfoEntityArgumentCaptor.capture());

		assertThat(batchJobTrackInfoEntityArgumentCaptor.getValue()).isEqualTo(batchJobTrackInfoEntityMock);
	}

	@Test
	void trackJobFailure_ShouldCreateAFailedJob_WhenJobIsNotFound() {

		final LocalDateTime now = TimeMachine.now();
		TimeMachine.useFixedClockAt(now);

		when(batchJobTrackingRepositoryMock.findById(JOB_ID)).thenReturn(Optional.empty());

		testObj.trackJobFailure(JOB_ID, JOB_TYPE);

		verify(batchJobTrackingRepositoryMock).save(batchJobTrackInfoEntityArgumentCaptor.capture());

		assertThat(batchJobTrackInfoEntityArgumentCaptor.getValue())
				.isEqualTo(BatchJobTrackInfoEntity.builder().batchJobId(JOB_ID).batchJobType(JOB_TYPE)
						.status(BatchJobStatus.FAILED).startTime(now).finishTime(now).build());
	}

	@Test
	void markNonFinishedJobsAsAbortedByBatchJobType_ShouldMarkAsAbortedTheNotFinishedBatchJobTrackInfoEntitiesAndTheBatchJobItemTrackInfoEntities_WhenBatchJobItemTrackInfoEntitiesStatusesAreFinishedStatuses() {

		final BatchJobTrackInfoEntity batchJobTrackInfoEntity = BatchJobTrackInfoEntity.builder().batchJobId(JOB_ID)
				.build();
		when(batchJobTrackingRepositoryMock.findByBatchJobTypeAndStatusIn(JOB_TYPE, JOB_NOT_FINISHED_STATUSES))
				.thenReturn(List.of(batchJobTrackInfoEntity));

		final BatchJobItemTrackInfoEntity batchJobItemTrackInfoEntity = BatchJobItemTrackInfoEntity.builder()
				.batchJobId(JOB_ID).status(BatchJobItemStatus.FAILED).build();
		when(batchJobItemTrackingRepositoryMock.findByBatchJobId(JOB_ID))
				.thenReturn(List.of(batchJobItemTrackInfoEntity));

		testObj.markNonFinishedJobsAsAborted(JOB_TYPE);

		verify(batchJobTrackingRepositoryMock).save(batchJobTrackInfoEntityArgumentCaptor.capture());
		batchJobTrackInfoEntity.setStatus(BatchJobStatus.ABORTED);
		assertThat(batchJobTrackInfoEntityArgumentCaptor.getValue()).isEqualTo(batchJobTrackInfoEntity);

		verify(batchJobItemTrackingRepositoryMock).saveAll(batchJobItemTrackInfoEntitiesArgumentCaptor.capture());
		batchJobItemTrackInfoEntity.setStatus(BatchJobItemStatus.ABORTED);
		assertThat(batchJobItemTrackInfoEntitiesArgumentCaptor.getValue()).containsExactly(batchJobItemTrackInfoEntity);
	}

	@Test
	void markNonFinishedJobsAsAbortedByBatchJobType_ShouldMarkAsAbortedTheNotFinishedBatchJobTrackInfoEntitiesAndNotTheBatchJobItemTrackInfoEntities_WhenBatchJobItemTrackInfoEntitiesStatusesAreNotFinishedStatuses() {

		final BatchJobTrackInfoEntity batchJobTrackInfoEntity = BatchJobTrackInfoEntity.builder().batchJobId(JOB_ID)
				.build();
		when(batchJobTrackingRepositoryMock.findByBatchJobTypeAndStatusIn(JOB_TYPE, JOB_NOT_FINISHED_STATUSES))
				.thenReturn(List.of(batchJobTrackInfoEntity));

		final BatchJobItemTrackInfoEntity batchJobItemTrackInfoEntity = BatchJobItemTrackInfoEntity.builder()
				.batchJobId(JOB_ID).status(BatchJobItemStatus.PENDING).build();
		when(batchJobItemTrackingRepositoryMock.findByBatchJobId(JOB_ID))
				.thenReturn(List.of(batchJobItemTrackInfoEntity));

		testObj.markNonFinishedJobsAsAborted(JOB_TYPE);

		verify(batchJobTrackingRepositoryMock).save(batchJobTrackInfoEntityArgumentCaptor.capture());
		batchJobTrackInfoEntity.setStatus(BatchJobStatus.ABORTED);
		assertThat(batchJobTrackInfoEntityArgumentCaptor.getValue()).isEqualTo(batchJobTrackInfoEntity);

		verify(batchJobItemTrackingRepositoryMock).saveAll(List.of());
	}

	@Test
	void markNonFinishedJobsAsAborted_ShouldMarkAsAbortedTheNotFinishedBatchJobTrackInfoEntitiesAndTheBatchJobItemTrackInfoEntities_WhenBatchJobItemTrackInfoEntitiesStatusesAreFinishedStatuses() {

		final BatchJobTrackInfoEntity batchJobTrackInfoEntity = BatchJobTrackInfoEntity.builder().batchJobId(JOB_ID)
				.build();
		when(batchJobTrackingRepositoryMock.findByStatusIn(JOB_NOT_FINISHED_STATUSES))
				.thenReturn(List.of(batchJobTrackInfoEntity));

		final BatchJobItemTrackInfoEntity batchJobItemTrackInfoEntity = BatchJobItemTrackInfoEntity.builder()
				.batchJobId(JOB_ID).status(BatchJobItemStatus.FAILED).build();
		when(batchJobItemTrackingRepositoryMock.findByBatchJobId(JOB_ID))
				.thenReturn(List.of(batchJobItemTrackInfoEntity));

		testObj.markNonFinishedJobsAsAborted();

		verify(batchJobTrackingRepositoryMock).save(batchJobTrackInfoEntityArgumentCaptor.capture());
		batchJobTrackInfoEntity.setStatus(BatchJobStatus.ABORTED);
		assertThat(batchJobTrackInfoEntityArgumentCaptor.getValue()).isEqualTo(batchJobTrackInfoEntity);

		verify(batchJobItemTrackingRepositoryMock).saveAll(batchJobItemTrackInfoEntitiesArgumentCaptor.capture());
		batchJobItemTrackInfoEntity.setStatus(BatchJobItemStatus.ABORTED);
		assertThat(batchJobItemTrackInfoEntitiesArgumentCaptor.getValue()).containsExactly(batchJobItemTrackInfoEntity);
	}

	@Test
	void markNonFinishedJobsAsAborted_ShouldMarkAsAbortedTheNotFinishedBatchJobTrackInfoEntitiesAndNotTheBatchJobItemTrackInfoEntities_WhenBatchJobItemTrackInfoEntitiesStatusesAreNotFinishedStatuses() {

		final BatchJobTrackInfoEntity batchJobTrackInfoEntity = BatchJobTrackInfoEntity.builder().batchJobId(JOB_ID)
				.build();
		when(batchJobTrackingRepositoryMock.findByStatusIn(JOB_NOT_FINISHED_STATUSES))
				.thenReturn(List.of(batchJobTrackInfoEntity));

		final BatchJobItemTrackInfoEntity batchJobItemTrackInfoEntity = BatchJobItemTrackInfoEntity.builder()
				.batchJobId(JOB_ID).status(BatchJobItemStatus.PENDING).build();
		when(batchJobItemTrackingRepositoryMock.findByBatchJobId(JOB_ID))
				.thenReturn(List.of(batchJobItemTrackInfoEntity));

		testObj.markNonFinishedJobsAsAborted();

		verify(batchJobTrackingRepositoryMock).save(batchJobTrackInfoEntityArgumentCaptor.capture());
		batchJobTrackInfoEntity.setStatus(BatchJobStatus.ABORTED);
		assertThat(batchJobTrackInfoEntityArgumentCaptor.getValue()).isEqualTo(batchJobTrackInfoEntity);

		verify(batchJobItemTrackingRepositoryMock).saveAll(List.of());
	}

	@Test
	void trackJobItemsAdded_ShouldCreateBatchJobItemTrackInfoEntitiesAndSaveThem() {

		when(batchJobItemMock.getItemId()).thenReturn(BATCH_JOB_ITEM_ID);
		when(batchJobItemMock.getItemType()).thenReturn(JOB_TYPE);

		testObj.trackJobItemsAdded(JOB_ID, List.of(batchJobItemMock));

		verify(batchJobItemTrackingRepositoryMock).saveAll(batchJobItemTrackInfoEntitiesArgumentCaptor.capture());
		assertThat(batchJobItemTrackInfoEntitiesArgumentCaptor.getValue())
				.containsExactly(BatchJobItemTrackInfoEntity.builder().batchJobId(JOB_ID).itemId(BATCH_JOB_ITEM_ID)
						.itemType(JOB_TYPE).status(BatchJobItemStatus.PENDING).build());
	}

	@Test
	void trackJobItemProcessingStarted_ShouldSetJobStatusAsInProgress() {

		when(batchJobItemMock.getItemId()).thenReturn(BATCH_JOB_ITEM_ID);
		when(batchJobItemMock.getItemType()).thenReturn(JOB_TYPE);

		when(batchJobItemTrackingRepositoryMock.getById(batchJobItemTrackingInfoIdArgumentCaptor.capture()))
				.thenReturn(batchJobItemTrackInfoEntityMock);

		when(batchJobItemTrackInfoEntityMock.toBuilder()).thenReturn(batchJobItemTrackInfoEntityBuilderMock);
		when(batchJobItemTrackInfoEntityBuilderMock.status(BatchJobItemStatus.IN_PROGRESS))
				.thenReturn(batchJobItemTrackInfoEntityBuilderMock);
		when(batchJobItemTrackInfoEntityBuilderMock.build()).thenReturn(batchJobItemTrackInfoEntityMock);

		testObj.trackJobItemProcessingStarted(JOB_ID, batchJobItemMock);

		assertThat(batchJobItemTrackingInfoIdArgumentCaptor.getValue()).isEqualTo(BatchJobItemTrackingInfoId.builder()
				.batchJobId(JOB_ID).itemType(JOB_TYPE).itemId(BATCH_JOB_ITEM_ID).build());

		verify(batchJobItemTrackingRepositoryMock).save(batchJobItemTrackInfoEntityMock);
	}

	@Test
	void trackJobItemProcessingFinished_ShouldSetJobStatusAsSuccessful_WenIsSuccessful() {

		when(batchJobItemMock.getItemId()).thenReturn(BATCH_JOB_ITEM_ID);
		when(batchJobItemMock.getItemType()).thenReturn(JOB_TYPE);

		when(batchJobItemTrackingRepositoryMock.getById(batchJobItemTrackingInfoIdArgumentCaptor.capture()))
				.thenReturn(batchJobItemTrackInfoEntityMock);

		when(batchJobItemTrackInfoEntityMock.toBuilder()).thenReturn(batchJobItemTrackInfoEntityBuilderMock);
		when(batchJobItemTrackInfoEntityBuilderMock.status(BatchJobItemStatus.SUCCESSFUL))
				.thenReturn(batchJobItemTrackInfoEntityBuilderMock);
		when(batchJobItemTrackInfoEntityBuilderMock.build()).thenReturn(batchJobItemTrackInfoEntityMock);

		testObj.trackJobItemProcessingFinished(JOB_ID, batchJobItemMock, true);

		assertThat(batchJobItemTrackingInfoIdArgumentCaptor.getValue()).isEqualTo(BatchJobItemTrackingInfoId.builder()
				.batchJobId(JOB_ID).itemType(JOB_TYPE).itemId(BATCH_JOB_ITEM_ID).build());

		verify(batchJobItemTrackingRepositoryMock).save(batchJobItemTrackInfoEntityMock);
	}

	@Test
	void trackJobItemProcessingFinished_ShouldSetJobStatusAsFailed_WenIsNotSuccessful() {

		when(batchJobItemMock.getItemId()).thenReturn(BATCH_JOB_ITEM_ID);
		when(batchJobItemMock.getItemType()).thenReturn(JOB_TYPE);

		when(batchJobItemTrackingRepositoryMock.getById(batchJobItemTrackingInfoIdArgumentCaptor.capture()))
				.thenReturn(batchJobItemTrackInfoEntityMock);

		when(batchJobItemTrackInfoEntityMock.toBuilder()).thenReturn(batchJobItemTrackInfoEntityBuilderMock);
		when(batchJobItemTrackInfoEntityBuilderMock.status(BatchJobItemStatus.FAILED))
				.thenReturn(batchJobItemTrackInfoEntityBuilderMock);
		when(batchJobItemTrackInfoEntityBuilderMock.build()).thenReturn(batchJobItemTrackInfoEntityMock);

		testObj.trackJobItemProcessingFinished(JOB_ID, batchJobItemMock, false);

		assertThat(batchJobItemTrackingInfoIdArgumentCaptor.getValue()).isEqualTo(BatchJobItemTrackingInfoId.builder()
				.batchJobId(JOB_ID).itemType(JOB_TYPE).itemId(BATCH_JOB_ITEM_ID).build());

		verify(batchJobItemTrackingRepositoryMock).save(batchJobItemTrackInfoEntityMock);
	}

	@Test
	void getItemsBeingProcessedOrEnquedToProcess_ShouldReturnTheBatchJobItemTrackInfoEntityWithStatusNotFinished() {

		when(batchJobItemTrackingRepositoryMock.findByItemTypeAndStatusIn(JOB_TYPE, ITEM_NOT_FINISHED_STATUSES))
				.thenReturn(List.of(batchJobItemTrackInfoEntityMock));

		final List<BatchJobItemTrackInfoEntity> result = testObj.getItemsBeingProcessedOrEnquedToProcess(JOB_TYPE);

		assertThat(result).isEqualTo(List.of(batchJobItemTrackInfoEntityMock));
	}

	@Test
	void findLastJobExecutionWithNonEmptyExtraction_ShouldReturnJobWithNonEmptyExtraction() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());

		when(batchJobTrackingRepositoryMock.findLastJobExecutionsWithItems(JOB_TYPE, TimeMachine.now(),
				Pageable.ofSize(1))).thenReturn(List.of(batchJobTrackInfoEntityMock));

		Optional<BatchJobTrackInfoEntity> result = testObj.findLastJobExecutionWithNonEmptyExtraction(JOB_TYPE,
				TimeMachine.now());

		assertThat(result).contains(batchJobTrackInfoEntityMock);
	}

	@Test
	void findLastJobExecutionWithNonEmptyExtraction_ShouldReturnEmpty_WhenNoJobsFound() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());

		when(batchJobTrackingRepositoryMock.findLastJobExecutionsWithItems(JOB_TYPE, TimeMachine.now(),
				Pageable.ofSize(1))).thenReturn(List.of());

		Optional<BatchJobTrackInfoEntity> result = testObj.findLastJobExecutionWithNonEmptyExtraction(JOB_TYPE,
				TimeMachine.now());

		assertThat(result).isEmpty();
	}

}
