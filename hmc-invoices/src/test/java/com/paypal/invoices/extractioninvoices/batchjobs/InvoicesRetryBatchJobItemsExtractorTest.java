package com.paypal.invoices.extractioninvoices.batchjobs;

import com.paypal.invoices.extractioninvoices.batchjobs.InvoiceExtractJobItem;
import com.paypal.invoices.extractioninvoices.batchjobs.InvoicesRetryBatchJobItemsExtractor;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobfailures.services.resolvepolicies.AllRetryPendingFailedItemCacheFailureResolvePolicy;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import com.paypal.invoices.extractioncommons.services.MiraklAccountingDocumentExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoicesRetryBatchJobItemsExtractorTest {

	private static final String INVOICE_ID_1 = "1";

	private static final String INVOICE_ID_2 = "2";

	@InjectMocks
	private InvoicesRetryBatchJobItemsExtractor testObj;

	@Mock
	private MiraklAccountingDocumentExtractService<InvoiceModel> miraklAccountingDocumentInvoicesExtractService;

	@Mock
	private AllRetryPendingFailedItemCacheFailureResolvePolicy allRetryPendingFailedItemCacheFailureResolvePolicyMock;

	@Mock
	private InvoiceModel invoiceModel1Mock, invoiceModel2Mock;

	@Test
	void getItem_shouldReturnInvoiceType() {
		final String result = testObj.getItemType();

		assertThat(result).isEqualTo(InvoiceExtractJobItem.ITEM_TYPE);
	}

	@Test
	void getBatchJobFailedItemCacheFailureResolvePolicy_shouldReturnRetryAllPendingItemsPolicy() {
		assertThat(testObj.getBatchJobFailedItemCacheFailureResolvePolicy())
				.contains(allRetryPendingFailedItemCacheFailureResolvePolicyMock);
	}

	@Test
	void getItems_shouldReturnAllInvoicesByGivenIds() {
		when(miraklAccountingDocumentInvoicesExtractService
				.extractAccountingDocuments(List.of(INVOICE_ID_1, INVOICE_ID_2)))
						.thenReturn(List.of(invoiceModel1Mock, invoiceModel2Mock));
		when(invoiceModel1Mock.getInvoiceNumber()).thenReturn("1");
		when(invoiceModel2Mock.getInvoiceNumber()).thenReturn("2");

		final Collection<InvoiceExtractJobItem> result = testObj.getItems(List.of(INVOICE_ID_1, INVOICE_ID_2));

		assertThat(result.stream().map(BatchJobItem::getItemId).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(INVOICE_ID_1, INVOICE_ID_2);
	}

}
