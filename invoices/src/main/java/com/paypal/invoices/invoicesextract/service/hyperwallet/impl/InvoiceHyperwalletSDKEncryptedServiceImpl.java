package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.paypal.invoices.infraestructure.configuration.InvoicesHyperwalletApiConfig;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link HyperwalletSDKService}
 */
@Service
@Profile({ "encrypted", "qaEncrypted" })
public class InvoiceHyperwalletSDKEncryptedServiceImpl extends InvoiceHyperwalletSDKServiceImpl {

	private final HyperwalletEncryption hyperwalletEncryption;

	public InvoiceHyperwalletSDKEncryptedServiceImpl(final InvoicesHyperwalletApiConfig invoicesHyperwalletApiConfig,
			final HyperwalletEncryption hyperwalletEncryption) {
		super(invoicesHyperwalletApiConfig);
		this.hyperwalletEncryption = hyperwalletEncryption;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstanceWithProgramToken(final String programToken) {
		return new Hyperwallet(invoicesHyperwalletApiConfig.getUsername(), invoicesHyperwalletApiConfig.getPassword(),
				programToken, invoicesHyperwalletApiConfig.getServer(), hyperwalletEncryption);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstanceByHyperwalletProgram(final String programToken) {
		final String programUserToken = getProgramTokenByHyperwalletProgram(programToken);

		return new Hyperwallet(invoicesHyperwalletApiConfig.getUsername(), invoicesHyperwalletApiConfig.getPassword(),
				programUserToken, invoicesHyperwalletApiConfig.getServer(), hyperwalletEncryption);
	}

}
