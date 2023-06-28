package com.paypal.observability.miraklapichecks.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class MiraklAPICheck {

	@Builder.Default
	private MiraklAPICheckStatus miraklAPICheckStatus = MiraklAPICheckStatus.DOWN;

	private String error;

	private String version;

	private String location;

	public boolean isHealthy() {
		return !miraklAPICheckStatus.equals(MiraklAPICheckStatus.DOWN) && version != null;
	}

}
