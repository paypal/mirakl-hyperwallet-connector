package com.paypal.reports.jobs;

import com.paypal.reports.reportsextract.service.ReportsExtractService;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportsExtractJobTest {

	private static final String FILE_NAME = "fileName";

	@InjectMocks
	@Spy
	private ReportsExtractJob testObj;

	@Mock
	private Date startDateMock, endDateMock;

	@Mock
	private ReportsExtractService reportsExtractServiceMock;

	@Mock
	private JobExecutionContext contextMock;

	@Mock
	private JobDetail jobDetailMock;

	@Test
	void execute() {
		doReturn(startDateMock).when(testObj).getStartDate(contextMock);
		doReturn(endDateMock).when(testObj).getEndDate(contextMock);
		doReturn(FILE_NAME).when(testObj).getFileName(contextMock);

		testObj.execute(contextMock);

		verify(reportsExtractServiceMock).extractFinancialReport(startDateMock, endDateMock, FILE_NAME);
	}

	@Test
	void creteJobDataMap_shouldAddStartDateEndDateAndFileNamePassedAsArgument() {
		final Date startDate = new Date();
		final Date endDate = new Date();
		final String fileName = "fileName";

		final JobDataMap result = ReportsExtractJob.createJobDataMap(startDate, endDate, fileName);

		AssertionsForInterfaceTypes.assertThat(result).contains(Map.entry("startDate", startDate),
				Map.entry("endDate", endDate), Map.entry("fileName", fileName));
	}

	@Test
	void getStartDate_shouldReturnStartDateWhenJobExecutionContextWithStartDateIsPassedAsArgument() {
		final Date now = new Date();
		when(contextMock.getJobDetail()).thenReturn(jobDetailMock);
		final JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("startDate", now);
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMap);

		final Date result = testObj.getStartDate(contextMock);

		assertThat(result).isEqualTo(now);
	}

	@Test
	void getEndDate_shouldReturnEndDateTimeWhenJobExecutionContextWithEndDateIsPassedAsArgument() {
		final Date now = new Date();
		when(contextMock.getJobDetail()).thenReturn(jobDetailMock);
		final JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("endDate", now);
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMap);

		final Date result = testObj.getEndDate(contextMock);

		assertThat(result).isEqualTo(now);
	}

	@Test
	void getFileName_shouldReturnFileNameWhenJobExecutionContextWithFileNameIsPassedAsArgument() {
		final String fileName = "fileName.csv";
		when(contextMock.getJobDetail()).thenReturn(jobDetailMock);
		final JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put("fileName", fileName);
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMap);

		final String result = testObj.getFileName(contextMock);

		assertThat(result).isEqualTo(fileName);
	}

}
