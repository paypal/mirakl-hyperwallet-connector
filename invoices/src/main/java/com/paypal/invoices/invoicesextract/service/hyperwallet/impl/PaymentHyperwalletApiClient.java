package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.hyperwallet.clientsdk.model.HyperwalletPaymentListOptions;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import org.springframework.stereotype.Service;

@Service
public class PaymentHyperwalletApiClient {

	private final HyperwalletSDKService sdkService;

	public PaymentHyperwalletApiClient(final HyperwalletSDKService hyperwalletSDKService) {
		this.sdkService = hyperwalletSDKService;
	}

	public HyperwalletPayment createPayment(final HyperwalletPayment hyperwalletPayment) {
		final Hyperwallet client = sdkService
				.getHyperwalletInstanceWithProgramToken(hyperwalletPayment.getProgramToken());
		return client.createPayment(hyperwalletPayment);
	}

	public HyperwalletList<HyperwalletPayment> listPayments(String programToken, String clientPaymentId) {
		final Hyperwallet client = sdkService.getHyperwalletInstanceWithProgramToken(programToken);
		final HyperwalletPaymentListOptions queryOptions = new HyperwalletPaymentListOptions()
				.clientPaymentId(clientPaymentId);
		return client.listPayments(queryOptions);
	}

}
