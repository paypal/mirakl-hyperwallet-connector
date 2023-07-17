package com.paypal.observability.miraklapichecks.services.converters;

import org.springframework.stereotype.Component;

import com.mirakl.client.mmp.domain.version.MiraklVersion;
import com.paypal.infrastructure.mirakl.configuration.MiraklApiClientConfig;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheckStatus;

@Component
public class MiraklAPIHealthCheckConnectorConverterImpl implements MiraklAPIHealthCheckConnectorConverter {

	private final MiraklApiClientConfig config;

	public MiraklAPIHealthCheckConnectorConverterImpl(final MiraklApiClientConfig config) {
		this.config = config;
	}

	@Override
	public MiraklAPICheck from(final MiraklVersion miraklVersion) {
		//@formatter:off
		return MiraklAPICheck.builder()
			.miraklAPICheckStatus(isHealthy(miraklVersion) ? MiraklAPICheckStatus.UP : MiraklAPICheckStatus.DOWN)
			.location(config.getEnvironment())
				.version(getVersion(miraklVersion))
				.error(getError(miraklVersion))
				.build();
		//@formatter:on
	}

	@Override
	public MiraklAPICheck from(final Exception e) {
		//@formatter:off
		return MiraklAPICheck.builder()
			.miraklAPICheckStatus(MiraklAPICheckStatus.DOWN)
			.location(config.getEnvironment())
				.error(e.getMessage())
				.build();
		//@formatter:on
	}

	private boolean isHealthy(final MiraklVersion miraklVersion) {
		return miraklVersion != null && miraklVersion.getVersion() != null;
	}

	private String getVersion(final MiraklVersion miraklVersion) {
		return miraklVersion != null && miraklVersion.getVersion() != null ? miraklVersion.getVersion() : null;
	}

	String getError(final MiraklVersion miraklVersion) {
		return miraklVersion == null || miraklVersion.getVersion() == null
				? "Mirakl Health Check end point didn't return version info" : null;
	}

}
