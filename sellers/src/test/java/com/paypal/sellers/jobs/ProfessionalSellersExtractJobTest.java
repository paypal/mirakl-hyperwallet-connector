package com.paypal.sellers.jobs;

import com.paypal.infrastructure.model.entity.JobExecutionInformationEntity;
import com.paypal.infrastructure.repository.JobExecutionInformationRepository;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.SellersExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfessionalSellersExtractJobTest {

	private static final String DELTA = "delta";

	private static final String JOB_NAME = "jobName";

	@InjectMocks
	private ProfessionalSellersExtractJob testObj;

	@Mock
	private SellersExtractService sellersExtractServiceMock;

	@Mock
	private JobExecutionInformationRepository jobExecutionInformationRepositoryMock;

	@Mock
	private JobExecutionContext contextMock;

	@Mock
	private JobDetail jobDetailMock;

	@Mock
	private SellerModel professionalSellerModelMock;

	@Captor
	private ArgumentCaptor<Date> dateArgumentCaptor;

	@Captor
	private ArgumentCaptor<JobExecutionInformationEntity> deltaEntityArgumentCaptor;

	@Test
	void execute_shouldCallSellersExtractServiceWithDeltaAsNullIfNoDeltaKeyIsFound() {
		when(contextMock.getJobDetail()).thenReturn(JobBuilder.newJob(MyJob.class).withIdentity("jobName").build());

		testObj.execute(contextMock);

		verify(sellersExtractServiceMock).extractProfessionals(null);
	}

	@Test
	void execute_shouldCallSellersExtractServiceWithDeltaAsNullIfDeltaKeyIsFound() {
		final Date expectedDate = new Date();
		when(contextMock.getJobDetail()).thenReturn(JobBuilder.newJob(MyJob.class)
				.usingJobData(new JobDataMap(Map.of(DELTA, expectedDate))).withIdentity(JOB_NAME).build());

		testObj.execute(contextMock);

		verify(sellersExtractServiceMock).extractProfessionals(dateArgumentCaptor.capture());
		assertThat(dateArgumentCaptor.getValue()).isEqualTo(expectedDate);
	}

	private static class MyJob implements Job {

		@Override
		public void execute(final JobExecutionContext context) {
			// doNothing
		}

	}

}
