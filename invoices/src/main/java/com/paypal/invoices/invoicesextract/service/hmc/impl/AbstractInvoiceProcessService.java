package com.paypal.invoices.invoicesextract.service.hmc.impl;

import com.paypal.invoices.invoicesextract.service.hmc.InvoiceProcessService;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;

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
