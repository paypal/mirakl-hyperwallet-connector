package com.paypal.infrastructure.sdk.mirakl.domain.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoiceSummary;

import java.math.BigDecimal;

public class HMCMiraklInvoiceSummary extends MiraklInvoiceSummary {

	@JsonProperty("amount_transferred_to_operator")
	private BigDecimal amountTransferredToOperator;

	public BigDecimal getAmountTransferredToOperator() {
		return amountTransferredToOperator;
	}

	public void setAmountTransferredToOperator(final BigDecimal amountTransferredToOperator) {
		this.amountTransferredToOperator = amountTransferredToOperator;
	}

}
