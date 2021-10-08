package com.paypal.infrastructure.sdk.mirakl.domain.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoices;

import java.util.List;

public class HMCMiraklInvoices extends MiraklInvoices {

	@JsonProperty("invoices")
	private List<HMCMiraklInvoice> hmcInvoices;

	public List<HMCMiraklInvoice> getHmcInvoices() {
		return hmcInvoices;
	}

	public void setHmcInvoices(final List<HMCMiraklInvoice> invoices) {
		this.hmcInvoices = invoices;
	}

}
