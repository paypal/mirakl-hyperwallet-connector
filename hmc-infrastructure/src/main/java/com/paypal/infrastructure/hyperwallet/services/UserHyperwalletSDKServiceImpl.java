package com.paypal.infrastructure.hyperwallet.services;

import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletConnectionConfiguration;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class UserHyperwalletSDKServiceImpl extends AbstractHyperwalletSDKService implements UserHyperwalletSDKService {

	public UserHyperwalletSDKServiceImpl(final HyperwalletProgramsConfiguration programsConfiguration,
			final HyperwalletConnectionConfiguration connectionConfiguration,
			@Nullable final HyperwalletEncryption encryption) {
		super(programsConfiguration, connectionConfiguration, encryption);
	}

	protected String getProgramToken(
			final HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration programConfiguration) {
		return programConfiguration.getUsersProgramToken();
	}

}
