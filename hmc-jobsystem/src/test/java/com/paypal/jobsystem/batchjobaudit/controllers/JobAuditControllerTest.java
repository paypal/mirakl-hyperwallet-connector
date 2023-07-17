package com.paypal.jobsystem.batchjobaudit.controllers;

import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import com.paypal.jobsystem.batchjobaudit.controllers.converters.BatchJobItemTrackInfoEntityConverter;
import com.paypal.jobsystem.batchjobaudit.controllers.converters.BatchJobTrackInfoEntityConverter;
import com.paypal.jobsystem.batchjobaudit.controllers.dto.BatchJobItemTrackInfoResponse;
import com.paypal.jobsystem.batchjobaudit.controllers.dto.BatchJobTrackInfoResponse;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobItemTrackInfoEntity;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobTrackInfoEntity;
import com.paypal.jobsystem.batchjobaudit.controllers.JobAuditController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobAuditControllerTest {

	@InjectMocks
	private JobAuditController testObj;

	@Mock
	private BatchJobItemTrackInfoEntityConverter batchJobItemTrackInfoEntityConverterMock;

	@Mock
	private BatchJobTrackInfoEntityConverter batchJobTrackInfoEntityConverterMock;

	@Mock
	private BatchJobTrackingService batchJobTrackingServiceMock;

	@Mock
	private LocalDateTime localDateTime1Mock, localDateTime2Mock;

	@Mock
	private BatchJobTrackInfoResponse batchJobTrackInfoResponse1Mock, batchJobTrackInfoResponse2Mock;

	@Mock
	private BatchJobItemTrackInfoResponse batchJobItemTrackInfoResponse1Mock, batchJobItemTrackInfoResponse2Mock;

	@Mock
	private BatchJobItemTrackInfoEntity batchJobItemTrackInfoEntity1Mock, batchJobItemTrackInfoEntity2Mock;

	@Mock
	private BatchJobTrackInfoEntity batchJobTrackInfoEntity1Mock, batchJobTrackInfoEntity2Mock;

	@Test
	void getAllJobs_ShouldReturnAllJobsInsideTimeRage() {
		final List<BatchJobTrackInfoEntity> batchJobTrackInfoEntities = List.of(batchJobTrackInfoEntity1Mock,
				batchJobTrackInfoEntity2Mock);
		final List<BatchJobTrackInfoResponse> batchJobTrackInfoResponses = List.of(batchJobTrackInfoResponse1Mock,
				batchJobTrackInfoResponse2Mock);

		when(batchJobTrackingServiceMock.getJobTrackingEntries(localDateTime1Mock, localDateTime2Mock))
				.thenReturn(batchJobTrackInfoEntities);
		when(batchJobTrackInfoEntityConverterMock.toResponse(batchJobTrackInfoEntities))
				.thenReturn(batchJobTrackInfoResponses);

		final List<BatchJobTrackInfoResponse> result = testObj.getAllJobs(localDateTime1Mock, localDateTime2Mock);

		assertThat(result).containsAll(batchJobTrackInfoResponses);
	}

	@Test
	void getJobItems_ShouldReturnAllTrackingItemsOfAJob() {
		final List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities = List
				.of(batchJobItemTrackInfoEntity1Mock, batchJobItemTrackInfoEntity2Mock);
		final List<BatchJobItemTrackInfoResponse> batchJobItemTrackInfoResponses = List
				.of(batchJobItemTrackInfoResponse1Mock, batchJobItemTrackInfoResponse2Mock);

		when(batchJobTrackingServiceMock.getJobItemTrackingEntries("job1")).thenReturn(batchJobItemTrackInfoEntities);
		when(batchJobItemTrackInfoEntityConverterMock.toResponse(batchJobItemTrackInfoEntities))
				.thenReturn(batchJobItemTrackInfoResponses);

		final List<BatchJobItemTrackInfoResponse> result = testObj.getJobItems("job1");

		assertThat(result).containsAll(batchJobItemTrackInfoResponses);
	}

}
