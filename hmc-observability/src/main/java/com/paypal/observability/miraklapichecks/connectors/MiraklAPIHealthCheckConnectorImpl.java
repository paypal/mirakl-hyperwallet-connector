package com.paypal.observability.miraklapichecks.connectors;

import com.mirakl.client.mmp.domain.version.MiraklVersion;
import com.paypal.infrastructure.mirakl.client.MiraklClient;

import org.springframework.stereotype.Component;

@Component
public class MiraklAPIHealthCheckConnectorImpl implements MiraklAPIHealthCheckConnector {

	private final MiraklClient miraklOperatorClient;

	public MiraklAPIHealthCheckConnectorImpl(final MiraklClient miraklOperatorClient) {
		this.miraklOperatorClient = miraklOperatorClient;
	}

	@Override
	public MiraklVersion getVersion() {
		return miraklOperatorClient.getVersion();
	}

}
