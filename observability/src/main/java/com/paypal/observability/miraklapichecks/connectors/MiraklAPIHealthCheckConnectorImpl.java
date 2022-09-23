package com.paypal.observability.miraklapichecks.connectors;

import com.mirakl.client.mmp.domain.version.MiraklVersion;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;

import org.springframework.stereotype.Component;

@Component
public class MiraklAPIHealthCheckConnectorImpl implements MiraklAPIHealthCheckConnector {

	private final MiraklMarketplacePlatformOperatorApiWrapper miraklOperatorClient;

	public MiraklAPIHealthCheckConnectorImpl(final MiraklMarketplacePlatformOperatorApiWrapper miraklOperatorClient) {
		this.miraklOperatorClient = miraklOperatorClient;
	}

	@Override
	public MiraklVersion getVersion() {
		return miraklOperatorClient.getVersion();
	}

}
