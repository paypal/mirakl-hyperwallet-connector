package com.paypal.reports.services.impl;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.operator.domain.payment.MiraklTransactionsLines;
import com.mirakl.client.mmp.operator.domain.payment.transaction.MiraklTransactionLine;
import com.mirakl.client.mmp.operator.request.payment.transaction.MiraklTransactionLineRequest;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.reports.model.HmcMiraklTransactionLine;
import com.paypal.reports.services.ReportsMiraklExtractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.paypal.infrastructure.support.date.DateUtil.convertToLocalDateTime;
import static com.paypal.infrastructure.support.date.DateUtil.convertToString;

@Slf4j
@Service
public class ReportsMiraklExtractServiceImpl implements ReportsMiraklExtractService {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final String EMAIL_SUBJECT_MESSAGE = "Issue detected retrieving log lines from Mirakl";

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further information:\n";

	private final MiraklClient miraklClient;

	private final Converter<MiraklTransactionLine, HmcMiraklTransactionLine> miraklTransactionLogMiraklTransactionLineConverter;

	private final MailNotificationUtil mailNotificationUtil;

	public ReportsMiraklExtractServiceImpl(final MiraklClient miraklClient,
			final Converter<MiraklTransactionLine, HmcMiraklTransactionLine> miraklTransactionLogMiraklTransactionLineConverter,
			final MailNotificationUtil mailNotificationUtil) {
		this.miraklClient = miraklClient;
		this.miraklTransactionLogMiraklTransactionLineConverter = miraklTransactionLogMiraklTransactionLineConverter;
		this.mailNotificationUtil = mailNotificationUtil;
	}

	@SuppressWarnings("java:S1874")
	@Override
	public List<HmcMiraklTransactionLine> getAllTransactionLinesByDate(final Date startDate, final Date endDate) {
		log.info("Retrieving Mirakl transaction logs from {} to {}", startDate, endDate);

		try {
			final List<HmcMiraklTransactionLine> result = new ArrayList<>();
			String nextPageToken = StringUtils.EMPTY;

			final var miraklTransactionLineRequest = new MiraklTransactionLineRequest();
			miraklTransactionLineRequest.setDateCreatedFrom(startDate);
			miraklTransactionLineRequest.setDateCreatedTo(endDate);

			do {

				if (StringUtils.isNotBlank(nextPageToken)) {
					miraklTransactionLineRequest.setPageToken(nextPageToken);
				}

				final MiraklTransactionsLines transactionLines = miraklClient
					.getTransactionLines(miraklTransactionLineRequest);
				log.info("{} Mirakl transaction logs retrieved (page token: {})", transactionLines.getData().size(),
						nextPageToken);

				//@formatter:off
				Optional.ofNullable(transactionLines.getData())
						.orElse(List.of()).stream()
						.map(miraklTransactionLogMiraklTransactionLineConverter::convert)
						.forEach(result::add);
				//@formatter:on

				nextPageToken = transactionLines.getNextPageToken();
			}
			while (StringUtils.isNotBlank(nextPageToken));

			log.info("Total {} Mirakl transaction logs retrieved from {} to {}", result.size(), startDate, endDate);
			return result;
		}
		catch (final MiraklException e) {
			log.error("Something went wrong retrieving log lines from Mirakl from %1$tT %1$tF to %2$tT %2$tF"
				.formatted(startDate, endDate), e);
			mailNotificationUtil.sendPlainTextEmail(EMAIL_SUBJECT_MESSAGE,
					(ERROR_MESSAGE_PREFIX
							+ "Something went wrong getting transaction log information from %s to %s\n%s")
						.formatted(convertToString(convertToLocalDateTime(startDate), DATE_FORMAT),
								convertToString(convertToLocalDateTime(endDate), DATE_FORMAT),
								MiraklLoggingErrorsUtil.stringify(e)));
			return List.of();
		}
	}

}
