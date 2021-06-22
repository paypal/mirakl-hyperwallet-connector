package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.paypal.invoices.infraestructure.configuration.InvoicesHyperwalletApiConfig;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link HyperwalletSDKService}
 */
@Service
@Profile({ "!encrypted && !qaEncrypted" })
public class InvoiceHyperwalletSDKServiceImpl implements HyperwalletSDKService {

	protected final InvoicesHyperwalletApiConfig invoicesHyperwalletApiConfig;

	public InvoiceHyperwalletSDKServiceImpl(final InvoicesHyperwalletApiConfig invoicesHyperwalletApiConfig) {
		this.invoicesHyperwalletApiConfig = invoicesHyperwalletApiConfig;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstanceWithProgramToken(final String programToken) {
		return new Hyperwallet(this.invoicesHyperwalletApiConfig.getUsername(),
				this.invoicesHyperwalletApiConfig.getPassword(), programToken,
				this.invoicesHyperwalletApiConfig.getServer(), null);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstanceByHyperwalletProgram(final String hyperwalletProgram) {
		final String programUserToken = getProgramTokenByHyperwalletProgram(hyperwalletProgram);

		return new Hyperwallet(this.invoicesHyperwalletApiConfig.getUsername(),
				this.invoicesHyperwalletApiConfig.getPassword(), programUserToken,
				this.invoicesHyperwalletApiConfig.getServer(), null);
	}

	/**
	 * {@inheritDoc}
	 * @return
	 */
	@Override
	public String getProgramTokenByHyperwalletProgram(final String hyperwalletProgram) {
		//@formatter:off
		return Optional.ofNullable(this.invoicesHyperwalletApiConfig.getPaymentStoreTokens())
                .map(tokens -> tokens.get(hyperwalletProgram))
                .orElse(null);
        //@formatter:on
	}

}
