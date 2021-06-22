package com.paypal.reports.reportsextract.service.impl;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.payment.MiraklTransactionLog;
import com.mirakl.client.mmp.domain.payment.MiraklTransactionLogs;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.request.payment.MiraklGetTransactionLogsRequest;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.reports.reportsextract.model.HmcMiraklTransactionLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.paypal.infrastructure.util.DateUtil.convertToLocalDateTime;
import static com.paypal.infrastructure.util.DateUtil.convertToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportsMiraklExtractServiceImplTest {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further information:\n";

	Date startDate, endDate;

	@InjectMocks
	private ReportsMiraklExtractServiceImpl testObj;

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient reportsMiraklApiClientMock;

	@Mock
	private Converter<MiraklTransactionLog, HmcMiraklTransactionLine> miraklTransactionLogHMCMiraklTransactionLineConverterMock;

	@Mock
	private MiraklTransactionLogs transactionLogsMock;

	@Mock
	private MiraklTransactionLog transactionLogOneMock, transactionLogTwoMock;

	@Mock
	private HmcMiraklTransactionLine hmcMiraklTransactionLineOneMock, hmcMiraklTransactionLineTwoMock;

	@Mock
	private MailNotificationUtil mailNotificationMock;

	@Captor
	private ArgumentCaptor<MiraklGetTransactionLogsRequest> miraklGetTransactionLogsRequest;

	@BeforeEach
	void setUp() {
		startDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 19, 12, 30, 22), ZoneId.systemDefault());
		endDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 20, 12, 0, 22), ZoneId.systemDefault());
	}

	@Test
	void getAllTransactions_shouldReturnHMCMiraklTransactionLinesCorrespondingWithTransactionLineLog() {

		when(reportsMiraklApiClientMock.getTransactionLogs(miraklGetTransactionLogsRequest.capture()))
				.thenReturn(transactionLogsMock);
		when(transactionLogsMock.getTotalCount()).thenReturn(Long.valueOf(2));
		when(transactionLogsMock.getLines()).thenReturn(List.of(transactionLogOneMock, transactionLogTwoMock));
		when(miraklTransactionLogHMCMiraklTransactionLineConverterMock.convert(transactionLogOneMock))
				.thenReturn(hmcMiraklTransactionLineOneMock);
		when(miraklTransactionLogHMCMiraklTransactionLineConverterMock.convert(transactionLogTwoMock))
				.thenReturn(hmcMiraklTransactionLineTwoMock);

		final List<HmcMiraklTransactionLine> result = testObj.getAllTransactionLinesByDate(startDate, endDate);

		assertThat(result).containsExactlyInAnyOrder(hmcMiraklTransactionLineOneMock, hmcMiraklTransactionLineTwoMock);
	}

	@Test
	void getAllTransactions_shouldReturnAnEmptyListWhenSDKResponseIsEmpty() {
		when(reportsMiraklApiClientMock.getTransactionLogs(miraklGetTransactionLogsRequest.capture()))
				.thenReturn(transactionLogsMock);

		when(transactionLogsMock.getTotalCount()).thenReturn(Long.valueOf(0));
		when(transactionLogsMock.getLines()).thenReturn(null);

		final List<HmcMiraklTransactionLine> result = testObj.getAllTransactionLinesByDate(startDate, endDate);

		assertThat(result).isEmpty();
	}

	@Test
	void getAllTransactions_shouldSendAnEmailWhenMiraklSDKThrowsAnException() {
		final MiraklException thrownException = new MiraklException("Something went wrong contacting Mirakl");
		when(reportsMiraklApiClientMock.getTransactionLogs(any())).thenThrow(thrownException);

		final List<HmcMiraklTransactionLine> result = testObj.getAllTransactionLinesByDate(startDate, endDate);

		assertThat(result).isEmpty();
		verify(mailNotificationMock).sendPlainTextEmail("Issue detected retrieving log lines from Mirakl",
				String.format(
						ERROR_MESSAGE_PREFIX
								+ "Something went wrong getting transaction log information from %s to %s\n%s",
						convertToString(convertToLocalDateTime(startDate), DATE_FORMAT),
						convertToString(convertToLocalDateTime(endDate), DATE_FORMAT),
						MiraklLoggingErrorsUtil.stringify(thrownException)));
	}

}
