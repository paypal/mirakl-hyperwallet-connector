package com.paypal.reports.services.converters;

import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.reports.model.graphql.braintree.paymentransaction.BraintreeGraphQLAmountModel;
import com.paypal.reports.model.graphql.braintree.paymentransaction.BraintreeNodeGraphQLModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public abstract class AbstractMapToBraintreeLineConverter {

	// ISO-8601
	private static final String DATE_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	protected BraintreeNodeGraphQLModel getBraintreeNodeGraphQLModel(final Map<String, Object> source) {
		if (Objects.isNull(source)) {
			return null;
		}

		final String createdAt = (String) source.get("createdAt");
		final String orderId = (String) source.get("orderId");
		final String status = (String) source.get("status");
		final List<Map<String, String>> customFields = (List<Map<String, String>>) source.get("customFields");

		final Map<String, Object> amountMap = (Map<String, Object>) source.get("amount");
		final BigDecimal amountValue = new BigDecimal((String) amountMap.get("value"));
		final String amountCurrencyCode = (String) amountMap.get("currencyCode");

		//@formatter:off
		final BraintreeGraphQLAmountModel amount = BraintreeGraphQLAmountModel.builder()
				.value(amountValue)
				.currencyCode(amountCurrencyCode)
				.build();
		//@formatter:on

		//@formatter:off
		return BraintreeNodeGraphQLModel.builder()
				.createdAt(DateUtil.convertToDate(createdAt, DATE_FORMATTER, TimeZone.getDefault()))
				.orderId(orderId)
				.status(status)
				.amount(amount)
				.customFields(customFields)
				.build();
		//@formatter:on
	}

}
