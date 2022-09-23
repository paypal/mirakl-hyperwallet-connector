package com.paypal.infrastructure.hyperwallet.api;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletProgram;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultHyperwalletSDKUserService implements HyperwalletSDKUserService {

	protected final UserHyperwalletApiConfig userHyperwalletApiConfig;

	private final HyperwalletEncryption encryption;

	public DefaultHyperwalletSDKUserService(final UserHyperwalletApiConfig userHyperwalletApiConfig,
			@Nullable final HyperwalletEncryption encryption) {
		this.userHyperwalletApiConfig = userHyperwalletApiConfig;
		this.encryption = encryption;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstanceByHyperwalletProgram(final String hyperwalletProgram) {
		//@formatter:off
		final String programUserToken = Optional.ofNullable(userHyperwalletApiConfig.getTokens())
				.map(tokens -> tokens.get(hyperwalletProgram))
				.orElse(null);
		//@formatter:on
		return new Hyperwallet(this.userHyperwalletApiConfig.getUsername(), this.userHyperwalletApiConfig.getPassword(),
				programUserToken, this.userHyperwalletApiConfig.getServer(), encryption);
	}

	@Override
	public Hyperwallet getHyperwalletInstanceByProgramToken(final String programToken) {
		return getHyperwalletInstance(programToken);
	}

	@Override
	public HyperwalletProgram getRootProgram() {
		return getHyperwalletInstanceByHyperwalletProgram(userHyperwalletApiConfig.getRootProgramToken())
				.getProgram(userHyperwalletApiConfig.getRootProgramToken());
	}

	protected Hyperwallet getHyperwalletInstance(final String programToken) {
		return new Hyperwallet(userHyperwalletApiConfig.getUsername(), userHyperwalletApiConfig.getPassword(),
				programToken, userHyperwalletApiConfig.getServer(), encryption);
	}

}
