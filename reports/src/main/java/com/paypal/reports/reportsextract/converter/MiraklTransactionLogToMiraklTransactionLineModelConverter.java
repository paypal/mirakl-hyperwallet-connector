package com.paypal.reports.reportsextract.converter;

import com.mirakl.client.mmp.domain.payment.MiraklTransactionLog;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.reports.reportsextract.model.HmcMiraklTransactionLine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Converts {@link MiraklTransactionLog} to {@link HmcMiraklTransactionLine}
 */
@Slf4j
@Service
public class MiraklTransactionLogToMiraklTransactionLineModelConverter
		implements Converter<MiraklTransactionLog, HmcMiraklTransactionLine> {

	@Override
	public HmcMiraklTransactionLine convert(final MiraklTransactionLog source) {
		if (Objects.isNull(source)) {
			log.warn("Input MiraklTransactionLog is null");
			return null;
		}
		//@formatter:off
		return HmcMiraklTransactionLine.builder()
				.orderId(source.getOrderId())
				.creditAmount(source.getAmountCredited())
				.debitAmount(source.getAmountDebited())
				.amount(source.getAmount())
				.transactionTime(Objects.nonNull(source.getDateCreated()) ? DateUtil.convertToLocalDateTime(source.getDateCreated()) : null)
				.transactionNumber(source.getTransactionNumber())
				.transactionType(String.valueOf(source.getTransactionType()))
				.transactionLineId(source.getId())
				.currencyIsoCode(String.valueOf(source.getCurrencyIsoCode()))
				.sellerId(source.getShopId())
				.build();
		//@formatter:on
	}

}
