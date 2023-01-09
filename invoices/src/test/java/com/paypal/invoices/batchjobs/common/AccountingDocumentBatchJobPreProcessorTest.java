package com.paypal.invoices.batchjobs.common;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hmc.AccountingDocumentsLinksService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountingDocumentBatchJobPreProcessorTest extends AccountingDocumentBatchJobHandlersTestSupport {

	@InjectMocks
	private AccountingDocumentBatchJobPreProcessor testObj;

	@Mock
	private AccountingDocumentsLinksService accountingDocumentsLinksServiceMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private InvoiceModel invoiceModel1Mock, invoiceModel2Mock;

	@Mock
	private TestAccountingDocumentBatchJobItem testAccountingDocumentBatchJobItem1Mock,
			testAccountingDocumentBatchJobItem2Mock;

	@Test
	void prepareForProcessing_shouldStoreAllLinks() {
		when(testAccountingDocumentBatchJobItem1Mock.getItem()).thenReturn(invoiceModel1Mock);
		when(testAccountingDocumentBatchJobItem2Mock.getItem()).thenReturn(invoiceModel2Mock);

		testObj.prepareForProcessing(batchJobContextMock,
				List.of(testAccountingDocumentBatchJobItem1Mock, testAccountingDocumentBatchJobItem2Mock));

		verify(accountingDocumentsLinksServiceMock)
				.storeRequiredLinks(argThat(x -> x.containsAll(List.of(invoiceModel1Mock, invoiceModel2Mock))));
	}

}
