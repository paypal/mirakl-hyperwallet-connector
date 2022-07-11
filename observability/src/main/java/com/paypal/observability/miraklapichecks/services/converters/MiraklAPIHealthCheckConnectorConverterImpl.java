package com.paypal.observability.miraklapichecks.services.converters;

import com.mirakl.client.mmp.domain.version.MiraklVersion;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheckStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MiraklAPIHealthCheckConnectorConverterImpl implements MiraklAPIHealthCheckConnectorConverter {

	@Value("${mirakl.environment}")
	private String miraklEnvironment;

	@Override
	public MiraklAPICheck from(MiraklVersion miraklVersion) {
		//@formatter:off
		return MiraklAPICheck.builder()
				.miraklAPICheckStatus(isHealthy(miraklVersion) ? MiraklAPICheckStatus.UP : MiraklAPICheckStatus.DOWN)
				.location(miraklEnvironment)
				.version(getVersion(miraklVersion))
				.error(getError(miraklVersion))
				.build();
		//@formatter:on
	}

	@Override
	public MiraklAPICheck from(Exception e) {
		//@formatter:off
		return MiraklAPICheck.builder()
				.miraklAPICheckStatus(MiraklAPICheckStatus.DOWN)
				.location(miraklEnvironment)
				.error(e.getMessage())
				.build();
		//@formatter:on
	}

	private boolean isHealthy(MiraklVersion miraklVersion) {
		return miraklVersion != null && miraklVersion.getVersion() != null;
	}

	private String getVersion(MiraklVersion miraklVersion) {
		return miraklVersion != null && miraklVersion.getVersion() != null ? miraklVersion.getVersion() : null;
	}

	String getError(MiraklVersion miraklVersion) {
		return miraklVersion == null || miraklVersion.getVersion() == null
				? "Mirakl Health Check end point didn't return version info" : null;
	}

}
