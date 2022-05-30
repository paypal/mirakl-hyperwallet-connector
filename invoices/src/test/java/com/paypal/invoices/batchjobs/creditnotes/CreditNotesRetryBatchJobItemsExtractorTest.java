package com.paypal.invoices.batchjobs.creditnotes;

import com.paypal.infrastructure.batchjob.BatchJobItem;
import com.paypal.infrastructure.batchjob.cache.AllRetryPendingFailedItemCacheFailureResolvePolicy;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklAccountingDocumentExtractService;
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
class CreditNotesRetryBatchJobItemsExtractorTest {

	private static final String CREDIT_NOTE_ID_1 = "1";

	private static final String CREDIT_NOTE_ID_2 = "2";

	@InjectMocks
	private CreditNotesRetryBatchJobItemsExtractor testObj;

	@Mock
	private MiraklAccountingDocumentExtractService<CreditNoteModel> miraklAccountingDocumentCreditNotesExtractServiceMock;

	@Mock
	private AllRetryPendingFailedItemCacheFailureResolvePolicy allRetryPendingFailedItemCacheFailureResolvePolicyMock;

	@Mock
	private CreditNoteModel creditNoteModel1Mock, creditNoteModel2Mock;

	@Test
	void getItem_shouldReturnCreditNotesType() {
		String result = testObj.getItemType();

		assertThat(result).isEqualTo(CreditNoteExtractJobItem.ITEM_TYPE);
	}

	@Test
	void getBatchJobFailedItemCacheFailureResolvePolicy_shouldReturnRetryAllPendingItemsPolicy() {
		assertThat(testObj.getBatchJobFailedItemCacheFailureResolvePolicy())
				.contains(allRetryPendingFailedItemCacheFailureResolvePolicyMock);
	}

	@Test
	void getItems_shouldReturnAllInvoicesByGivenIds() {
		when(miraklAccountingDocumentCreditNotesExtractServiceMock
				.extractAccountingDocuments(List.of(CREDIT_NOTE_ID_1, CREDIT_NOTE_ID_2)))
						.thenReturn(List.of(creditNoteModel1Mock, creditNoteModel2Mock));
		when(creditNoteModel1Mock.getInvoiceNumber()).thenReturn("1");
		when(creditNoteModel2Mock.getInvoiceNumber()).thenReturn("2");

		Collection<CreditNoteExtractJobItem> result = testObj.getItems(List.of(CREDIT_NOTE_ID_1, CREDIT_NOTE_ID_2));

		assertThat(result.stream().map(BatchJobItem::getItemId).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(CREDIT_NOTE_ID_1, CREDIT_NOTE_ID_2);
	}

}
