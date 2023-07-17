package com.paypal.invoices.extractioninvoices.services;

import com.paypal.invoices.extractioncommons.services.HyperWalletPaymentExtractService;

/**
 * Abstract class that holds the {@link HyperWalletPaymentExtractService} for creating
 * payments based on invoices
 */
public abstract class AbstractInvoiceProcessService implements InvoiceProcessService {

	protected final HyperWalletPaymentExtractService hyperWalletPaymentExtractService;

	protected AbstractInvoiceProcessService(final HyperWalletPaymentExtractService hyperWalletPaymentExtractService) {
		this.hyperWalletPaymentExtractService = hyperWalletPaymentExtractService;
	}

}
