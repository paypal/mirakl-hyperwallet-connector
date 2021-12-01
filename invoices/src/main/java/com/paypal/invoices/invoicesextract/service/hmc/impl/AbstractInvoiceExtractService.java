package com.paypal.invoices.invoicesextract.service.hmc.impl;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.InvoiceExtractService;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklAccountingDocumentExtractService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract class that holds the logic to call hyperwallet and mirakl to retrieve and
 * create payments based on invoices
 */
public abstract class AbstractInvoiceExtractService implements InvoiceExtractService {

	protected final MiraklAccountingDocumentExtractService<InvoiceModel> miraklAccountingDocumentInvoicesExtractService;

	protected final MiraklAccountingDocumentExtractService<CreditNoteModel> miraklAccountingDocumentCreditNotesExtractService;

	protected final HyperWalletPaymentExtractService hyperWalletPaymentExtractService;

	protected AbstractInvoiceExtractService(
			final MiraklAccountingDocumentExtractService<InvoiceModel> miraklAccountingDocumentInvoicesExtractService,
			final MiraklAccountingDocumentExtractService<CreditNoteModel> miraklAccountingDocumentCreditNotesExtractService,
			final HyperWalletPaymentExtractService hyperWalletPaymentExtractService) {
		this.miraklAccountingDocumentInvoicesExtractService = miraklAccountingDocumentInvoicesExtractService;
		this.miraklAccountingDocumentCreditNotesExtractService = miraklAccountingDocumentCreditNotesExtractService;
		this.hyperWalletPaymentExtractService = hyperWalletPaymentExtractService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<HyperwalletPayment> extractInvoices(final Date delta) {
		final List<InvoiceModel> invoices = miraklAccountingDocumentInvoicesExtractService
				.extractAccountingDocument(delta);
		final List<HyperwalletPayment> payeePayments = hyperWalletPaymentExtractService.payPayeeInvoice(invoices);
		final List<HyperwalletPayment> operatorPayments = payOperator(invoices);

		return Stream.concat(payeePayments.stream(), operatorPayments.stream()).collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<HyperwalletPayment> extractCreditNotes(final Date delta) {
		final List<CreditNoteModel> creditNotes = miraklAccountingDocumentCreditNotesExtractService
				.extractAccountingDocument(delta);

		return hyperWalletPaymentExtractService.payPayeeCreditNote(creditNotes);
	}

	protected abstract List<HyperwalletPayment> payOperator(final List<InvoiceModel> invoices);

}
