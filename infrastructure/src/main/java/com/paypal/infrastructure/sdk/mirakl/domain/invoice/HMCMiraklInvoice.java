package com.paypal.infrastructure.sdk.mirakl.domain.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;

public class HMCMiraklInvoice extends MiraklInvoice {

	@JsonProperty("summary")
	private HMCMiraklInvoiceSummary hmcSummary;

	@Override
	public HMCMiraklInvoiceSummary getSummary() {
		return this.hmcSummary;
	}

	public void setSummary(HMCMiraklInvoiceSummary summary) {
		this.hmcSummary = summary;
	}

}
