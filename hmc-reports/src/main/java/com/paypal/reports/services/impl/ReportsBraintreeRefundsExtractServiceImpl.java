package com.paypal.reports.services.impl;

import com.braintreegateway.BraintreeGateway;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.reports.model.HmcBraintreeRefundLine;
import com.paypal.reports.model.graphql.braintree.paymentransaction.BraintreeTypeEnum;
import com.paypal.reports.services.ReportsBraintreeRefundsExtractService;
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
public class ReportsBraintreeRefundsExtractServiceImpl
		extends AbstractReportsBraintreeExtractServiceImpl<HmcBraintreeRefundLine>
		implements ReportsBraintreeRefundsExtractService {

	private final Converter<Map<String, Object>, HmcBraintreeRefundLine> mapToBraintreeRefundLineConverter;

	public ReportsBraintreeRefundsExtractServiceImpl(final BraintreeGateway braintreeGateway,
			final Converter<Map<String, Object>, HmcBraintreeRefundLine> mapToBraintreeRefundLineConverter) {
		super(braintreeGateway);
		this.mapToBraintreeRefundLineConverter = mapToBraintreeRefundLineConverter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<HmcBraintreeRefundLine> getAllRefundsByTypeAndDateInterval(final String transactionType,
			final Date startDate, final Date endDate) {
		log.info("Retrieving {} refunds from {} to {} from Braintree", transactionType, startDate, endDate);
		final List<HmcBraintreeRefundLine> allRefundsList = new ArrayList<>();
		boolean hasNextPage;
		String cursor = null;

		do {
			final Map<String, Object> result = getGraphQLClient().query(getSearchQuery(),
					populateVars(transactionType, startDate, endDate, cursor));
			hasNextPage = getHasNextPage(BraintreeTypeEnum.REFUNDS, result);
			cursor = getCursor(BraintreeTypeEnum.REFUNDS, result);
			final List<HmcBraintreeRefundLine> refundLines = getEdges(BraintreeTypeEnum.REFUNDS, result);
			allRefundsList.addAll(refundLines);
		}
		while (hasNextPage);

		log.info("Retrieved {} {} refunds from {} to {} from Braintree", allRefundsList.size(), transactionType,
				startDate, endDate);

		return allRefundsList;
	}

	protected List<HmcBraintreeRefundLine> getEdges(final BraintreeTypeEnum braintreeType,
			final Map<String, Object> search) {
		final List<Map<String, Object>> edges = (List<Map<String, Object>>) ((Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) search
				.get(DATA)).get(SEARCH)).get(braintreeType.getTypeName())).get(EDGES);

		//@formatter:off
		return edges.stream()
				.map(value -> (Map<String, Object>) value.get(NODE))
				.map(mapToBraintreeRefundLineConverter::convert)
				.collect(Collectors.toList());
		//@formatter:on
	}

	protected String getSearchQuery() {
		try {
			final InputStream refundSearchQueryInputStream = ReportsBraintreeRefundsExtractServiceImpl.class
					.getResourceAsStream("/refundsSearchQuery.graphql");
			return IOUtils.toString(refundSearchQueryInputStream, StandardCharsets.UTF_8.name());
		}
		catch (final IOException ex) {
			log.error("Impossible to access to [transactionSearchQuery.graphql] file", ex);
		}
		return StringUtils.EMPTY;
	}

}
