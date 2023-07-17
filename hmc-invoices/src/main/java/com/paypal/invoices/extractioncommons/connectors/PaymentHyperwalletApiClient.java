package com.paypal.invoices.extractioncommons.connectors;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.hyperwallet.clientsdk.model.HyperwalletPaymentListOptions;
import com.paypal.infrastructure.hyperwallet.services.PaymentHyperwalletSDKService;
import org.springframework.stereotype.Component;

@Component
public class PaymentHyperwalletApiClient {

	private final PaymentHyperwalletSDKService sdkService;

	public PaymentHyperwalletApiClient(final PaymentHyperwalletSDKService paymentHyperwalletSDKService) {
		this.sdkService = paymentHyperwalletSDKService;
	}

	public HyperwalletPayment createPayment(final HyperwalletPayment hyperwalletPayment) {
		final Hyperwallet client = sdkService
				.getHyperwalletInstanceByProgramToken(hyperwalletPayment.getProgramToken());
		return client.createPayment(hyperwalletPayment);
	}

	public HyperwalletList<HyperwalletPayment> listPayments(final String programToken, final String clientPaymentId) {
		final Hyperwallet client = sdkService.getHyperwalletInstanceByProgramToken(programToken);
		final HyperwalletPaymentListOptions queryOptions = new HyperwalletPaymentListOptions()
				.clientPaymentId(clientPaymentId);
		return client.listPayments(queryOptions);
	}

}
