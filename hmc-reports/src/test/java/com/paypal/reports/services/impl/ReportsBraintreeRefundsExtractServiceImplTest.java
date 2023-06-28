package com.paypal.reports.services.impl;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.util.GraphQLClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.reports.model.HmcBraintreeRefundLine;
import com.paypal.reports.model.HmcBraintreeTransactionLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportsBraintreeRefundsExtractServiceImplTest {

	private MyReportsBraintreeTransactionsRefunds testObj;

	// ISO-8601 format
	private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_INSTANT;

	private String refundSearchQuery;

	@Mock
	private BraintreeGateway braintreeGatewayMock;

	@Mock
	private Converter<Map<String, Object>, HmcBraintreeRefundLine> mapToBraintreeRefundLineConverterMock;

	@Mock
	private GraphQLClient graphQLClientMock;

	@BeforeEach
	void setUp() {
		testObj = Mockito.spy(
				new MyReportsBraintreeTransactionsRefunds(braintreeGatewayMock, mapToBraintreeRefundLineConverterMock));
		refundSearchQuery = Paths.get("src", "test", "resources", "graphQLRefundsSearchQuery.graphql").toFile()
				.toString();
	}

	@Test
	void getAllRefundsByTypeAndDateInterval_shouldReturnTwoRefundsWhenGraphQLQueryReturnsTwoNodesOnOnePage()
			throws IOException {
		final Date startDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 19, 12, 30, 22),
				ZoneId.systemDefault());
		final Date endDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 20, 12, 0, 22), ZoneId.systemDefault());
		doReturn(graphQLClientMock).when(testObj).getGraphQLClient();
		final Map<String, Object> graphQLQueryResponseMap = new ObjectMapper().readValue(
				Paths.get("src", "test", "resources", "graphQLOnePageRefundQueryResponse.json").toFile(), Map.class);
		final Map<String, Object> graphQLFirstEdge = new ObjectMapper().readValue(
				Paths.get("src", "test", "resources", "graphQLOnePageRefundFirstEdge.json").toFile(), Map.class);
		final Map<String, Object> graphQLSecondEdge = new ObjectMapper().readValue(
				Paths.get("src", "test", "resources", "graphQLOnePageRefundSecondEdge.json").toFile(), Map.class);
		when(graphQLClientMock.query(refundSearchQuery, createInputVars("SETTLED", startDate, endDate, null)))
				.thenReturn(graphQLQueryResponseMap);
		final HmcBraintreeRefundLine firstRefund = HmcBraintreeRefundLine.builder().paymentTransactionId("firstRefund")
				.build();
		when(mapToBraintreeRefundLineConverterMock.convert(graphQLFirstEdge)).thenReturn(firstRefund);

		final HmcBraintreeRefundLine secondRefund = HmcBraintreeRefundLine.builder()
				.paymentTransactionId("secondRefund").build();
		when(mapToBraintreeRefundLineConverterMock.convert(graphQLSecondEdge)).thenReturn(secondRefund);

		final List<HmcBraintreeRefundLine> result = testObj.getAllRefundsByTypeAndDateInterval("SETTLED", startDate,
				endDate);

		assertThat(result.stream().map(HmcBraintreeRefundLine::getPaymentTransactionId))
				.containsExactlyInAnyOrder("firstRefund", "secondRefund");
		verify(mapToBraintreeRefundLineConverterMock, times(2)).convert(any());
	}

	@Test
	void getAllRefundsByTypeAndDateInterval_shouldReturnTwoRefundsWhenGraphQLQueryReturnsOneNodePerPagePage()
			throws IOException {
		final Date startDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 19, 12, 30, 22),
				ZoneId.systemDefault());
		final Date endDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 20, 12, 0, 22), ZoneId.systemDefault());
		doReturn(graphQLClientMock).when(testObj).getGraphQLClient();
		final Map<String, Object> graphQLFirstPageQueryResponseMap = new ObjectMapper().readValue(
				Paths.get("src", "test", "resources", "graphQLTwoPagesRefundQueryResponseOne.json").toFile(),
				Map.class);
		final Map<String, Object> graphQLSecondPageQueryResponseMap = new ObjectMapper().readValue(
				Paths.get("src", "test", "resources", "graphQLTwoPagesRefundQueryResponseTwo.json").toFile(),
				Map.class);
		final Map<String, Object> graphQLFirstEdge = new ObjectMapper().readValue(
				Paths.get("src", "test", "resources", "graphQLOnePageRefundFirstEdge.json").toFile(), Map.class);
		final Map<String, Object> graphQLSecondEdge = new ObjectMapper().readValue(
				Paths.get("src", "test", "resources", "graphQLOnePageRefundSecondEdge.json").toFile(), Map.class);
		when(graphQLClientMock.query(refundSearchQuery, createInputVars("SETTLED", startDate, endDate, null)))
				.thenReturn(graphQLFirstPageQueryResponseMap);
		when(graphQLClientMock.query(refundSearchQuery,
				createInputVars("SETTLED", startDate, endDate,
						"ZEhKaGJuTmhZM1JwYjI1ZmJYWjVZWFp4YW1vOzIwMjEtMDUtMTNUMDY6NTY6MDNa")))
								.thenReturn(graphQLSecondPageQueryResponseMap);
		final HmcBraintreeRefundLine firstRefund = HmcBraintreeRefundLine.builder().paymentTransactionId("firstRefund")
				.build();
		when(mapToBraintreeRefundLineConverterMock.convert(graphQLFirstEdge)).thenReturn(firstRefund);
		final HmcBraintreeRefundLine secondRefund = HmcBraintreeRefundLine.builder()
				.paymentTransactionId("secondRefund").build();
		when(mapToBraintreeRefundLineConverterMock.convert(graphQLSecondEdge)).thenReturn(secondRefund);

		final List<HmcBraintreeRefundLine> result = testObj.getAllRefundsByTypeAndDateInterval("SETTLED", startDate,
				endDate);

		assertThat(result.stream().map(HmcBraintreeTransactionLine::getPaymentTransactionId))
				.containsExactlyInAnyOrder("firstRefund", "secondRefund");
		verify(mapToBraintreeRefundLineConverterMock, times(2)).convert(any());
	}

	@Test
	void getAllRefundsByTypeAndDateInterval_shouldReturnAnEmptyListOfRefunds() throws IOException {
		final Date startDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 19, 12, 30, 22),
				ZoneId.systemDefault());
		final Date endDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 20, 12, 0, 22), ZoneId.systemDefault());
		doReturn(graphQLClientMock).when(testObj).getGraphQLClient();
		final Map<String, Object> emptyResponse = new ObjectMapper().readValue(
				Paths.get("src", "test", "resources", "graphQLEmptyRefundResponse.json").toFile(), Map.class);
		when(graphQLClientMock.query(refundSearchQuery, createInputVars("SETTLED", startDate, endDate, null)))
				.thenReturn(emptyResponse);

		final List<HmcBraintreeRefundLine> result = testObj.getAllRefundsByTypeAndDateInterval("SETTLED", startDate,
				endDate);

		assertThat(result).isEmpty();
	}

	Map<String, Object> createInputVars(final String transactionType, final Date startDate, final Date endDate,
			final String cursor) {
		final HashMap<String, Object> isTransactionType = new HashMap<>(
				Map.of("status", (Map.of("is", transactionType))));
		final HashMap<String, Object> vars = new HashMap<>(Map.of("input", isTransactionType));

		if (cursor != null) {
			vars.put("after", cursor);
		}

		final HashMap<String, Object> intervalDates = new HashMap<>();
		if (startDate != null) {
			intervalDates.put("greaterThanOrEqualTo", convertToISO8601(DateUtil.convertToLocalDateTime(startDate)));
		}

		if (endDate != null) {
			intervalDates.put("lessThanOrEqualTo", convertToISO8601(DateUtil.convertToLocalDateTime(endDate)));
		}

		if (!intervalDates.isEmpty()) {
			final Map<String, Object> input = (Map<String, Object>) vars.get("input");
			input.put("createdAt", intervalDates);
		}

		return vars;
	}

	private String convertToISO8601(final LocalDateTime date) {
		final TemporalAccessor temp = date.atZone(ZoneId.systemDefault());
		return DATE_FORMATTER.format(temp);
	}

	private class MyReportsBraintreeTransactionsRefunds extends ReportsBraintreeRefundsExtractServiceImpl {

		public MyReportsBraintreeTransactionsRefunds(final BraintreeGateway braintreeGateway,
				final Converter<Map<String, Object>, HmcBraintreeRefundLine> mapToBraintreeRefundLineConverter) {
			super(braintreeGateway, mapToBraintreeRefundLineConverter);
		}

		@Override
		protected GraphQLClient getGraphQLClient() {
			return graphQLClientMock;
		}

		@Override
		protected String getSearchQuery() {
			return refundSearchQuery;
		}

	}

}
