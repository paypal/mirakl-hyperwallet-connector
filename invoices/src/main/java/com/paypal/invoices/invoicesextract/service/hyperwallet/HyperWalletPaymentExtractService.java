package com.paypal.invoices.invoicesextract.service.hyperwallet;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;

/**
 * Service to manipulate the payment creation within Hyperwallet
 */
public interface HyperWalletPaymentExtractService {

	/**
	 * Creates a payment of type {@link HyperwalletPayment} based on {@code invoices} for
	 * the payees
	 * @param invoice the {@link InvoiceModel}
	 * @return
	 */
	HyperwalletPayment payPayeeInvoice(InvoiceModel invoice);

	/**
	 * Creates a payment of type {@link HyperwalletPayment} based on {@code invoices} for
	 * the payees
	 * @param creditNote the {@link CreditNoteModel}
	 * @return
	 */
	HyperwalletPayment payPayeeCreditNotes(CreditNoteModel creditNote);

	/**
	 * Create a payment of type {@link HyperwalletPayment} based on {@code invoices} for
	 * the operator
	 * @param invoice the {@link InvoiceModel}
	 */
	HyperwalletPayment payInvoiceOperator(InvoiceModel invoice);

}
