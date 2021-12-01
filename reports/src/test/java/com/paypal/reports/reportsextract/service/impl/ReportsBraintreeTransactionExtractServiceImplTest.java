package com.paypal.reports.reportsextract.service.impl;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.util.GraphQLClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.reports.reportsextract.model.HmcBraintreeTransactionLine;
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
class ReportsBraintreeTransactionExtractServiceImplTest {

	// ISO-8601 format
	private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_INSTANT;

	private String transactionSearchQuery;

	private MyReportsBraintreeTransactionsExtractServiceImpl testObj;

	@Mock
	private BraintreeGateway braintreeGatewayMock;

	@Mock
	private Converter<Map<String, Object>, HmcBraintreeTransactionLine> mapToBraintreeTransactionLineConverterMock;

	@Mock
	private GraphQLClient graphQLClientMock;

	@BeforeEach
	void setUp() {
		testObj = Mockito.spy(new MyReportsBraintreeTransactionsExtractServiceImpl(braintreeGatewayMock,
				mapToBraintreeTransactionLineConverterMock));
		transactionSearchQuery = Paths.get("src", "test", "resources", "graphQLTransactionSearchQuery.graphql").toFile()
				.toString();
	}

	@Test
	void getAllTransactionsByTypeAndDateInterval_shouldReturnTwoTransactionsWhenGraphQLQueryReturnsTwoNodesOnOnePage()
			throws IOException {
		final Date startDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 19, 12, 30, 22),
				ZoneId.systemDefault());
		final Date endDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 20, 12, 0, 22), ZoneId.systemDefault());
		doReturn(graphQLClientMock).when(testObj).getGraphQLClient();
		final Map<String, Object> graphQLQueryResponseMap = new ObjectMapper().readValue(
				Paths.get("src", "test", "resources", "graphQLOnePageQueryResponse.json").toFile(), Map.class);
		final Map<String, Object> graphQLFirstEdge = new ObjectMapper()
				.readValue(Paths.get("src", "test", "resources", "graphQLOnePageFirstEdge.json").toFile(), Map.class);
		final Map<String, Object> graphQLSecondEdge = new ObjectMapper()
				.readValue(Paths.get("src", "test", "resources", "graphQLOnePageSecondEdge.json").toFile(), Map.class);
		when(graphQLClientMock.query(transactionSearchQuery, createInputVars("SETTLED", startDate, endDate, null)))
				.thenReturn(graphQLQueryResponseMap);
		final HmcBraintreeTransactionLine firstTransaction = HmcBraintreeTransactionLine.builder()
				.paymentTransactionId("firstTransaction").build();
		when(mapToBraintreeTransactionLineConverterMock.convert(graphQLFirstEdge)).thenReturn(firstTransaction);

		final HmcBraintreeTransactionLine secondTransaction = HmcBraintreeTransactionLine.builder()
				.paymentTransactionId("secondTransaction").build();
		when(mapToBraintreeTransactionLineConverterMock.convert(graphQLSecondEdge)).thenReturn(secondTransaction);

		final List<HmcBraintreeTransactionLine> result = testObj.getAllTransactionsByTypeAndDateInterval("SETTLED",
				startDate, endDate);

		assertThat(result.stream().map(HmcBraintreeTransactionLine::getPaymentTransactionId))
				.containsExactlyInAnyOrder("firstTransaction", "secondTransaction");
		verify(mapToBraintreeTransactionLineConverterMock, times(2)).convert(any());
	}

	@Test
	void getAllTransactionsByTypeAndDateInterval_shouldReturnTwoTransactionsWhenGraphQLQueryReturnsOneNodePerPagePage()
			throws IOException {
		final Date startDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 19, 12, 30, 22),
				ZoneId.systemDefault());
		final Date endDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 20, 12, 0, 22), ZoneId.systemDefault());
		doReturn(graphQLClientMock).when(testObj).getGraphQLClient();
		final Map<String, Object> graphQLFirstPageQueryResponseMap = new ObjectMapper().readValue(
				Paths.get("src", "test", "resources", "graphQLTwoPagesQueryResponseOne.json").toFile(), Map.class);
		final Map<String, Object> graphQLSecondPageQueryResponseMap = new ObjectMapper().readValue(
				Paths.get("src", "test", "resources", "graphQLTwoPagesQueryResponseTwo.json").toFile(), Map.class);
		final Map<String, Object> graphQLFirstEdge = new ObjectMapper()
				.readValue(Paths.get("src", "test", "resources", "graphQLOnePageFirstEdge.json").toFile(), Map.class);
		final Map<String, Object> graphQLSecondEdge = new ObjectMapper()
				.readValue(Paths.get("src", "test", "resources", "graphQLOnePageSecondEdge.json").toFile(), Map.class);
		when(graphQLClientMock.query(transactionSearchQuery, createInputVars("SETTLED", startDate, endDate, null)))
				.thenReturn(graphQLFirstPageQueryResponseMap);
		when(graphQLClientMock.query(transactionSearchQuery,
				createInputVars("SETTLED", startDate, endDate,
						"ZEhKaGJuTmhZM1JwYjI1ZmJYWjVZWFp4YW1vOzIwMjEtMDUtMTNUMDY6NTY6MDNa")))
								.thenReturn(graphQLSecondPageQueryResponseMap);
		final HmcBraintreeTransactionLine firstTransaction = HmcBraintreeTransactionLine.builder()
				.paymentTransactionId("firstTransaction").build();
		when(mapToBraintreeTransactionLineConverterMock.convert(graphQLFirstEdge)).thenReturn(firstTransaction);
		final HmcBraintreeTransactionLine secondTransaction = HmcBraintreeTransactionLine.builder()
				.paymentTransactionId("secondTransaction").build();
		when(mapToBraintreeTransactionLineConverterMock.convert(graphQLSecondEdge)).thenReturn(secondTransaction);

		final List<HmcBraintreeTransactionLine> result = testObj.getAllTransactionsByTypeAndDateInterval("SETTLED",
				startDate, endDate);

		assertThat(result.stream().map(HmcBraintreeTransactionLine::getPaymentTransactionId))
				.containsExactlyInAnyOrder("firstTransaction", "secondTransaction");
		verify(mapToBraintreeTransactionLineConverterMock, times(2)).convert(any());
	}

	@Test
	void getAllTransactionsByTypeAndDateInterval_shouldReturnAnEmptyListOfTransactions() throws IOException {
		final Date startDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 19, 12, 30, 22),
				ZoneId.systemDefault());
		final Date endDate = DateUtil.convertToDate(LocalDateTime.of(2021, 5, 20, 12, 0, 22), ZoneId.systemDefault());
		doReturn(graphQLClientMock).when(testObj).getGraphQLClient();
		final Map<String, Object> emptyResponse = new ObjectMapper()
				.readValue(Paths.get("src", "test", "resources", "graphQLEmptyResponse.json").toFile(), Map.class);
		when(graphQLClientMock.query(transactionSearchQuery, createInputVars("SETTLED", startDate, endDate, null)))
				.thenReturn(emptyResponse);

		final List<HmcBraintreeTransactionLine> result = testObj.getAllTransactionsByTypeAndDateInterval("SETTLED",
				startDate, endDate);

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
		TemporalAccessor temp = date.atZone(ZoneId.systemDefault());
		return DATE_FORMATTER.format(temp);
	}

	private class MyReportsBraintreeTransactionsExtractServiceImpl
			extends ReportsBraintreeTransactionsExtractServiceImpl {

		public MyReportsBraintreeTransactionsExtractServiceImpl(final BraintreeGateway braintreeGateway,
				final Converter<Map<String, Object>, HmcBraintreeTransactionLine> mapToBraintreeTransactionLineConverter) {
			super(braintreeGateway, mapToBraintreeTransactionLineConverter);
		}

		@Override
		protected GraphQLClient getGraphQLClient() {
			return graphQLClientMock;
		}

		@Override
		protected String getSearchQuery() {
			return transactionSearchQuery;
		}

	}

}
