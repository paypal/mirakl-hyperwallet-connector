package com.paypal.reports.services.impl;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.operator.domain.payment.MiraklTransactionsLines;
import com.mirakl.client.mmp.operator.domain.payment.transaction.MiraklTransactionLine;
import com.mirakl.client.mmp.operator.request.payment.transaction.MiraklTransactionLineRequest;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.reports.model.HmcMiraklTransactionLine;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.paypal.infrastructure.support.date.DateUtil.convertToLocalDateTime;
import static com.paypal.infrastructure.support.date.DateUtil.convertToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportsMiraklExtractServiceImplTest {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further information:\n";

	private static final String NEXT_PAGE_TOKEN = "next-page-token";

	Date startDate, endDate;

	@InjectMocks
	private ReportsMiraklExtractServiceImpl testObj;

	@Mock
	private MiraklClient reportsMiraklApiClientMock;

	@Mock
	private Converter<MiraklTransactionLine, HmcMiraklTransactionLine> miraklTransactionLogHMCMiraklTransactionLineConverterMock;

	@Mock
	private MiraklTransactionsLines transactionLinesMock;

	@Mock
	private MiraklTransactionsLines transactionLinesSecondPageMock;

	@Mock
	private MiraklTransactionLine transactionLogOneMock, transactionLogTwoMock;

	@Mock
	private HmcMiraklTransactionLine hmcMiraklTransactionLineOneMock, hmcMiraklTransactionLineTwoMock;

	@Mock
	private MailNotificationUtil mailNotificationMock;

	@Captor
	private ArgumentCaptor<MiraklTransactionLineRequest> miraklGetTransactionLinesRequest;

	@BeforeEach
	void setUp() {
		startDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 19, 12, 30, 22), ZoneId.systemDefault());
		endDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 20, 12, 0, 22), ZoneId.systemDefault());
	}

	@Test
	void getAllTransactions_shouldReturnHMCMiraklTransactionLinesCorrespondingWithTransactionLineLog() {
		when(reportsMiraklApiClientMock.getTransactionLines(miraklGetTransactionLinesRequest.capture()))
			.thenReturn(transactionLinesMock);

		when(transactionLinesMock.getData()).thenReturn(List.of(transactionLogOneMock, transactionLogTwoMock));
		when(miraklTransactionLogHMCMiraklTransactionLineConverterMock.convert(transactionLogOneMock))
			.thenReturn(hmcMiraklTransactionLineOneMock);
		when(miraklTransactionLogHMCMiraklTransactionLineConverterMock.convert(transactionLogTwoMock))
			.thenReturn(hmcMiraklTransactionLineTwoMock);

		final List<HmcMiraklTransactionLine> result = testObj.getAllTransactionLinesByDate(startDate, endDate);

		assertThat(result).containsExactlyInAnyOrder(hmcMiraklTransactionLineOneMock, hmcMiraklTransactionLineTwoMock);
	}

	@Test
	void getAllTransactions_shouldReturnAnEmptyListWhenSDKResponseIsEmpty() {
		when(reportsMiraklApiClientMock.getTransactionLines(miraklGetTransactionLinesRequest.capture()))
			.thenReturn(transactionLinesMock);

		when(transactionLinesMock.getData()).thenReturn(List.of());

		final List<HmcMiraklTransactionLine> result = testObj.getAllTransactionLinesByDate(startDate, endDate);

		assertThat(result).isEmpty();
	}

	@Test
	void getAllTransactions_shouldSendAnEmailWhenMiraklSDKThrowsAnException() {
		final MiraklException thrownException = new MiraklException("Something went wrong contacting Mirakl");
		when(reportsMiraklApiClientMock.getTransactionLines(any())).thenThrow(thrownException);

		final List<HmcMiraklTransactionLine> result = testObj.getAllTransactionLinesByDate(startDate, endDate);

		assertThat(result).isEmpty();
		verify(mailNotificationMock).sendPlainTextEmail("Issue detected retrieving log lines from Mirakl",
				(ERROR_MESSAGE_PREFIX + "Something went wrong getting transaction log information from %s to %s\n%s")
					.formatted(convertToString(convertToLocalDateTime(startDate), DATE_FORMAT),
							convertToString(convertToLocalDateTime(endDate), DATE_FORMAT),
							MiraklLoggingErrorsUtil.stringify(thrownException)));
	}

	@Test
	void getAllTransactions_shouldFetchAllPagesWhenNextPageTokenIsPresent() {
		final List<String> capturedPageTokens = new ArrayList<>();

		when(reportsMiraklApiClientMock.getTransactionLines(any(MiraklTransactionLineRequest.class)))
			.thenAnswer(invocation -> {
				var request = (MiraklTransactionLineRequest) invocation.getArgument(0);
				capturedPageTokens.add(request.getPageToken());
				if (capturedPageTokens.size() == 1) {
					return transactionLinesMock;
				}
				return transactionLinesSecondPageMock;
			});

		when(transactionLinesMock.getData()).thenReturn(Collections.singletonList(transactionLogOneMock));
		when(transactionLinesMock.getNextPageToken()).thenReturn(NEXT_PAGE_TOKEN);

		when(transactionLinesSecondPageMock.getData()).thenReturn(Collections.singletonList(transactionLogTwoMock));
		when(transactionLinesSecondPageMock.getNextPageToken()).thenReturn(null);

		when(miraklTransactionLogHMCMiraklTransactionLineConverterMock.convert(transactionLogOneMock))
			.thenReturn(hmcMiraklTransactionLineOneMock);
		when(miraklTransactionLogHMCMiraklTransactionLineConverterMock.convert(transactionLogTwoMock))
			.thenReturn(hmcMiraklTransactionLineTwoMock);

		final List<HmcMiraklTransactionLine> result = testObj.getAllTransactionLinesByDate(startDate, endDate);

		assertThat(result).containsExactlyInAnyOrder(hmcMiraklTransactionLineOneMock, hmcMiraklTransactionLineTwoMock);
		assertThat(capturedPageTokens).containsExactly(null, NEXT_PAGE_TOKEN);

	}

}
