package com.paypal.invoices.invoicesextract.service.hmc.impl;

import com.paypal.invoices.infraestructure.testing.TestingInvoicesSessionDataHelper;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceProcessServiceMockTest {

	@InjectMocks
	private InvoiceProcessServiceMock testObj;

	@Mock
	private TestingInvoicesSessionDataHelper testingInvoicesSessionDataHelperMock;

	@Mock
	private HyperWalletPaymentExtractService hyperWalletPaymentExtractServiceMock;

	@Mock
	private InvoiceModel invoiceModelMock;

	@Test
	void payOperator_ShouldPayInvoiceOperator_WhenOperatorCommissionsIsEnabled() {

		when(testingInvoicesSessionDataHelperMock.isOperatorCommissionsEnabled()).thenReturn(Boolean.TRUE);

		testObj.payOperator(invoiceModelMock);

		verify(hyperWalletPaymentExtractServiceMock).payInvoiceOperator(invoiceModelMock);
	}

	@Test
	void payOperator_ShouldNotPayInvoiceOperator_WhenOperatorCommissionsIsNotEnabled() {

		when(testingInvoicesSessionDataHelperMock.isOperatorCommissionsEnabled()).thenReturn(Boolean.FALSE);

		testObj.payOperator(invoiceModelMock);

		verify(hyperWalletPaymentExtractServiceMock, never()).payInvoiceOperator(invoiceModelMock);
	}

}
