package com.paypal.reports.reportsextract.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class HmcMiraklTransactionLine {

	private String orderId;

	private String transactionNumber;

	private String sellerId;

	private String transactionLineId;

	private LocalDateTime transactionTime;

	private String transactionType;

	private BigDecimal amount;

	private BigDecimal creditAmount;

	private BigDecimal debitAmount;

	private String currencyIsoCode;

}
