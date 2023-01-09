package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.paypal.infrastructure.hyperwallet.api.PaymentsHyperwalletApiConfig;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link HyperwalletSDKService}
 */
@Service
public class PaymentsHyperwalletSDKServiceImpl implements HyperwalletSDKService {

	protected final PaymentsHyperwalletApiConfig paymentsHyperwalletApiConfig;

	private final HyperwalletEncryption encryption;

	public PaymentsHyperwalletSDKServiceImpl(final PaymentsHyperwalletApiConfig paymentsHyperwalletApiConfig,
			@Nullable final HyperwalletEncryption encryption) {
		this.paymentsHyperwalletApiConfig = paymentsHyperwalletApiConfig;
		this.encryption = encryption;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstanceWithProgramToken(final String programToken) {
		return new Hyperwallet(paymentsHyperwalletApiConfig.getUsername(), paymentsHyperwalletApiConfig.getPassword(),
				programToken, paymentsHyperwalletApiConfig.getServer(), encryption);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstanceByHyperwalletProgram(final String hyperwalletProgram) {
		final String programUserToken = getProgramTokenByHyperwalletProgram(hyperwalletProgram);

		return new Hyperwallet(paymentsHyperwalletApiConfig.getUsername(), paymentsHyperwalletApiConfig.getPassword(),
				programUserToken, paymentsHyperwalletApiConfig.getServer(), encryption);
	}

	/**
	 * {@inheritDoc}
	 * @return
	 */
	@Override
	public String getProgramTokenByHyperwalletProgram(final String hyperwalletProgram) {
		//@formatter:off
		return Optional.ofNullable(paymentsHyperwalletApiConfig.getPaymentStoreTokens())
				.map(tokens -> tokens.get(hyperwalletProgram))
				.orElse(null);
		//@formatter:on
	}

}
