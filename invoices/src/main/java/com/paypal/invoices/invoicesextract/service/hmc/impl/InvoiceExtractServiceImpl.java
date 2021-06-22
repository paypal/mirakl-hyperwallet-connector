package com.paypal.invoices.invoicesextract.service.hmc.impl;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.invoices.infraestructure.configuration.InvoicesOperatorCommissionsConfig;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklAccountingDocumentExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class InvoiceExtractServiceImpl extends AbstractInvoiceExtractService {

	protected final InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig;

	protected InvoiceExtractServiceImpl(
			@Qualifier("miraklInvoicesExtractServiceImpl") final MiraklAccountingDocumentExtractService<InvoiceModel> miraklAccountingDocumentInvoicesExtractService,
			@Qualifier("miraklCreditNotesExtractServiceImpl") final MiraklAccountingDocumentExtractService<CreditNoteModel> miraklAccountingDocumentCreditNotesExtractService,
			final HyperWalletPaymentExtractService hyperWalletPaymentExtractService,
			final InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig) {
		super(miraklAccountingDocumentInvoicesExtractService, miraklAccountingDocumentCreditNotesExtractService,
				hyperWalletPaymentExtractService);
		this.invoicesOperatorCommissionsConfig = invoicesOperatorCommissionsConfig;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<HyperwalletPayment> payOperator(final List<InvoiceModel> invoices) {
		List<HyperwalletPayment> operatorPayments = new ArrayList<>();
		if (invoicesOperatorCommissionsConfig.isEnabled()) {
			log.info("Payment of commissions is enabled, retrieving payments for the operator");
			operatorPayments = hyperWalletPaymentExtractService.payInvoiceOperator(invoices);
		}
		else {
			log.info("Payment of commissions is disabled, skipping processing payments to operator");
		}
		return operatorPayments;
	}

}
