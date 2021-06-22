package com.paypal.kyc.jobs;

import com.paypal.infrastructure.repository.JobExecutionInformationRepository;
import com.paypal.kyc.service.DocumentsExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentsExtractJobTest {

	@InjectMocks
	private DocumentsExtractJob testObj;

	@Mock
	private JobExecutionContext contextMock;

	@Mock
	private DocumentsExtractService documentsExtractServiceMock;

	@Mock
	private JobExecutionInformationRepository jobExecutionInformationRepositoryMock;

	@Captor
	private ArgumentCaptor<Date> dateArgumentCaptor;

	private static final String DELTA = "delta";

	private static final String JOB_NAME = "jobName";

	@Test
	void execute_shouldCallDocumentsExtractServiceWithDeltaAsNullIfNoDeltaKeyIsFound() {
		when(contextMock.getJobDetail()).thenReturn(JobBuilder.newJob(MyJob.class).withIdentity(JOB_NAME).build());

		testObj.execute(contextMock);

		verify(documentsExtractServiceMock).extractProofOfIdentityAndBusinessSellerDocuments(null);
		verify(documentsExtractServiceMock).extractBusinessStakeholderDocuments(null);
	}

	@Test
	void execute_shouldCallMiraklDocumentsExtractServiceWithDeltaAsNullIfDeltaKeyIsFound() {
		final Date expectedDate = new Date();
		when(contextMock.getJobDetail()).thenReturn(JobBuilder.newJob(MyJob.class)
				.usingJobData(new JobDataMap(Map.of(DELTA, expectedDate))).withIdentity(JOB_NAME).build());

		testObj.execute(contextMock);

		verify(documentsExtractServiceMock)
				.extractProofOfIdentityAndBusinessSellerDocuments(dateArgumentCaptor.capture());
		assertThat(dateArgumentCaptor.getValue()).isEqualTo(expectedDate);
		verify(documentsExtractServiceMock).extractProofOfIdentityAndBusinessSellerDocuments(expectedDate);
	}

	@Test
	void createJobDataMap_shouldReturnMapWithDateAsDeltaValue() {
		final Date expectedDate = new Date();

		final JobDataMap result = DocumentsExtractJob.createJobDataMap(expectedDate);

		assertThat(result).containsEntry(DELTA, expectedDate);
	}

	private static class MyJob implements Job {

		@Override
		public void execute(final JobExecutionContext context) {
			// doNothing
		}

	}

}
