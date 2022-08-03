package com.paypal.infrastructure.hyperwallet.api;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletProgram;

import java.util.Optional;

public class DefaultHyperwalletSDKService implements HyperwalletSDKService {

	protected final DefaultHyperwalletApiConfig defaultHyperwalletApiConfig;

	public DefaultHyperwalletSDKService(final DefaultHyperwalletApiConfig defaultHyperwalletApiConfig) {
		this.defaultHyperwalletApiConfig = defaultHyperwalletApiConfig;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstance(final String hyperwalletProgram) {
		//@formatter:off
		final String programUserToken = Optional.ofNullable(defaultHyperwalletApiConfig.getTokens())
				.map(tokens -> tokens.get(hyperwalletProgram))
				.orElse(null);
		//@formatter:on
		return new Hyperwallet(this.defaultHyperwalletApiConfig.getUsername(),
				this.defaultHyperwalletApiConfig.getPassword(), programUserToken,
				this.defaultHyperwalletApiConfig.getServer(), null);
	}

	@Override
	public HyperwalletProgram getRootProgram() {
		return getHyperwalletInstance().getProgram(defaultHyperwalletApiConfig.getRootProgramToken());
	}

	private Hyperwallet getHyperwalletInstance() {
		return getHyperwalletInstance(defaultHyperwalletApiConfig.getRootProgramToken());
	}

}
