package com.paypal.invoices.batchjobs.invoices;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklAccountingDocumentExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoicesExtractBatchJobItemsExtractorTest {

	private static final Date DELTA = new Date();

	@InjectMocks
	private InvoicesExtractBatchJobItemsExtractor testObj;

	@Mock
	private MiraklAccountingDocumentExtractService<InvoiceModel> miraklAccountingDocumentInvoicesExtractServiceMock;

	@Mock
	private InvoiceModel invoiceModelMock1, invoiceModelMock2;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void getItems_ShouldReturnACollectionOfInvoiceExtractJobItemForTheGivenDetla() {

		when(miraklAccountingDocumentInvoicesExtractServiceMock.extractAccountingDocuments(DELTA))
				.thenReturn(List.of(invoiceModelMock1, invoiceModelMock2));

		final Collection<InvoiceExtractJobItem> result = testObj.getItems(batchJobContextMock, DELTA);

		assertThat(result.stream().map(InvoiceExtractJobItem::getItem)).containsExactlyInAnyOrder(invoiceModelMock1,
				invoiceModelMock2);
	}

}
