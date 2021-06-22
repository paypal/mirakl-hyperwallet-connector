package com.paypal.sellers.jobs;

import com.paypal.infrastructure.model.entity.JobExecutionInformationEntity;
import com.paypal.infrastructure.repository.JobExecutionInformationRepository;
import com.paypal.sellers.bankaccountextract.service.BankAccountExtractService;
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
class BankAccountExtractJobTest {

	private static final String DELTA = "delta";

	private static final String JOB_NAME = "jobName";

	@InjectMocks
	private BankAccountExtractJob testObj;

	@Mock
	private JobExecutionContext contextMock;

	@Mock
	private BankAccountExtractService bankAccountExtractServiceMock;

	@Mock
	private JobExecutionInformationRepository jobExecutionInformationRepositoryMock;

	@Captor
	private ArgumentCaptor<Date> dateArgumentCaptor;

	@Captor
	private ArgumentCaptor<JobExecutionInformationEntity> deltaEntityArgumentCaptor;

	@Test
	void execute_shouldCallBankAccountExtractSellerServiceWithDeltaAsNullIfNoDeltaKeyIsFound() {
		when(contextMock.getJobDetail()).thenReturn(JobBuilder.newJob(MyJob.class).withIdentity("jobName").build());

		testObj.execute(contextMock);

		verify(bankAccountExtractServiceMock).extractBankAccounts(null);
	}

	@Test
	void execute_shouldCallMiraklBankAccountServiceWithDeltaAsNullIfDeltaKeyIsFound() {
		final Date expectedDate = new Date();
		when(contextMock.getJobDetail()).thenReturn(JobBuilder.newJob(MyJob.class)
				.usingJobData(new JobDataMap(Map.of(DELTA, expectedDate))).withIdentity(JOB_NAME).build());

		testObj.execute(contextMock);

		verify(bankAccountExtractServiceMock).extractBankAccounts(dateArgumentCaptor.capture());
		assertThat(dateArgumentCaptor.getValue()).isEqualTo(expectedDate);
	}

	@Test
	void createJobDataMap_shouldReturnMapWithDateAsDeltaValue() {
		final Date expectedDate = new Date();

		final JobDataMap result = BankAccountExtractJob.createJobDataMap(expectedDate);

		assertThat(result).containsEntry(DELTA, expectedDate);
	}

	private static class MyJob implements Job {

		@Override
		public void execute(final JobExecutionContext context) {
			// doNothing
		}

	}

}
