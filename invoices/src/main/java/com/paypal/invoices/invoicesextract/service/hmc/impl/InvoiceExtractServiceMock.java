package com.paypal.invoices.invoicesextract.service.hmc.impl;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.invoices.infraestructure.testing.TestingInvoicesSessionDataHelper;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklAccountingDocumentExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("invoiceExtractService")
@Profile({ "qa" })
public class InvoiceExtractServiceMock extends AbstractInvoiceExtractService {

	protected final TestingInvoicesSessionDataHelper testingInvoicesSessionDataHelper;

	protected InvoiceExtractServiceMock(
			@Qualifier("miraklInvoicesExtractServiceImpl") final MiraklAccountingDocumentExtractService<InvoiceModel> miraklInvoiceExtractService,
			@Qualifier("miraklCreditNotesExtractServiceImpl") final MiraklAccountingDocumentExtractService<CreditNoteModel> miraklAccountingDocumentExtractService,
			final HyperWalletPaymentExtractService hyperWalletPaymentExtractService,
			final TestingInvoicesSessionDataHelper testingInvoicesSessionDataHelper) {
		super(miraklInvoiceExtractService, miraklAccountingDocumentExtractService, hyperWalletPaymentExtractService);
		this.testingInvoicesSessionDataHelper = testingInvoicesSessionDataHelper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<HyperwalletPayment> payOperator(final List<InvoiceModel> invoices) {
		List<HyperwalletPayment> operatorPayments = new ArrayList<>();
		if (testingInvoicesSessionDataHelper.isOperatorCommissionsEnabled()) {
			operatorPayments = hyperWalletPaymentExtractService.payInvoiceOperator(invoices);
		}
		return operatorPayments;
	}

}
