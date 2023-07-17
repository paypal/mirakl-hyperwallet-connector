package com.paypal.invoices.extractioncommons.services;

import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface MiraklAccountingDocumentExtractService<T extends AccountingDocumentModel> {

	/**
	 * Extracts the {@link MiraklInvoice} data from Mirakl environment
	 * @param delta Optional parameter to filter all invoices that have been modified
	 * since this parameter value
	 * @return a {@link List} of {@link InvoiceModel}
	 */
	List<T> extractAccountingDocuments(@Nullable Date delta);

	/**
	 * Extracts the {@link MiraklInvoice} data from Mirakl environment
	 * @param delta Optional parameter to filter all invoices that have been modified
	 * @param includePaid Optional parameter to filter all invoices that have been paid
	 * since this parameter value
	 * @return a {@link List} of {@link InvoiceModel}
	 */
	List<T> extractAccountingDocuments(@Nullable Date delta, boolean includePaid);

	/**
	 * Extracts the {@link MiraklInvoice} data from Mirakl environment
	 * @param ids List of identifiers of the invoices to be returned
	 * @return a {@link List} of {@link InvoiceModel}
	 */
	Collection<T> extractAccountingDocuments(List<String> ids);

}
