package com.paypal.invoices.invoicesextract.service.hmc.impl;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.invoices.infraestructure.configuration.InvoicesOperatorCommissionsConfig;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceExtractServiceImplTest {

	@InjectMocks
	private InvoiceExtractServiceImpl testObj;

	@Mock
	private InvoiceModel invoiceModelOneMock, invoiceModelTwoMock;

	@Mock
	private InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfigMock;

	@Mock
	private HyperWalletPaymentExtractService hyperWalletPaymentExtractServiceMock;

	@Mock
	private HyperwalletPayment hyperwalletPaymentOneMock, hyperwalletPaymentTwoMock;

	@Test
	void payOperator_shouldSendOperatorInvoicesWhenOperatorCommissionsIsEnabled() {
		final List<InvoiceModel> invoices = List.of(invoiceModelOneMock, invoiceModelTwoMock);
		when(invoicesOperatorCommissionsConfigMock.isEnabled()).thenReturn(true);
		final List<HyperwalletPayment> createdPayments = List.of(hyperwalletPaymentOneMock, hyperwalletPaymentTwoMock);
		when(hyperWalletPaymentExtractServiceMock.payInvoiceOperator(invoices)).thenReturn(createdPayments);

		final List<HyperwalletPayment> result = testObj.payOperator(invoices);

		verify(hyperWalletPaymentExtractServiceMock).payInvoiceOperator(invoices);
		assertThat(result).isEqualTo(createdPayments);
	}

	@Test
	void payOperator_shouldNotSendOperatorInvoicesWhenOperatorCommissionsIsDisabled() {
		when(invoicesOperatorCommissionsConfigMock.isEnabled()).thenReturn(false);

		final List<HyperwalletPayment> result = testObj.payOperator(List.of(invoiceModelOneMock, invoiceModelTwoMock));

		verifyNoInteractions(hyperWalletPaymentExtractServiceMock);
		assertThat(result).isEmpty();
	}

}
