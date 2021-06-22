package com.paypal.invoices.paymentnotifications.service;

import com.mirakl.client.mmp.request.invoice.MiraklConfirmAccountingDocumentPaymentRequest;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcceptedPaymentNotificationMockStrategyTest {

	private static final String MOCK_SERVER_URL = "https://mockserver.aws.e2y.io";

	private static final String CONFIRM_PAYMENT_ENDPOINT = "/mirakl/api/invoices/confirm";

	private static final String PAYMENT_ID = "2000";

	@Spy
	@InjectMocks
	private AcceptedPaymentNotificationMockStrategy testObj;

	@Mock
	private PaymentNotificationBodyModel paymentNotificationBodyModelMock;

	@Mock
	private RestTemplate restTemplateMock;

	@Mock
	private MiraklConfirmAccountingDocumentPaymentRequest miraklConfirmAccountingDocumentPaymentRequestMock;

	@Test
	void execute_shouldSendConfirmationPaymentToMockServer() {
		when(testObj.getMockServerUrl()).thenReturn(MOCK_SERVER_URL);
		when(paymentNotificationBodyModelMock.getClientPaymentId()).thenReturn(PAYMENT_ID);
		doReturn(miraklConfirmAccountingDocumentPaymentRequestMock).when(testObj)
				.createPaymentRequest(paymentNotificationBodyModelMock);

		testObj.execute(paymentNotificationBodyModelMock);

		verify(restTemplateMock).postForObject(MOCK_SERVER_URL + CONFIRM_PAYMENT_ENDPOINT + '/' + PAYMENT_ID,
				miraklConfirmAccountingDocumentPaymentRequestMock, MiraklConfirmAccountingDocumentPaymentRequest.class);
	}

}
