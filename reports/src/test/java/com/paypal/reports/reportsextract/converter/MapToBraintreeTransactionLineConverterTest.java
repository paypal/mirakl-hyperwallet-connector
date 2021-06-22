package com.paypal.reports.reportsextract.converter;

import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.reports.reportsextract.model.HmcBraintreeTransactionLine;
import com.paypal.reports.reportsextract.model.graphql.braintree.paymentransaction.BraintreeGraphQLAmountModel;
import com.paypal.reports.reportsextract.model.graphql.braintree.paymentransaction.BraintreeNodeGraphQLModel;
import com.paypal.reports.reportsextract.model.graphql.braintree.paymentransaction.BraintreeTransactionTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapToBraintreeTransactionLineConverterTest {

	@Spy
	@InjectMocks
	private MapToBraintreeTransactionLineConverter testObj;

	@Mock
	private BraintreeNodeGraphQLModel braintreeNodeGraphQLModelMock;

	@Mock
	private BraintreeGraphQLAmountModel braintreeNodeGraphQLAmountModelMock;

	@Mock
	private Map<String, Object> edgeMock;

	@Test
	void convert_shouldReturnPopulatedHMCBraintreeTransactionLineWhenInputDataIsAValidEdgeMapWithOrderIdFilled() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2021, 5, 13, 6, 56, 3));
		final LocalDateTime createdAt = TimeMachine.now();

		BigDecimal expected = BigDecimal.valueOf(108.84);
		expected = expected.setScale(2);

		doReturn(braintreeNodeGraphQLModelMock).when(testObj).getBraintreeNodeGraphQLModel(edgeMock);
		when(braintreeNodeGraphQLModelMock.getOrderId()).thenReturn("854201000");
		when(edgeMock.get("legacyId")).thenReturn("dHJhbnNhY3Rpb25fbXZ5YXZxamo");
		when(braintreeNodeGraphQLModelMock.getAmount()).thenReturn(braintreeNodeGraphQLAmountModelMock);
		when(braintreeNodeGraphQLModelMock.getCreatedAt())
				.thenReturn(DateUtil.convertToDate(createdAt, ZoneId.systemDefault()));
		when(braintreeNodeGraphQLAmountModelMock.getValue()).thenReturn(expected);
		when(braintreeNodeGraphQLAmountModelMock.getCurrencyCode()).thenReturn("USD");

		final HmcBraintreeTransactionLine result = testObj.convert(edgeMock);

		assertThat(result.getAmount()).isEqualTo(expected);
		assertThat(result.getPaymentTransactionId()).isEqualTo("dHJhbnNhY3Rpb25fbXZ5YXZxamo");
		assertThat(result.getPaymentTransactionTime()).isEqualTo(createdAt);
		assertThat(result.getCurrencyIsoCode()).isEqualTo("USD");
		assertThat(result.getOrderId()).isEqualTo("854201000");
		assertThat(result.getTransactionType()).isEqualTo(BraintreeTransactionTypeEnum.OPERATOR_ORDER_AMOUNT.name());
	}

	@Test
	void convert_shouldReturnNullWhenInputMapIsNull() {
		doReturn(null).when(testObj).getBraintreeNodeGraphQLModel(null);

		final HmcBraintreeTransactionLine result = testObj.convert(null);

		assertThat(result).isNull();
	}

}
