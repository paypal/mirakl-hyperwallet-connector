package com.paypal.jobsystem;

import com.paypal.infrastructure.support.date.TimeMachine;
import com.paypal.jobsystem.batchjob.model.BatchJobItemStatus;
import com.paypal.jobsystem.batchjob.model.BatchJobStatus;
import com.paypal.jobsystem.batchjobaudit.repositories.BatchJobItemTrackingRepository;
import com.paypal.jobsystem.batchjobaudit.repositories.BatchJobTrackingRepository;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobItemTrackInfoEntity;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobTrackInfoEntity;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import com.paypal.testsupport.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ExtractionDeltaCalculationITTest extends AbstractIntegrationTest {

	@Autowired
	private BatchJobTrackingRepository batchJobTrackingRepository;

	@Autowired
	private BatchJobItemTrackingRepository batchJobItemTrackingRepository;

	@Autowired
	private BatchJobTrackingService batchJobTrackingService;

	@AfterEach
	void resetTimeMachine() {
		TimeMachine.useSystemDefaultZoneClock();
	}

	@Test
	void repository_shouldFindJobsWithItems_WhenTheyAreInDateRange() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());

		final BatchJobTrackInfoEntity batchJobTrackInfoEntity1 = BatchJobTrackingEntitiesMother
				.buildJobTrackingInfo('A', 1, 11);
		final List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities1 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity1, 10);
		final BatchJobTrackInfoEntity batchJobTrackInfoEntity2 = BatchJobTrackingEntitiesMother
				.buildJobTrackingInfo('A', 2, 10);
		final BatchJobTrackInfoEntity batchJobTrackInfoEntity3 = BatchJobTrackingEntitiesMother
				.buildJobTrackingInfo('A', 3, 10);
		final List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities3 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity3, 10);
		final BatchJobTrackInfoEntity batchJobTrackInfoEntity4 = BatchJobTrackingEntitiesMother
				.buildJobTrackingInfo('B', 4, 10);
		final List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities4 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity4, 10);

		batchJobTrackingRepository.saveAll(List.of(batchJobTrackInfoEntity1, batchJobTrackInfoEntity2,
				batchJobTrackInfoEntity3, batchJobTrackInfoEntity4));
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities1);
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities3);
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities4);

		final List<BatchJobTrackInfoEntity> jobsWithItems = batchJobTrackingRepository.findLastJobExecutionsWithItems(
				batchJobTrackInfoEntity1.getBatchJobType(), TimeMachine.now().minusDays(11).minusSeconds(1),
				Pageable.unpaged());

		assertThat(jobsWithItems).containsExactly(batchJobTrackInfoEntity3, batchJobTrackInfoEntity1);
	}

	@Test
	void repository_shouldNotFindJobsWithItems_WhenTheyAreNotInDateRange() {
		final BatchJobTrackInfoEntity batchJobTrackInfoEntity1 = BatchJobTrackingEntitiesMother
				.buildJobTrackingInfo('A', 1, 3);
		final List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities1 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity1, 10);
		final BatchJobTrackInfoEntity batchJobTrackInfoEntity2 = BatchJobTrackingEntitiesMother
				.buildJobTrackingInfo('A', 2, 3);
		final List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities2 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity2, 10);
		final BatchJobTrackInfoEntity batchJobTrackInfoEntity4 = BatchJobTrackingEntitiesMother
				.buildJobTrackingInfo('B', 4, 10);
		final List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities4 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity4, 10);

		batchJobTrackingRepository
				.saveAll(List.of(batchJobTrackInfoEntity1, batchJobTrackInfoEntity2, batchJobTrackInfoEntity4));
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities1);
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities2);
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities4);

		final List<BatchJobTrackInfoEntity> jobsWithItems = batchJobTrackingRepository.findLastJobExecutionsWithItems(
				batchJobTrackInfoEntity1.getBatchJobType(), TimeMachine.now().minusDays(2), Pageable.unpaged());

		assertThat(jobsWithItems).isEmpty();
	}

	@Test
	void service_shouldFindLastJobWithItems_WhenTheyAreInDateRange() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());

		final BatchJobTrackInfoEntity batchJobTrackInfoEntity1 = BatchJobTrackingEntitiesMother
				.buildJobTrackingInfo('A', 1, 3);
		final List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities1 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity1, 10);
		final BatchJobTrackInfoEntity batchJobTrackInfoEntity2 = BatchJobTrackingEntitiesMother
				.buildJobTrackingInfo('A', 2, 2);
		final List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities2 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity2, 10);
		final BatchJobTrackInfoEntity batchJobTrackInfoEntity4 = BatchJobTrackingEntitiesMother
				.buildJobTrackingInfo('B', 4, 10);
		final List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities4 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity4, 10);

		batchJobTrackingRepository
				.saveAll(List.of(batchJobTrackInfoEntity1, batchJobTrackInfoEntity2, batchJobTrackInfoEntity4));
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities1);
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities2);
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities4);

		final Optional<BatchJobTrackInfoEntity> result = batchJobTrackingService
				.findLastJobExecutionWithNonEmptyExtraction(batchJobTrackInfoEntity1.getBatchJobType(),
						TimeMachine.now().minusDays(2).minusSeconds(1));

		assertThat(result).contains(batchJobTrackInfoEntity2);
	}

	static class BatchJobTrackingEntitiesMother {

		static BatchJobTrackInfoEntity buildJobTrackingInfo(final char type, final int id, final int days) {
			// @formatter:off
			return BatchJobTrackInfoEntity.builder()
					.batchJobId("JOB_ID_" + id)
					.batchJobType("JOB_TYPE_" + type)
					.startTime(TimeMachine.now().minusDays(days))
					.status(BatchJobStatus.FINISHED)
					.build();
			// @formatter:on
		}

		static List<BatchJobItemTrackInfoEntity> buildJobItemsTrackingInfo(
				final BatchJobTrackInfoEntity batchJobTrackInfo, final int num) {
			// @formatter:off
			return IntStream.of(0, num)
					.mapToObj(i -> BatchJobItemTrackInfoEntity.builder()
							.batchJobId(batchJobTrackInfo.getBatchJobId())
							.itemId(batchJobTrackInfo.getBatchJobId() + "_" + i)
							.itemType(batchJobTrackInfo.getBatchJobType())
							.startTime(batchJobTrackInfo.getStartTime())
							.status(BatchJobItemStatus.PENDING)
							.build())
					.collect(Collectors.toList());
			// @formatter:on
		}

	}

}
