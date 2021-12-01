package com.paypal.invoices.invoicesextract.service.hmc.impl;

import com.paypal.invoices.infraestructure.testing.TestingInvoicesSessionDataHelper;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceExtractServiceMockTest {

	@InjectMocks
	private InvoiceExtractServiceMock testObj;

	@Mock
	private InvoiceModel invoiceModelOneMock, invoiceModelTwoMock;

	@Mock
	private HyperWalletPaymentExtractService hyperWalletPaymentExtractServiceMock;

	@Mock
	private TestingInvoicesSessionDataHelper testingInvoicesSessionDataHelperMock;

	@Test
	void payOperator_shouldSendOperatorInvoicesWhenOperatorCommissionsIsEnabled() {
		when(testingInvoicesSessionDataHelperMock.isOperatorCommissionsEnabled()).thenReturn(true);
		testObj.payOperator(List.of(invoiceModelOneMock, invoiceModelTwoMock));
		verify(hyperWalletPaymentExtractServiceMock)
				.payInvoiceOperator(List.of(invoiceModelOneMock, invoiceModelTwoMock));
	}

	@Test
	void payOperator_shouldNotSendOperatorInvoicesWhenOperatorCommissionsIsDisabled() {
		when(testingInvoicesSessionDataHelperMock.isOperatorCommissionsEnabled()).thenReturn(false);

		testObj.payOperator(List.of(invoiceModelOneMock, invoiceModelTwoMock));

		verifyNoInteractions(hyperWalletPaymentExtractServiceMock);
	}

}
