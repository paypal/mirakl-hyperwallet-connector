package com.paypal.invoices.paymentnotifications.service;

import com.mirakl.client.mmp.request.invoice.MiraklConfirmAccountingDocumentPaymentRequest;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Class to help testing concurrent executions of
 * {@link AcceptedPaymentNotificationStrategy#execute(PaymentNotificationBodyModel)}
 */
@Profile({ "qa" })
@Slf4j
@Service("acceptedPaymentNotificationStrategy")
public class AcceptedPaymentNotificationMockStrategy extends AcceptedPaymentNotificationStrategy
		implements Strategy<PaymentNotificationBodyModel, Void> {

	private final String mockServerUrl;

	private final RestTemplate restTemplate;

	private static final String MIRAKL_API_INVOICES_CONFIRM = "/mirakl/api/invoices/confirm";

	public AcceptedPaymentNotificationMockStrategy(@Value("${mockserver.url}") final String mockServerUrl,
			final RestTemplate restTemplate) {
		this.mockServerUrl = mockServerUrl;
		this.restTemplate = restTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void execute(final PaymentNotificationBodyModel paymentNotificationBodyModel) {
		restTemplate.postForObject(
				getMockServerUrl() + MIRAKL_API_INVOICES_CONFIRM + "/"
						+ paymentNotificationBodyModel.getClientPaymentId(),
				createPaymentConfirmationRequest(paymentNotificationBodyModel),
				MiraklConfirmAccountingDocumentPaymentRequest.class);

		return null;
	}

	protected String getMockServerUrl() {
		return mockServerUrl;
	}

}
