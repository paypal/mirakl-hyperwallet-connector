package com.paypal.invoices.batchjobs.invoices;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.invoices.invoicesextract.service.hmc.InvoiceProcessService;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import org.springframework.stereotype.Service;

/**
 * Invoices extract batch job item processor for creating the payments in HW
 */
@Service
public class InvoicesExtractBatchJobItemProcessor
		implements BatchJobItemProcessor<BatchJobContext, InvoiceExtractJobItem> {

	private final HyperWalletPaymentExtractService hyperWalletPaymentExtractService;

	private final InvoiceProcessService invoiceProcessService;

	public InvoicesExtractBatchJobItemProcessor(final HyperWalletPaymentExtractService hyperWalletPaymentExtractService,
			final InvoiceProcessService invoiceProcessService) {
		this.hyperWalletPaymentExtractService = hyperWalletPaymentExtractService;
		this.invoiceProcessService = invoiceProcessService;
	}

	/**
	 * Processes the {@link InvoiceExtractJobItem} with the
	 * {@link HyperWalletPaymentExtractService}
	 * @param ctx The {@link BatchJobContext}
	 * @param jobItem The {@link InvoiceExtractJobItem}
	 */
	@Override
	public void processItem(final BatchJobContext ctx, final InvoiceExtractJobItem jobItem) {
		hyperWalletPaymentExtractService.payPayeeInvoice(jobItem.getItem());
		invoiceProcessService.payOperator(jobItem.getItem());
	}

}
