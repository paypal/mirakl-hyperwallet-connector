package com.paypal.infrastructure.sdk.mirakl.impl;

import com.mirakl.client.core.AbstractMiraklApiClient;
import com.mirakl.client.core.security.Credential;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoices;

public class MiraklMarketplacePlatformOperatorApiClientWrapperImpl extends MiraklMarketplacePlatformOperatorApiClient
		implements MiraklMarketplacePlatformOperatorApiWrapper {

	/**
	 * @param endpoint
	 * @param credential
	 * @see AbstractMiraklApiClient#AbstractMiraklApiClient(String, Credential)
	 */
	public MiraklMarketplacePlatformOperatorApiClientWrapperImpl(final String endpoint, final Credential credential) {
		super(endpoint, credential);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HMCMiraklInvoices getInvoices(final MiraklGetInvoicesRequest request) {
		return getHmcMiraklInvoices(request);
	}

	protected HMCMiraklInvoices getHmcMiraklInvoices(final MiraklGetInvoicesRequest request) {
		return get(request, HMCMiraklInvoices.class);
	}

}
