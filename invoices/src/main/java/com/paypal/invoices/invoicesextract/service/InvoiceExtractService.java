package com.paypal.invoices.invoicesextract.service;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;

import java.util.Date;
import java.util.List;

/**
 * Service that orchestrates the flow to extract invoices from mirakl and create them in
 * hyperwallet
 */

public interface InvoiceExtractService {

	/**
	 * Extracts the {@link MiraklInvoice} data from Mirakl environment
	 * @param delta Optional parameter to filter all shops that have been modified since
	 * this parameter value and creates the into hyperwallet. Returns a list
	 * {@link HyperwalletPayment} successfully created in hyperwallet
	 * @return a {@link List} of {@link HyperwalletPayment}
	 */
	List<HyperwalletPayment> extractInvoices(Date delta);

	/**
	 * Extracts the credit notes as {@link MiraklInvoice} data from Mirakl environment
	 * @param delta Optional parameter to filter all shops that have been modified since
	 * this parameter value and creates the into hyperwallet. Returns a list
	 * {@link HyperwalletPayment} successfully created in hyperwallet
	 * @return a {@link List} of {@link HyperwalletPayment}
	 */
	List<HyperwalletPayment> extractCreditNotes(Date delta);

}
