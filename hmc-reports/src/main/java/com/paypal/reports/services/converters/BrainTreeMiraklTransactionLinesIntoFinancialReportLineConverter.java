package com.paypal.reports.services.converters;

import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.reports.model.HmcBraintreeTransactionLine;
import com.paypal.reports.model.HmcFinancialReportLine;
import com.paypal.reports.model.HmcMiraklTransactionLine;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BrainTreeMiraklTransactionLinesIntoFinancialReportLineConverter
		implements Converter<Pair<HmcBraintreeTransactionLine, HmcMiraklTransactionLine>, HmcFinancialReportLine> {

	/**
	 * Method that retrieves a {@link Pair< HmcBraintreeTransactionLine ,
	 * HmcMiraklTransactionLine >} and returns a {@link HmcFinancialReportLine}
	 * @param source the source object {@link Pair< HmcBraintreeTransactionLine ,
	 * HmcMiraklTransactionLine >}
	 * @return the returned object {@link HmcFinancialReportLine}
	 */
	@Override
	public HmcFinancialReportLine convert(final Pair<HmcBraintreeTransactionLine, HmcMiraklTransactionLine> source) {

		final HmcBraintreeTransactionLine hmcBraintreeTransactionLine = source.getLeft();
		final HmcMiraklTransactionLine hmcMiraklTransactionLine = source.getRight();

		if (Objects.isNull(hmcBraintreeTransactionLine) || Objects.isNull(hmcMiraklTransactionLine)) {
			return null;
		}
		if (!hmcBraintreeTransactionLine.getPaymentTransactionId()
				.equals(hmcMiraklTransactionLine.getTransactionNumber())) {
			return null;
		}

		//@formatter:off
		return HmcFinancialReportLine.builder()
				.braintreeCommerceOrderId(hmcBraintreeTransactionLine.getOrderId())
				.miraklOrderId(hmcMiraklTransactionLine.getOrderId())
				.miraklSellerId(hmcMiraklTransactionLine.getSellerId())
				.miraklTransactionLineId(hmcMiraklTransactionLine.getTransactionLineId())
				.miraklTransactionTime(hmcMiraklTransactionLine.getTransactionTime())
				.miraklTransactionType(hmcMiraklTransactionLine.getTransactionType())
				.braintreeAmount(hmcBraintreeTransactionLine.getAmount())
				.miraklDebitAmount(hmcMiraklTransactionLine.getDebitAmount())
				.miraklCreditAmount(hmcMiraklTransactionLine.getCreditAmount())
				.currencyIsoCode(hmcBraintreeTransactionLine.getCurrencyIsoCode())
				.braintreeTransactionId(hmcBraintreeTransactionLine.getPaymentTransactionId())
				.braintreeTransactionTime(hmcBraintreeTransactionLine.getPaymentTransactionTime())
				.build();
		//@formatter:on
	}

}
