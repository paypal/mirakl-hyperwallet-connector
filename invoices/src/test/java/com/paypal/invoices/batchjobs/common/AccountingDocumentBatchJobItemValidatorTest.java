package com.paypal.invoices.batchjobs.common;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemValidationResult;
import com.paypal.infrastructure.batchjob.BatchJobItemValidationStatus;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountingDocumentBatchJobItemValidatorTest extends AccountingDocumentBatchJobHandlersTestSupport {

	@InjectMocks
	private AccountingDocumentBatchJobItemValidator testObj;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private InvoiceModel invoiceModelMock;

	@Mock
	private TestAccountingDocumentBatchJobItem testAccountingDocumentBatchJobItemMock;

	@Test
	void validateItem_shouldReturnValidIfBankAccountAndProgramAreSet() {
		when(testAccountingDocumentBatchJobItemMock.getItem()).thenReturn(invoiceModelMock);
		when(invoiceModelMock.getHyperwalletProgram()).thenReturn("SOMETHING");
		when(invoiceModelMock.getDestinationToken()).thenReturn("SOMETHING");

		BatchJobItemValidationResult result = testObj.validateItem(batchJobContextMock,
				testAccountingDocumentBatchJobItemMock);

		assertThat(result.getStatus()).isEqualTo(BatchJobItemValidationStatus.VALID);
	}

	@Test
	void validateItem_shouldReturnInvalidIfBankAccountIsNotSet() {
		when(testAccountingDocumentBatchJobItemMock.getItem()).thenReturn(invoiceModelMock);
		when(testAccountingDocumentBatchJobItemMock.getItemId()).thenReturn("ITEMID-1");
		when(invoiceModelMock.getHyperwalletProgram()).thenReturn("SOMETHING");

		BatchJobItemValidationResult result = testObj.validateItem(batchJobContextMock,
				testAccountingDocumentBatchJobItemMock);

		assertThat(result.getStatus()).isEqualTo(BatchJobItemValidationStatus.INVALID);
		assertThat(result.getReason()).contains(
				"Invoice documents with id [ITEMID-1] should be skipped because are lacking hw-program or bank account token");
	}

	@Test
	void validateItem_shouldReturnInvalidIfProgramIsNotSet() {
		when(testAccountingDocumentBatchJobItemMock.getItem()).thenReturn(invoiceModelMock);

		BatchJobItemValidationResult result = testObj.validateItem(batchJobContextMock,
				testAccountingDocumentBatchJobItemMock);

		assertThat(result.getStatus()).isEqualTo(BatchJobItemValidationStatus.INVALID);
	}

}
