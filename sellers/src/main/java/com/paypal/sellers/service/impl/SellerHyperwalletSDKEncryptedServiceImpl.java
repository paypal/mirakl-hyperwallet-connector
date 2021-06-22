package com.paypal.sellers.service.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.paypal.sellers.infrastructure.configuration.SellersHyperwalletApiConfig;
import com.paypal.sellers.service.HyperwalletSDKService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link HyperwalletSDKService}
 */
@Service
@Profile({ "encrypted", "qaEncrypted" })
public class SellerHyperwalletSDKEncryptedServiceImpl extends SellerHyperwalletSDKServiceImpl {

	private final HyperwalletEncryption hyperwalletEncryption;

	public SellerHyperwalletSDKEncryptedServiceImpl(final SellersHyperwalletApiConfig kycHyperwalletApiConfig,
			final HyperwalletEncryption hyperwalletEncryption) {
		super(kycHyperwalletApiConfig);
		this.hyperwalletEncryption = hyperwalletEncryption;
	}

	@Override
	protected Hyperwallet getHyperwalletInstance(final String programToken) {
		return new Hyperwallet(this.sellersHyperwalletApiConfig.getUsername(),
				this.sellersHyperwalletApiConfig.getPassword(), programToken,
				this.sellersHyperwalletApiConfig.getServer(), this.hyperwalletEncryption);

	}

}
