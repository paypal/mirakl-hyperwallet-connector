package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.hyperwallet.clientsdk.model.HyperwalletPaymentListOptions;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentHyperwalletApiClientTest {

	public static final String PROGRAM_TOKEN = "programToken";

	public static final String CLIENT_PAYMENT_ID = "clientPaymentId";

	@Mock
	private HyperwalletSDKService sdkService;

	private PaymentHyperwalletApiClient client;

	@BeforeEach
	void setUp() {
		client = new PaymentHyperwalletApiClient(sdkService);
	}

	@Test
	void createPayment() {
		final Hyperwallet hyperwalletMock = Mockito.mock(Hyperwallet.class);
		when(sdkService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);
		final HyperwalletPayment payment = new HyperwalletPayment().programToken(PROGRAM_TOKEN);

		client.createPayment(payment);

		verify(hyperwalletMock).createPayment(payment);

	}

	@Test
	void listPayments() {
		final Hyperwallet hyperwalletMock = Mockito.mock(Hyperwallet.class);
		when(sdkService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);

		client.listPayments(PROGRAM_TOKEN, CLIENT_PAYMENT_ID);
		ArgumentCaptor<HyperwalletPaymentListOptions> captor = ArgumentCaptor
				.forClass(HyperwalletPaymentListOptions.class);
		verify(hyperwalletMock).listPayments(captor.capture());

		Assertions.assertEquals(CLIENT_PAYMENT_ID, captor.getValue().getClientPaymentId());
	}

}