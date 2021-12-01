package com.paypal.reports.reportsextract.model.graphql.braintree.paymentransaction;

import lombok.Getter;

@Getter
public enum BraintreeTypeEnum {

	TRANSACTIONS("transactions"), REFUNDS("refunds");

	private final String typeName;

	BraintreeTypeEnum(final String type) {
		this.typeName = type;
	}

}
