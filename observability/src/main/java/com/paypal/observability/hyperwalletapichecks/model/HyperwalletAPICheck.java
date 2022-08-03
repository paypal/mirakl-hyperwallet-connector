package com.paypal.observability.hyperwalletapichecks.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class HyperwalletAPICheck {

	@Builder.Default
	private HyperwalletAPICheckStatus hyperwalletAPICheckStatus = HyperwalletAPICheckStatus.DOWN;

	private String error;

	private String location;

	public boolean isHealthy() {
		return !hyperwalletAPICheckStatus.equals(HyperwalletAPICheckStatus.DOWN);
	}

}
