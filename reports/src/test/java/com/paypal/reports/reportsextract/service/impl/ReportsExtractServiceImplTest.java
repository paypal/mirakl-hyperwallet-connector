package com.paypal.reports.reportsextract.service.impl;

import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.reports.infraestructure.configuration.ReportsConfig;
import com.paypal.reports.reportsextract.service.FinancialReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportsExtractServiceImplTest {

	@InjectMocks
	private ReportsExtractServiceImpl testObj;

	@Mock
	private FinancialReportService financialReportServiceMock;

	@Mock
	private ReportsConfig reportsConfigMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	private static final String FILE_NAME = "fileName";

	private static final String SERVER_URI = "http://localhost:8080";

	@Test
	void extractFinancialReport_shouldCallToFinancialReportServiceInOrderToGenerateTheFinancialReport() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime startLocalDate = TimeMachine.now();
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 12, 10, 20, 45));
		final LocalDateTime endLocalDate = TimeMachine.now();

		final Date startDate = DateUtil.convertToDate(startLocalDate, ZoneId.systemDefault());
		final Date endDate = DateUtil.convertToDate(endLocalDate, ZoneId.systemDefault());

		when(reportsConfigMock.getHmcServerUri()).thenReturn(SERVER_URI);
		when(financialReportServiceMock.generateFinancialReport(startDate, endDate, FILE_NAME)).thenReturn(FILE_NAME);

		testObj.extractFinancialReport(startDate, endDate, FILE_NAME);

		verify(financialReportServiceMock).generateFinancialReport(startDate, endDate, FILE_NAME);

		verify(mailNotificationUtilMock).sendPlainTextEmail(
				"Your Marketplace Financial Report from " + startDate + " to " + endDate,
				"Below there is a link to your Marketplace Financial Report for the period from " + startDate + " to "
						+ endDate + ". Please click on it to download the report:\n\n" + SERVER_URI
						+ "/downloads/financial-report/" + FILE_NAME);
	}

}
