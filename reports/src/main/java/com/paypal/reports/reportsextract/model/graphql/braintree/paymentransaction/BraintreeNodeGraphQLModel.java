package com.paypal.reports.reportsextract.model.graphql.braintree.paymentransaction;

import lombok.Builder;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Builder
@Getter
public class BraintreeNodeGraphQLModel {

	String id;

	Date createdAt;

	String orderId;

	String status;

	BraintreeGraphQLAmountModel amount;

	List<Map<String, String>> customFields;

	public String getOrderId() {
		if (Objects.nonNull(orderId)) {
			return orderId;
		}

		final Map<String, String> customFieldsAsMap = Optional.ofNullable(customFields).orElse(List.of()).stream()
				.collect(Collectors.toMap(entry -> entry.get("name"), entry -> entry.get("value")));
		return customFieldsAsMap.get("order_id");
	}

}
