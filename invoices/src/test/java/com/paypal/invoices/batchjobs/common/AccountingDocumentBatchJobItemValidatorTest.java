package com.paypal.invoices.batchjobs.common;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemValidationResult;
import com.paypal.infrastructure.batchjob.BatchJobItemValidationStatus;
import com.paypal.infrastructure.service.IgnoreProgramsService;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountingDocumentBatchJobItemValidatorTest extends AccountingDocumentBatchJobHandlersTestSupport {

	@InjectMocks
	private AccountingDocumentBatchJobItemValidator testObj;

	@Mock
	private IgnoreProgramsService ignoreProgramsService;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private InvoiceModel invoiceModelMock;

	@Mock
	private TestAccountingDocumentBatchJobItem testAccountingDocumentBatchJobItemMock;

	@Test
	void validateItem_shouldReturnValidIfBankAccountAndProgramAreSet_andProgramIgnoredProgramsIsEmpty() {
		when(testAccountingDocumentBatchJobItemMock.getItem()).thenReturn(invoiceModelMock);
		when(invoiceModelMock.getHyperwalletProgram()).thenReturn("SOMETHING");
		when(invoiceModelMock.getDestinationToken()).thenReturn("SOMETHING");
		when(ignoreProgramsService.isIgnored("SOMETHING")).thenReturn(false);

		BatchJobItemValidationResult result = testObj.validateItem(batchJobContextMock,
				testAccountingDocumentBatchJobItemMock);

		assertThat(result.getStatus()).isEqualTo(BatchJobItemValidationStatus.VALID);
	}

	@Test
	void validateItem_shouldReturnInvalidIfProgramIsIgnored() {
		when(testAccountingDocumentBatchJobItemMock.getItem()).thenReturn(invoiceModelMock);
		when(testAccountingDocumentBatchJobItemMock.getItemId()).thenReturn("ITEMID-1");
		when(invoiceModelMock.getHyperwalletProgram()).thenReturn("SOMETHING");
		when(ignoreProgramsService.isIgnored("SOMETHING")).thenReturn(true);

		BatchJobItemValidationResult result = testObj.validateItem(batchJobContextMock,
				testAccountingDocumentBatchJobItemMock);

		assertThat(result.getStatus()).isEqualTo(BatchJobItemValidationStatus.INVALID);
		assertThat(result.getReason()).contains(
				"Invoice documents with id [ITEMID-1] should be skipped because it belongs to an ignored program");
	}

	@Test
	void validateItem_shouldReturnInvalidIfBankAccountIsNotSet_andProgramIsNotIgnored() {
		when(testAccountingDocumentBatchJobItemMock.getItem()).thenReturn(invoiceModelMock);
		when(testAccountingDocumentBatchJobItemMock.getItemId()).thenReturn("ITEMID-1");
		when(invoiceModelMock.getHyperwalletProgram()).thenReturn("SOMETHING");
		when(ignoreProgramsService.isIgnored("SOMETHING")).thenReturn(false);

		BatchJobItemValidationResult result = testObj.validateItem(batchJobContextMock,
				testAccountingDocumentBatchJobItemMock);

		assertThat(result.getStatus()).isEqualTo(BatchJobItemValidationStatus.INVALID);
		assertThat(result.getReason()).contains(
				"Invoice documents with id [ITEMID-1] should be skipped because are lacking hw-program or bank account token");
	}

	@Test
	void validateItem_shouldReturnInvalidIfProgramIsNotSet() {
		when(testAccountingDocumentBatchJobItemMock.getItem()).thenReturn(invoiceModelMock);
		when(testAccountingDocumentBatchJobItemMock.getItemId()).thenReturn("ITEMID-1");

		BatchJobItemValidationResult result = testObj.validateItem(batchJobContextMock,
				testAccountingDocumentBatchJobItemMock);

		assertThat(result.getStatus()).isEqualTo(BatchJobItemValidationStatus.INVALID);
		assertThat(result.getReason()).contains(
				"Invoice documents with id [ITEMID-1] should be skipped because are lacking hw-program or bank account token");
	}

}
