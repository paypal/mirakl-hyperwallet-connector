package com.paypal.infrastructure.sdk.mirakl;

import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApi;
import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoices;

public interface MiraklMarketplacePlatformOperatorApiWrapper extends MiraklMarketplacePlatformOperatorApi {

	/**
	 * (IV01) List invoices Note: this resource supports pagination and will return 10
	 * invoices.
	 */
	HMCMiraklInvoices getInvoices(MiraklGetInvoicesRequest request);

}
