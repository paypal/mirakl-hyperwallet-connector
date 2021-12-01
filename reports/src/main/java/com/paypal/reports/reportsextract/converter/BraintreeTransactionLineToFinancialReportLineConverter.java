package com.paypal.reports.reportsextract.converter;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.reports.reportsextract.model.HmcBraintreeTransactionLine;
import com.paypal.reports.reportsextract.model.HmcFinancialReportLine;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BraintreeTransactionLineToFinancialReportLineConverter
		implements Converter<HmcBraintreeTransactionLine, HmcFinancialReportLine> {

	/**
	 * Method that retrieves a {@link HmcBraintreeTransactionLine} and returns a
	 * {@link HmcFinancialReportLine}
	 * @param source the source object {@link HmcBraintreeTransactionLine}
	 * @return the returned object {@link HmcFinancialReportLine}
	 */
	@Override
	public HmcFinancialReportLine convert(final HmcBraintreeTransactionLine source) {
		if (Objects.isNull(source)) {
			return null;
		}
		//@formatter:off
		return HmcFinancialReportLine.builder()
				.braintreeCommerceOrderId(source.getOrderId())
				.braintreeAmount(source.getAmount())
				.currencyIsoCode(source.getCurrencyIsoCode())
				.miraklTransactionType(source.getTransactionType())
				.braintreeTransactionId(source.getPaymentTransactionId())
				.braintreeTransactionTime(source.getPaymentTransactionTime())
				.build();
		//@formatter:on
	}

}
