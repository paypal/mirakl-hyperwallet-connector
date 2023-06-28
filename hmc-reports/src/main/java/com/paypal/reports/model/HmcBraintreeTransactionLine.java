package com.paypal.reports.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class HmcBraintreeTransactionLine {

	protected String orderId;

	protected String paymentTransactionId;

	protected LocalDateTime paymentTransactionTime;

	protected String transactionType;

	protected String currencyIsoCode;

	protected BigDecimal amount;

}
