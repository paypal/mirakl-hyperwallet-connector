package com.paypal.reports.services.impl;

import com.braintreegateway.BraintreeGateway;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.reports.model.HmcBraintreeTransactionLine;
import com.paypal.reports.model.graphql.braintree.paymentransaction.BraintreeTypeEnum;
import com.paypal.reports.services.ReportsBraintreeTransactionsExtractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportsBraintreeTransactionsExtractServiceImpl
		extends AbstractReportsBraintreeExtractServiceImpl<HmcBraintreeTransactionLine>
		implements ReportsBraintreeTransactionsExtractService {

	protected final Converter<Map<String, Object>, HmcBraintreeTransactionLine> mapToBraintreeTransactionLineConverter;

	public ReportsBraintreeTransactionsExtractServiceImpl(final BraintreeGateway braintreeGateway,
			final Converter<Map<String, Object>, HmcBraintreeTransactionLine> mapToBraintreeTransactionLineConverter) {
		super(braintreeGateway);
		this.mapToBraintreeTransactionLineConverter = mapToBraintreeTransactionLineConverter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<HmcBraintreeTransactionLine> getAllTransactionsByTypeAndDateInterval(final String transactionType,
			final Date startDate, final Date endDate) {
		log.info("Retrieving {} transactions from {} to {} from Braintree", transactionType, startDate, endDate);
		final List<HmcBraintreeTransactionLine> allTransactionsList = new ArrayList<>();
		boolean hasNextPage;
		String cursor = null;

		do {
			final Map<String, Object> result = getGraphQLClient().query(getSearchQuery(),
					populateVars(transactionType, startDate, endDate, cursor));
			hasNextPage = getHasNextPage(BraintreeTypeEnum.TRANSACTIONS, result);
			cursor = getCursor(BraintreeTypeEnum.TRANSACTIONS, result);
			final List<HmcBraintreeTransactionLine> transactionLines = getEdges(BraintreeTypeEnum.TRANSACTIONS, result);
			allTransactionsList.addAll(transactionLines);
		}
		while (hasNextPage);

		log.info("Retrieved {} {} transactions from {} to {} from Braintree", allTransactionsList.size(),
				transactionType, startDate, endDate);

		return allTransactionsList;
	}

	protected List<HmcBraintreeTransactionLine> getEdges(final BraintreeTypeEnum braintreeType,
			final Map<String, Object> search) {
		final List<Map<String, Object>> edges = (List<Map<String, Object>>) ((Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) search
				.get(DATA)).get(SEARCH)).get(braintreeType.getTypeName())).get(EDGES);

		//@formatter:off
		return edges.stream()
				.map(value -> (Map<String, Object>) value.get(NODE))
				.map(mapToBraintreeTransactionLineConverter::convert)
				.collect(Collectors.toList());
		//@formatter:on
	}

	protected String getSearchQuery() {
		try {
			final InputStream transactionSearchQueryInputStream = ReportsBraintreeTransactionsExtractServiceImpl.class
					.getResourceAsStream("/transactionSearchQuery.graphql");
			return IOUtils.toString(transactionSearchQueryInputStream, StandardCharsets.UTF_8.name());
		}
		catch (final IOException ex) {
			log.error("Impossible to access to [transactionSearchQuery.graphql] file", ex);
		}
		return StringUtils.EMPTY;
	}

}
