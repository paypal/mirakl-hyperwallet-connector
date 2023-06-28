package com.paypal.reports.services.impl;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.payment.MiraklTransactionLog;
import com.mirakl.client.mmp.domain.payment.MiraklTransactionLogs;
import com.mirakl.client.mmp.request.payment.MiraklGetTransactionLogsRequest;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.reports.model.HmcMiraklTransactionLine;
import com.paypal.reports.services.ReportsMiraklExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.paypal.infrastructure.support.date.DateUtil.convertToLocalDateTime;
import static com.paypal.infrastructure.support.date.DateUtil.convertToString;

@Slf4j
@Service
public class ReportsMiraklExtractServiceImpl implements ReportsMiraklExtractService {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final String EMAIL_SUBJECT_MESSAGE = "Issue detected retrieving log lines from Mirakl";

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further information:\n";

	private final MiraklClient miraklClient;

	private final Converter<MiraklTransactionLog, HmcMiraklTransactionLine> miraklTransactionLogMiraklTransactionLineConverter;

	private final MailNotificationUtil mailNotificationUtil;

	public ReportsMiraklExtractServiceImpl(final MiraklClient miraklClient,
			final Converter<MiraklTransactionLog, HmcMiraklTransactionLine> miraklTransactionLogMiraklTransactionLineConverter,
			final MailNotificationUtil mailNotificationUtil) {
		this.miraklClient = miraklClient;
		this.miraklTransactionLogMiraklTransactionLineConverter = miraklTransactionLogMiraklTransactionLineConverter;
		this.mailNotificationUtil = mailNotificationUtil;
	}

	@SuppressWarnings("java:S1874")
	@Override
	public List<HmcMiraklTransactionLine> getAllTransactionLinesByDate(final Date startDate, final Date endDate) {
		log.info("Retrieving Mirakl transaction logs from {} to {}", startDate, endDate);
		final var miraklGetTransactionLogsRequest = new MiraklGetTransactionLogsRequest();
		miraklGetTransactionLogsRequest.setStartDate(startDate);
		miraklGetTransactionLogsRequest.setEndDate(endDate);
		miraklGetTransactionLogsRequest.setPaginate(false);
		try {
			final MiraklTransactionLogs transactionLogs = miraklClient
					.getTransactionLogs(miraklGetTransactionLogsRequest);
			log.info("{} Mirakl transaction logs from {} to {}", transactionLogs.getTotalCount(), startDate, endDate);

			//@formatter:off
			return Optional.ofNullable(transactionLogs.getLines()).orElse(List.of()).stream()
					.map(miraklTransactionLogMiraklTransactionLineConverter::convert)
					.collect(Collectors.toList());
			//@formatter:on

		}
		catch (final MiraklException e) {
			log.error("Something went wrong retrieving log lines from Mirakl from %1$tT %1$tF to %2$tT %2$tF"
					.formatted(startDate, endDate), e);
			mailNotificationUtil.sendPlainTextEmail(EMAIL_SUBJECT_MESSAGE,
					(ERROR_MESSAGE_PREFIX
							+ "Something went wrong getting transaction log information from %s to %s\n%s").formatted(
									convertToString(convertToLocalDateTime(startDate), DATE_FORMAT),
									convertToString(convertToLocalDateTime(endDate), DATE_FORMAT),
									MiraklLoggingErrorsUtil.stringify(e)));
			return List.of();
		}
	}

}
