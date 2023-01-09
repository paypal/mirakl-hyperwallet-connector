package com.paypal.invoices.batchjobs.creditnotes;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
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
