package com.paypal.invoices.controllers;

import com.paypal.infrastructure.job.AbstractDeltaInfoJob;
import com.paypal.infrastructure.service.JobService;
import com.paypal.invoices.dto.MiraklInvoiceMockDTO;
import com.paypal.invoices.dto.MiraklInvoicesMockListDTO;
import com.paypal.invoices.dto.converter.MiraklInvoiceDTOToInvoiceModelConverter;
import com.paypal.invoices.infraestructure.testing.TestingInvoicesSessionDataHelper;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.jobs.InvoicesExtractJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.SchedulerException;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoicesExtractJobMockControllerTest {

	private static final String JOB_NAME = "jobName";

	@InjectMocks
	private InvoiceExtractJobMockController testObj;

	@Mock
	private JobService jobService;

	@Mock
	private MiraklInvoiceMockDTO miraklInvoiceMockDTOOneMock, miraklInvoiceMockDTOTwoMock,
			miraklCreditNoteMockDTOOneMock, miraklCreditNoteMockDTOTwoMock;

	@Mock
	private MiraklInvoicesMockListDTO miraklInvoicesMockListDTOMock, miraklCreditNotesMockListDTOMock;

	@Mock
	private TestingInvoicesSessionDataHelper testingInvoicesSessionDataHelperMock;

	@Mock
	private MiraklInvoiceDTOToInvoiceModelConverter miraklInvoiceDTOToInvoiceModelConverterMock;

	@Mock
	private InvoiceModel invoiceModelModelOneMock, invoiceModelModelTwoMock;

	@Test
	void runJob_shouldCallCreateAndRunSingleExecutionJobAndTestingInvoiceSessionDataWithTheTransformedValues()
			throws SchedulerException {
		when(miraklInvoicesMockListDTOMock.getInvoices())
				.thenReturn(List.of(miraklInvoiceMockDTOOneMock, miraklInvoiceMockDTOTwoMock));
		when(miraklInvoiceDTOToInvoiceModelConverterMock.convert(miraklInvoiceMockDTOOneMock))
				.thenReturn(invoiceModelModelOneMock);
		when(miraklInvoiceDTOToInvoiceModelConverterMock.convert(miraklInvoiceMockDTOTwoMock))
				.thenReturn(invoiceModelModelTwoMock);

		testObj.runJob(null, JOB_NAME, miraklInvoicesMockListDTOMock, false);

		verify(jobService).createAndRunSingleExecutionJob(JOB_NAME, InvoicesExtractJob.class,
				AbstractDeltaInfoJob.createJobDataMap(null), null);
		verify(testingInvoicesSessionDataHelperMock)
				.setInvoices(List.of(invoiceModelModelOneMock, invoiceModelModelTwoMock));
		verify(testingInvoicesSessionDataHelperMock).setOperatorCommissionsEnabled(false);
	}

}
