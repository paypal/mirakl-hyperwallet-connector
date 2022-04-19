package com.paypal.invoices.invoicesextract.service.hmc;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;

/**
 * Service that provides functionality for paying sellers.
 */
public interface InvoiceProcessService {

	/**
	 * Execute pay invoice operator in HW for the given invoice.
	 * @param invoice a {@link InvoiceModel}.
	 * @return a {@link HyperwalletPayment}.
	 */
	HyperwalletPayment payOperator(final InvoiceModel invoice);

}
