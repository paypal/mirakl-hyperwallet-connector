package com.paypal.infrastructure.hyperwallet.services;

import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletConnectionConfiguration;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link PaymentHyperwalletSDKService}
 */
@Service
public class PaymentHyperwalletSDKServiceImpl extends AbstractHyperwalletSDKService
		implements PaymentHyperwalletSDKService {

	public PaymentHyperwalletSDKServiceImpl(final HyperwalletProgramsConfiguration programsConfiguration,
			final HyperwalletConnectionConfiguration connectionConfiguration,
			@Nullable final HyperwalletEncryption encryption) {
		super(programsConfiguration, connectionConfiguration, encryption);
	}

	@Override
	protected String getProgramToken(
			final HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration programConfiguration) {
		return programConfiguration.getPaymentProgramToken();
	}

}
