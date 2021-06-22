package com.paypal.invoices.invoicesextract.service.mirakl;

import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

public interface MiraklAccountingDocumentExtractService<T extends AccountingDocumentModel> {

	/**
	 * Extracts the {@link MiraklInvoice} data from Mirakl environment
	 * @param delta Optional parameter to filter all invoices that have been modified
	 * since this parameter value
	 * @return a {@link List} of {@link InvoiceModel}
	 */
	List<T> extractAccountingDocument(@Nullable Date delta);

}
