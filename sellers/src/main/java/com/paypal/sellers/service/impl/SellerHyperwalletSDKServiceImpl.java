package com.paypal.sellers.service.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.paypal.sellers.infrastructure.configuration.SellersHyperwalletApiConfig;
import com.paypal.sellers.service.HyperwalletSDKService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link HyperwalletSDKService}
 */
@Service
@Profile({ "!encrypted && !qaEncrypted" })
public class SellerHyperwalletSDKServiceImpl implements HyperwalletSDKService {

	protected final SellersHyperwalletApiConfig sellersHyperwalletApiConfig;

	public SellerHyperwalletSDKServiceImpl(final SellersHyperwalletApiConfig kycHyperwalletApiConfig) {
		this.sellersHyperwalletApiConfig = kycHyperwalletApiConfig;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstanceByHyperwalletProgram(final String hyperwalletProgram) {
		//@formatter:off
		final String programUserToken = Optional.ofNullable(this.sellersHyperwalletApiConfig.getUserStoreTokens())
				.map(tokens -> tokens.get(hyperwalletProgram))
				.orElse(null);
		//@formatter:on

		return getHyperwalletInstance(programUserToken);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstanceByProgramToken(final String programToken) {
		return getHyperwalletInstance(programToken);
	}

	protected Hyperwallet getHyperwalletInstance(final String programToken) {
		return new Hyperwallet(this.sellersHyperwalletApiConfig.getUsername(),
				this.sellersHyperwalletApiConfig.getPassword(), programToken,
				this.sellersHyperwalletApiConfig.getServer(), null);
	}

}
