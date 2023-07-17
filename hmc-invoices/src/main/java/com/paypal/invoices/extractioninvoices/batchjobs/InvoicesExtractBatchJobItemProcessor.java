package com.paypal.invoices.extractioninvoices.batchjobs;

import com.paypal.invoices.extractioncommons.services.HyperWalletPaymentExtractService;
import com.paypal.invoices.extractioninvoices.services.InvoiceProcessService;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Invoices extract batch job item processor for creating the payments in HW
 */
@Component
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
