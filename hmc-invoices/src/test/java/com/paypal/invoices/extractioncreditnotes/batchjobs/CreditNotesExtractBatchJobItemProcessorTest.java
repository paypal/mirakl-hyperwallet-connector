package com.paypal.invoices.extractioncreditnotes.batchjobs;

import com.paypal.invoices.extractioncreditnotes.batchjobs.CreditNoteExtractJobItem;
import com.paypal.invoices.extractioncreditnotes.batchjobs.CreditNotesExtractBatchJobItemProcessor;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.invoices.extractioncreditnotes.model.CreditNoteModel;
import com.paypal.invoices.extractioncommons.services.HyperWalletPaymentExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreditNotesExtractBatchJobItemProcessorTest {

	@InjectMocks
	private CreditNotesExtractBatchJobItemProcessor testObj;

	@Mock
	private HyperWalletPaymentExtractService hyperWalletPaymentExtractServiceMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void processItem_ShouldPayPayeeCreditNote() {

		final CreditNoteModel creditNoteModel = CreditNoteModel.builder().build();
		final CreditNoteExtractJobItem creditNoteExtractJobItem = new CreditNoteExtractJobItem(creditNoteModel);

		testObj.processItem(batchJobContextMock, creditNoteExtractJobItem);

		verify(hyperWalletPaymentExtractServiceMock).payPayeeCreditNotes(creditNoteModel);

	}

}
