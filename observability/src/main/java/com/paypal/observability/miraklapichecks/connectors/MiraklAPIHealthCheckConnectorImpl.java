package com.paypal.observability.miraklapichecks.connectors;

import com.mirakl.client.mmp.domain.version.MiraklVersion;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import org.springframework.stereotype.Component;

@Component
public class MiraklAPIHealthCheckConnectorImpl implements MiraklAPIHealthCheckConnector {

	private final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient;

	public MiraklAPIHealthCheckConnectorImpl(final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient) {
		this.miraklOperatorClient = miraklOperatorClient;
	}

	@Override
	public MiraklVersion getVersion() {
		return miraklOperatorClient.getVersion();
	}

}
