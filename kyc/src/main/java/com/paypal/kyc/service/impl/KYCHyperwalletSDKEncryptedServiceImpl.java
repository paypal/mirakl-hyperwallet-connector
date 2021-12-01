package com.paypal.kyc.service.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.paypal.kyc.infrastructure.configuration.KYCHyperwalletApiConfig;
import com.paypal.kyc.service.HyperwalletSDKService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link HyperwalletSDKService}
 */
@Service
@Profile({ "encrypted", "qaEncrypted" })
public class KYCHyperwalletSDKEncryptedServiceImpl extends KYCHyperwalletSDKServiceImpl {

	private final HyperwalletEncryption hyperwalletEncryption;

	public KYCHyperwalletSDKEncryptedServiceImpl(final KYCHyperwalletApiConfig kycHyperwalletApiConfig,
			final HyperwalletEncryption hyperwalletEncryption) {
		super(kycHyperwalletApiConfig);
		this.hyperwalletEncryption = hyperwalletEncryption;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstance(final String issuingStore) {
		//@formatter:off
		final String programUserToken = Optional.ofNullable(this.kycHyperwalletApiConfig.getUserStoreTokens())
				.map(tokens -> tokens.get(issuingStore))
				.orElse(null);
		//@formatter:on
		return new Hyperwallet(this.kycHyperwalletApiConfig.getUsername(), this.kycHyperwalletApiConfig.getPassword(),
				programUserToken, this.kycHyperwalletApiConfig.getServer(), this.hyperwalletEncryption);
	}

}
