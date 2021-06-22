package com.paypal.invoices.invoicesextract.service.hyperwallet;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;

import java.util.List;

/**
 * Service to manipulate the payment creation within Hyperwallet
 */
public interface HyperWalletPaymentExtractService {

	/**
	 * Creates payments of type {@link HyperwalletPayment} based on {@code invoices} for
	 * the payees
	 * @param invoices the {@link List <HyperwalletPayment>}
	 */
	List<HyperwalletPayment> payPayeeInvoice(List<InvoiceModel> invoices);

	List<HyperwalletPayment> payPayeeCreditNote(List<CreditNoteModel> invoices);

	/**
	 * Creates payments of type {@link HyperwalletPayment} based on {@code invoices} for
	 * the operator
	 * @param invoices the {@link List <HyperwalletPayment>}
	 */
	List<HyperwalletPayment> payInvoiceOperator(List<InvoiceModel> invoices);

}
