package com.paypal.reports.services.impl;

import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.reports.model.HmcBraintreeTransactionLine;
import com.paypal.reports.model.HmcFinancialReportLine;
import com.paypal.reports.model.HmcMiraklTransactionLine;
import com.paypal.reports.services.FinancialReportConverterService;
import com.paypal.reports.services.FinancialReportService;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link FinancialReportService}
 */
@Data
@Service
public class FinancialReportConverterServiceImpl implements FinancialReportConverterService {

	private final Converter<HmcBraintreeTransactionLine, HmcFinancialReportLine> braintreeTransactionLineFinancialReportLineConverter;

	private final Converter<HmcMiraklTransactionLine, HmcFinancialReportLine> miraklTransactionLineFinancialReportServiceConverter;

	private final Converter<Pair<HmcBraintreeTransactionLine, HmcMiraklTransactionLine>, HmcFinancialReportLine> braintreeAndMiraklTransactionLineFinancialReportLineConverter;

	public FinancialReportConverterServiceImpl(
			final Converter<HmcBraintreeTransactionLine, HmcFinancialReportLine> braintreeTransactionLineFinancialReportLineConverter,
			final Converter<HmcMiraklTransactionLine, HmcFinancialReportLine> miraklTransactionLineFinancialReportServiceConverter,
			final Converter<Pair<HmcBraintreeTransactionLine, HmcMiraklTransactionLine>, HmcFinancialReportLine> braintreeAndMiraklTransactionLineFinancialReportLineConverter) {
		this.braintreeTransactionLineFinancialReportLineConverter = braintreeTransactionLineFinancialReportLineConverter;
		this.miraklTransactionLineFinancialReportServiceConverter = miraklTransactionLineFinancialReportServiceConverter;
		this.braintreeAndMiraklTransactionLineFinancialReportLineConverter = braintreeAndMiraklTransactionLineFinancialReportLineConverter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HmcFinancialReportLine convertBraintreeTransactionLineIntoFinancialReportLine(
			final HmcBraintreeTransactionLine hmcBraintreeTransactionLine) {
		return braintreeTransactionLineFinancialReportLineConverter.convert(hmcBraintreeTransactionLine);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HmcFinancialReportLine convertMiraklTransactionLineIntoFinancialReportLine(
			final HmcMiraklTransactionLine hmcMiraklTransactionLine) {
		return miraklTransactionLineFinancialReportServiceConverter.convert(hmcMiraklTransactionLine);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HmcFinancialReportLine convertBrainTreeAndMiraklTransactionLineIntoFinancialReportLine(
			final HmcBraintreeTransactionLine hmcBraintreeTransactionLine,
			final HmcMiraklTransactionLine hmcMiraklTransactionLine) {
		return braintreeAndMiraklTransactionLineFinancialReportLineConverter
				.convert(Pair.of(hmcBraintreeTransactionLine, hmcMiraklTransactionLine));
	}

}
