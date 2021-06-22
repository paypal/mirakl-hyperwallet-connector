package com.paypal.reports.reportsextract.model.graphql.braintree.paymentransaction;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class BraintreeGraphQLAmountModel {

	private final BigDecimal value;

	private final String currencyCode;

}
