package com.paypal.invoices.batchjobs.invoices;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hmc.InvoiceProcessService;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InvoicesExtractBatchJobItemProcessorTest {

	@InjectMocks
	private InvoicesExtractBatchJobItemProcessor testObj;

	@Mock
	private HyperWalletPaymentExtractService hyperWalletPaymentExtractServiceMock;

	@Mock
	private InvoiceProcessService invoiceProcessServiceMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void processItem_ShouldPayPayeeInvoiceAndPayOperator() {

		final InvoiceModel invoiceModel = InvoiceModel.builder().build();
		final InvoiceExtractJobItem invoiceExtractJobItem = new InvoiceExtractJobItem(invoiceModel);

		testObj.processItem(batchJobContextMock, invoiceExtractJobItem);

		verify(hyperWalletPaymentExtractServiceMock).payPayeeInvoice(invoiceModel);
		verify(invoiceProcessServiceMock).payOperator(invoiceModel);
	}

}
