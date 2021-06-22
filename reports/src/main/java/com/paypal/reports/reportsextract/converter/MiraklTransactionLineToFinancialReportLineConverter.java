package com.paypal.reports.reportsextract.converter;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.reports.reportsextract.model.HmcFinancialReportLine;
import com.paypal.reports.reportsextract.model.HmcMiraklTransactionLine;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MiraklTransactionLineToFinancialReportLineConverter
		implements Converter<HmcMiraklTransactionLine, HmcFinancialReportLine> {

	/**
	 * Method that retrieves a {@link HmcMiraklTransactionLine} and returns a
	 * {@link HmcFinancialReportLine}
	 * @param source the source object {@link HmcMiraklTransactionLine}
	 * @return the returned object {@link HmcFinancialReportLine}
	 */
	@Override
	public HmcFinancialReportLine convert(final HmcMiraklTransactionLine source) {
		if (Objects.isNull(source)) {
			return null;
		}
		//@formatter:off
		return HmcFinancialReportLine.builder()
				.miraklOrderId(source.getOrderId())
				.miraklSellerId(source.getSellerId())
				.miraklTransactionLineId(source.getTransactionLineId())
				.miraklTransactionTime(source.getTransactionTime())
				.miraklTransactionType(source.getTransactionType())
				.miraklCreditAmount(source.getCreditAmount())
				.miraklDebitAmount(source.getDebitAmount())
				.currencyIsoCode(source.getCurrencyIsoCode())
				.build();
		//@formatter:on
	}

}
