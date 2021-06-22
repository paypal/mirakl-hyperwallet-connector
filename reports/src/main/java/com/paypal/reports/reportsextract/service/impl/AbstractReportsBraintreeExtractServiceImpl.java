package com.paypal.reports.reportsextract.service.impl;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.util.GraphQLClient;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.reports.reportsextract.model.HmcBraintreeTransactionLine;
import com.paypal.reports.reportsextract.model.graphql.braintree.paymentransaction.BraintreeTypeEnum;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public abstract class AbstractReportsBraintreeExtractServiceImpl<T extends HmcBraintreeTransactionLine> {

	protected static final String DATA = "data";

	protected static final String SEARCH = "search";

	protected static final String PAGE_INFO = "pageInfo";

	protected static final String END_CURSOR = "endCursor";

	protected static final String EDGES = "edges";

	protected static final String HAS_NEXT_PAGE = "hasNextPage";

	protected static final String NODE = "node";

	protected static final String STATUS = "status";

	protected static final String INPUT = "input";

	protected static final String IS = "is";

	protected static final String AFTER = "after";

	protected static final String GREATER_THAN_OR_EQUAL_TO = "greaterThanOrEqualTo";

	protected static final String LESS_THAN_OR_EQUAL_TO = "lessThanOrEqualTo";

	protected static final String CREATED_AT = "createdAt";

	// ISO-8601 format
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT;

	private final BraintreeGateway braintreeGateway;

	protected AbstractReportsBraintreeExtractServiceImpl(final BraintreeGateway braintreeGateway) {
		this.braintreeGateway = braintreeGateway;
	}

	protected abstract String getSearchQuery();

	protected abstract List<T> getEdges(BraintreeTypeEnum braintreeType, Map<String, Object> search);

	protected String getCursor(final BraintreeTypeEnum braintreeType, final Map<String, Object> result) {
		return (String) ((Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) result
				.get(DATA)).get(SEARCH)).get(braintreeType.getTypeName())).get(PAGE_INFO)).get(END_CURSOR);
	}

	protected Boolean getHasNextPage(final BraintreeTypeEnum braintreeType, final Map<String, Object> result) {
		return (Boolean) ((Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) result
				.get(DATA)).get(SEARCH)).get((braintreeType.getTypeName()))).get(PAGE_INFO)).get(HAS_NEXT_PAGE);
	}

	protected Map<String, Object> populateVars(final String transactionStatus, final Date startDate, final Date endDate,
			final String cursor) {

		final HashMap<String, Object> isTransactionStatus = new HashMap<>(
				Map.of(STATUS, (Map.of(IS, transactionStatus))));
		final HashMap<String, Object> vars = new HashMap<>(Map.of(INPUT, isTransactionStatus));

		if (Objects.nonNull(cursor)) {
			vars.put(AFTER, cursor);
		}

		final HashMap<String, Object> intervalDates = new HashMap<>();
		if (Objects.nonNull(startDate)) {
			intervalDates.put(GREATER_THAN_OR_EQUAL_TO, convertToISO8601(DateUtil.convertToLocalDateTime(startDate)));
		}

		if (Objects.nonNull(endDate)) {
			intervalDates.put(LESS_THAN_OR_EQUAL_TO, convertToISO8601(DateUtil.convertToLocalDateTime(endDate)));
		}

		if (!intervalDates.isEmpty()) {
			final Map<String, Object> input = (Map<String, Object>) vars.get(INPUT);
			input.put(CREATED_AT, intervalDates);
		}

		return vars;
	}

	private String convertToISO8601(final LocalDateTime date) {
		TemporalAccessor temp = date.atZone(ZoneId.systemDefault());
		return DATE_TIME_FORMATTER.format(temp);
	}

	protected GraphQLClient getGraphQLClient() {
		return braintreeGateway.graphQLClient;
	}

}
