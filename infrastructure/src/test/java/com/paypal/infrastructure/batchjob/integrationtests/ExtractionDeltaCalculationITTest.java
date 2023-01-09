package com.paypal.infrastructure.batchjob.integrationtests;

import com.paypal.infrastructure.batchjob.BatchJobItemStatus;
import com.paypal.infrastructure.batchjob.BatchJobStatus;
import com.paypal.infrastructure.batchjob.BatchJobTrackingService;
import com.paypal.infrastructure.batchjob.entities.BatchJobItemTrackInfoEntity;
import com.paypal.infrastructure.batchjob.entities.BatchJobTrackInfoEntity;
import com.paypal.infrastructure.batchjob.repository.BatchJobItemTrackingRepository;
import com.paypal.infrastructure.batchjob.repository.BatchJobTrackingRepository;
import com.paypal.infrastructure.util.TimeMachine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("IntegrationTest")
@SpringBootTest(classes = BatchJobTestContext.class)
@TestPropertySource(
		locations = { "classpath:infrastructure-test.properties", "classpath:infrastructure-test-db.properties" })
@ExtendWith(SpringExtension.class)
@Transactional
class ExtractionDeltaCalculationITTest {

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

		BatchJobTrackInfoEntity batchJobTrackInfoEntity1 = BatchJobTrackingEntitiesMother.buildJobTrackingInfo('A', 1,
				11);
		List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities1 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity1, 10);
		BatchJobTrackInfoEntity batchJobTrackInfoEntity2 = BatchJobTrackingEntitiesMother.buildJobTrackingInfo('A', 2,
				10);
		BatchJobTrackInfoEntity batchJobTrackInfoEntity3 = BatchJobTrackingEntitiesMother.buildJobTrackingInfo('A', 3,
				10);
		List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities3 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity3, 10);
		BatchJobTrackInfoEntity batchJobTrackInfoEntity4 = BatchJobTrackingEntitiesMother.buildJobTrackingInfo('B', 4,
				10);
		List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities4 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity4, 10);

		batchJobTrackingRepository.saveAll(List.of(batchJobTrackInfoEntity1, batchJobTrackInfoEntity2,
				batchJobTrackInfoEntity3, batchJobTrackInfoEntity4));
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities1);
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities3);
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities4);

		List<BatchJobTrackInfoEntity> jobsWithItems = batchJobTrackingRepository.findLastJobExecutionsWithItems(
				batchJobTrackInfoEntity1.getBatchJobType(), TimeMachine.now().minusDays(11).minusSeconds(1),
				Pageable.unpaged());

		assertThat(jobsWithItems).containsExactly(batchJobTrackInfoEntity3, batchJobTrackInfoEntity1);
	}

	@Test
	void repository_shouldNotFindJobsWithItems_WhenTheyAreNotInDateRange() {
		BatchJobTrackInfoEntity batchJobTrackInfoEntity1 = BatchJobTrackingEntitiesMother.buildJobTrackingInfo('A', 1,
				3);
		List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities1 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity1, 10);
		BatchJobTrackInfoEntity batchJobTrackInfoEntity2 = BatchJobTrackingEntitiesMother.buildJobTrackingInfo('A', 2,
				3);
		List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities2 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity2, 10);
		BatchJobTrackInfoEntity batchJobTrackInfoEntity4 = BatchJobTrackingEntitiesMother.buildJobTrackingInfo('B', 4,
				10);
		List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities4 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity4, 10);

		batchJobTrackingRepository
				.saveAll(List.of(batchJobTrackInfoEntity1, batchJobTrackInfoEntity2, batchJobTrackInfoEntity4));
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities1);
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities2);
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities4);

		List<BatchJobTrackInfoEntity> jobsWithItems = batchJobTrackingRepository.findLastJobExecutionsWithItems(
				batchJobTrackInfoEntity1.getBatchJobType(), TimeMachine.now().minusDays(2), Pageable.unpaged());

		assertThat(jobsWithItems).isEmpty();
	}

	@Test
	void service_shouldFindLastJobWithItems_WhenTheyAreInDateRange() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());

		BatchJobTrackInfoEntity batchJobTrackInfoEntity1 = BatchJobTrackingEntitiesMother.buildJobTrackingInfo('A', 1,
				3);
		List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities1 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity1, 10);
		BatchJobTrackInfoEntity batchJobTrackInfoEntity2 = BatchJobTrackingEntitiesMother.buildJobTrackingInfo('A', 2,
				2);
		List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities2 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity2, 10);
		BatchJobTrackInfoEntity batchJobTrackInfoEntity4 = BatchJobTrackingEntitiesMother.buildJobTrackingInfo('B', 4,
				10);
		List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities4 = BatchJobTrackingEntitiesMother
				.buildJobItemsTrackingInfo(batchJobTrackInfoEntity4, 10);

		batchJobTrackingRepository
				.saveAll(List.of(batchJobTrackInfoEntity1, batchJobTrackInfoEntity2, batchJobTrackInfoEntity4));
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities1);
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities2);
		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities4);

		Optional<BatchJobTrackInfoEntity> result = batchJobTrackingService.findLastJobExecutionWithNonEmptyExtraction(
				batchJobTrackInfoEntity1.getBatchJobType(), TimeMachine.now().minusDays(2).minusSeconds(1));

		assertThat(result).contains(batchJobTrackInfoEntity2);
	}

	static class BatchJobTrackingEntitiesMother {

		static BatchJobTrackInfoEntity buildJobTrackingInfo(char type, int id, int days) {
			// @formatter:off
			return BatchJobTrackInfoEntity.builder()
					.batchJobId("JOB_ID_" + id)
					.batchJobType("JOB_TYPE_" + type)
					.startTime(TimeMachine.now().minusDays(days))
					.status(BatchJobStatus.FINISHED)
					.build();
			// @formatter:on
		}

		static List<BatchJobItemTrackInfoEntity> buildJobItemsTrackingInfo(BatchJobTrackInfoEntity batchJobTrackInfo,
				int num) {
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
