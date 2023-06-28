package com.paypal.infrastructure.hyperwallet.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletProgram;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletConnectionConfiguration;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import org.springframework.lang.Nullable;

public abstract class AbstractHyperwalletSDKService {

	protected final HyperwalletProgramsConfiguration programsConfiguration;

	protected final HyperwalletConnectionConfiguration connectionConfiguration;

	protected final HyperwalletEncryption encryption;

	protected AbstractHyperwalletSDKService(final HyperwalletProgramsConfiguration programsConfiguration,
			final HyperwalletConnectionConfiguration connectionConfiguration,
			@Nullable final HyperwalletEncryption encryption) {
		this.programsConfiguration = programsConfiguration;
		this.connectionConfiguration = connectionConfiguration;
		this.encryption = encryption;
	}

	public Hyperwallet getHyperwalletInstanceByHyperwalletProgram(final String hyperwalletProgram) {
		//@formatter:off
		final HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration programConfiguration =
				programsConfiguration.getProgramConfiguration(hyperwalletProgram);
		//@formatter:on
		return getHyperwalletInstanceByProgramToken(getProgramToken(programConfiguration));
	}

	public Hyperwallet getHyperwalletInstanceByProgramToken(final String programToken) {
		return new Hyperwallet(connectionConfiguration.getUsername(), connectionConfiguration.getPassword(),
				programToken, connectionConfiguration.getServer(), encryption);
	}

	public Hyperwallet getHyperwalletInstance() {
		return new Hyperwallet(connectionConfiguration.getUsername(), connectionConfiguration.getPassword(), null,
				connectionConfiguration.getServer(), encryption);
	}

	public HyperwalletProgram getRootProgram() {
		final String rootProgramToken = programsConfiguration.getRootProgramToken();
		return getHyperwalletInstanceByProgramToken(rootProgramToken).getProgram(rootProgramToken);
	}

	public String getProgramTokenByHyperwalletProgram(final String hyperwalletProgram) {
		return getProgramToken(programsConfiguration.getProgramConfiguration(hyperwalletProgram));
	}

	protected abstract String getProgramToken(
			HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration programConfiguration);

}
