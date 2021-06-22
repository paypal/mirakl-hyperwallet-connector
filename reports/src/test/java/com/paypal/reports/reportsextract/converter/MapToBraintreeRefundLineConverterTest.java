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
class MapToBraintreeRefundLineConverterTest {

	@Spy
	@InjectMocks
	private MapToBraintreeRefundLineConverter testObj;

	@Mock
	private BraintreeNodeGraphQLModel braintreeNodeGraphQLModelMock;

	@Mock
	private Map<String, Object> edgeMock;

	@Mock
	private BraintreeGraphQLAmountModel braintreeNodeGraphQLAmountModelMock;

	@Test
	void convert_shouldReturnPopulatedHMCBraintreeTransactionLineWhenInputDataIsAValidEdgeMapWithOrderIdFilled() {
		BigDecimal expected = BigDecimal.valueOf(20.00);
		expected = expected.setScale(2);
		TimeMachine.useFixedClockAt(LocalDateTime.of(2021, 5, 13, 6, 56, 3));
		final LocalDateTime createdAt = TimeMachine.now();

		doReturn(braintreeNodeGraphQLModelMock).when(testObj).getBraintreeNodeGraphQLModel(edgeMock);
		when(braintreeNodeGraphQLModelMock.getOrderId()).thenReturn("871633000");
		when(edgeMock.get("legacyId")).thenReturn("cmVmdW5kXzhod3k1ZGZ0");
		when(braintreeNodeGraphQLModelMock.getAmount()).thenReturn(braintreeNodeGraphQLAmountModelMock);
		when(braintreeNodeGraphQLModelMock.getCreatedAt())
				.thenReturn(DateUtil.convertToDate(createdAt, ZoneId.systemDefault()));
		when(braintreeNodeGraphQLAmountModelMock.getValue()).thenReturn(expected);
		when(braintreeNodeGraphQLAmountModelMock.getCurrencyCode()).thenReturn("USD");

		final HmcBraintreeTransactionLine result = testObj.convert(edgeMock);

		assertThat(result.getAmount()).isEqualTo(expected.negate());
		assertThat(result.getPaymentTransactionId()).isEqualTo("cmVmdW5kXzhod3k1ZGZ0");
		assertThat(result.getPaymentTransactionTime()).isEqualTo(createdAt);
		assertThat(result.getCurrencyIsoCode()).isEqualTo("USD");
		assertThat(result.getOrderId()).isEqualTo("871633000");
		assertThat(result.getTransactionType())
				.isEqualTo(BraintreeTransactionTypeEnum.REFUND_OPERATOR_ORDER_AMOUNT.name());
	}

	@Test
	void convert_shouldReturnNullWhenInputMapIsNull() {
		final HmcBraintreeTransactionLine result = testObj.convert(null);

		assertThat(result).isNull();
	}

}
