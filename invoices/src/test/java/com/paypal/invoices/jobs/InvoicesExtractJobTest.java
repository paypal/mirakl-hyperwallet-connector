package com.paypal.invoices.jobs;

import com.paypal.invoices.infraestructure.configuration.CreditNotesConfig;
import com.paypal.invoices.invoicesextract.service.InvoiceExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoicesExtractJobTest {

	@InjectMocks
	@Spy
	private InvoicesExtractJob testObj;

	@Mock
	private InvoiceExtractService invoiceExtractServiceMock;

	@Mock
	private CreditNotesConfig creditNotesConfigMock;

	@Mock
	private JobExecutionContext contextMock;

	@Mock
	private Date dateMock;

	@Test
	void execute_shouldExecuteExtractInvoicesAndCreditNotesWhenCreditNotesOptionIsEnabled() {
		doReturn(dateMock).when(testObj).getDelta(contextMock);
		when(creditNotesConfigMock.isEnabled()).thenReturn(Boolean.TRUE);

		testObj.execute(contextMock);

		verify(invoiceExtractServiceMock).extractInvoices(dateMock);
		verify(invoiceExtractServiceMock).extractCreditNotes(dateMock);
	}

	@Test
	void execute_shouldExecuteExtractInvoicesWhenCreditNotesOptionIsNotEnabled() {
		doReturn(dateMock).when(testObj).getDelta(contextMock);
		when(creditNotesConfigMock.isEnabled()).thenReturn(Boolean.FALSE);

		testObj.execute(contextMock);

		verify(invoiceExtractServiceMock).extractInvoices(dateMock);
	}

}
