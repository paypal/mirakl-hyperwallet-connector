package com.paypal.invoices.extractioncreditnotes.batchjobs;

import com.paypal.invoices.extractioncommons.services.MiraklAccountingDocumentExtractService;
import com.paypal.invoices.extractioncreditnotes.model.CreditNoteModel;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditNotesExtractBatchJobItemsExtractorTest {

	private static final Date DELTA = new Date();

	@InjectMocks
	private CreditNotesExtractBatchJobItemsExtractor testObj;

	@Mock
	private MiraklAccountingDocumentExtractService<CreditNoteModel> miraklAccountingDocumentCreditNotesExtractServiceMock;

	@Mock
	private CreditNoteModel creditNoteModelMock1, creditNoteModelMock2;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void getItems_ShouldReturnACollectionOfCreditNoteExtractJobItemForTheGivenDelta() {

		when(miraklAccountingDocumentCreditNotesExtractServiceMock.extractAccountingDocuments(DELTA, false))
				.thenReturn(List.of(creditNoteModelMock1, creditNoteModelMock2));

		final Collection<CreditNoteExtractJobItem> result = testObj.getItems(batchJobContextMock, DELTA, false);

		assertThat(result.stream().map(CreditNoteExtractJobItem::getItem))
				.containsExactlyInAnyOrder(creditNoteModelMock1, creditNoteModelMock2);
	}

}
