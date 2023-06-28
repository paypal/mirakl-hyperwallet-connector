package com.paypal.observability.miraklapichecks.services;

import com.mirakl.client.mmp.domain.version.MiraklVersion;
import com.paypal.observability.miraklapichecks.connectors.MiraklAPIHealthCheckConnector;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import com.paypal.observability.miraklapichecks.services.converters.MiraklAPIHealthCheckConnectorConverter;
import org.springframework.stereotype.Component;

@Component
public class MiraklHealthCheckServiceImpl implements MiraklHealthCheckService {

	private final MiraklAPIHealthCheckConnector miraklAPIHealthCheckConnector;

	private final MiraklAPIHealthCheckConnectorConverter miraklAPIHealthCheckConnectorConverter;

	public MiraklHealthCheckServiceImpl(final MiraklAPIHealthCheckConnector miraklAPIHealthCheckConnector,
			final MiraklAPIHealthCheckConnectorConverter miraklAPIHealthCheckConnectorConverter) {
		this.miraklAPIHealthCheckConnector = miraklAPIHealthCheckConnector;
		this.miraklAPIHealthCheckConnectorConverter = miraklAPIHealthCheckConnectorConverter;
	}

	@Override
	public MiraklAPICheck check() {
		try {
			final MiraklVersion miraklVersion = miraklAPIHealthCheckConnector.getVersion();
			return miraklAPIHealthCheckConnectorConverter.from(miraklVersion);
		}
		catch (final Exception e) {
			return miraklAPIHealthCheckConnectorConverter.from(e);
		}
	}

}
