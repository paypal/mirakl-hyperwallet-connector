package com.paypal.kyc.service.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.paypal.kyc.infrastructure.configuration.KYCHyperwalletApiConfig;
import com.paypal.kyc.service.HyperwalletSDKService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link HyperwalletSDKService}
 */
@Service
@Profile({ "!encrypted && !qaEncrypted" })
public class KYCHyperwalletSDKServiceImpl implements HyperwalletSDKService {

	protected final KYCHyperwalletApiConfig kycHyperwalletApiConfig;

	public KYCHyperwalletSDKServiceImpl(final KYCHyperwalletApiConfig kycHyperwalletApiConfig) {
		this.kycHyperwalletApiConfig = kycHyperwalletApiConfig;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstance(final String hyperwalletProgram) {
		//@formatter:off
		final String programUserToken = Optional.ofNullable(this.kycHyperwalletApiConfig.getUserStoreTokens())
				.map(tokens -> tokens.get(hyperwalletProgram))
				.orElse(null);
		//@formatter:on
		return new Hyperwallet(this.kycHyperwalletApiConfig.getUsername(), this.kycHyperwalletApiConfig.getPassword(),
				programUserToken, this.kycHyperwalletApiConfig.getServer(), null);
	}

}
