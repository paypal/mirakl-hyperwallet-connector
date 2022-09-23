package com.paypal.infrastructure.sdk.mirakl.impl;

import org.springframework.stereotype.Service;

import com.mirakl.client.core.security.MiraklCredential;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoices;

@Service
public class MiraklMarketplacePlatformOperatorApiClientWrapperImpl extends MiraklMarketplacePlatformOperatorApiClient
		implements MiraklMarketplacePlatformOperatorApiWrapper {

	/**
	 * @param config {@link MiraklApiClientConfig} bean.
	 */
	public MiraklMarketplacePlatformOperatorApiClientWrapperImpl(final MiraklApiClientConfig config) {
		super(config.getEnvironment(), new MiraklCredential(config.getOperatorApiKey()));
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
