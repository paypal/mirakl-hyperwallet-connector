package com.paypal.invoices.invoicesextract.service.hmc.impl;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.invoices.infraestructure.configuration.InvoicesOperatorCommissionsConfig;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hmc.InvoiceProcessService;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link InvoiceProcessService}.
 */
@Service
public class InvoiceProcessServiceImpl extends AbstractInvoiceProcessService {

	private final InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig;

	protected InvoiceProcessServiceImpl(final HyperWalletPaymentExtractService hyperWalletPaymentExtractService,
			final InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig) {
		super(hyperWalletPaymentExtractService);
		this.invoicesOperatorCommissionsConfig = invoicesOperatorCommissionsConfig;
	}

	/**
	 * {@inheritDoc}
	 */
	public void payOperator(final InvoiceModel invoice) {
		if (invoicesOperatorCommissionsConfig.isEnabled()) {
			super.hyperWalletPaymentExtractService.payInvoiceOperator(invoice);
		}
	}

}
