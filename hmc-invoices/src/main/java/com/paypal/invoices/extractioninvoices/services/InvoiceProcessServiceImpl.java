package com.paypal.invoices.extractioninvoices.services;

import com.paypal.invoices.extractioninvoices.configuration.InvoicesOperatorCommissionsConfig;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import com.paypal.invoices.extractioncommons.services.HyperWalletPaymentExtractService;
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
	@Override
	public void payOperator(final InvoiceModel invoice) {
		if (invoicesOperatorCommissionsConfig.isEnabled()) {
			super.hyperWalletPaymentExtractService.payInvoiceOperator(invoice);
		}
	}

}
