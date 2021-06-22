package com.paypal.invoices.jobs;

import com.paypal.infrastructure.job.AbstractDeltaInfoJob;
import com.paypal.invoices.infraestructure.configuration.CreditNotesConfig;
import com.paypal.invoices.invoicesextract.service.InvoiceExtractService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;

import javax.annotation.Resource;

/**
 * Extract invoices job for extracting Mirakl invoices data and populate it on HyperWallet
 * as users
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class InvoicesExtractJob extends AbstractDeltaInfoJob {

	@Resource
	protected InvoiceExtractService invoiceExtractService;

	@Resource
	CreditNotesConfig creditNotesConfig;

	@Override
	public void execute(final JobExecutionContext context) {
		invoiceExtractService.extractInvoices(getDelta(context));
		if (creditNotesConfig.isEnabled()) {
			invoiceExtractService.extractCreditNotes(getDelta(context));
		}
	}

}
