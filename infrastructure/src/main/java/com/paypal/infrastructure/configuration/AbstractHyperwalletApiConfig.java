package com.paypal.infrastructure.configuration;

import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Main Abstract class that manages Hyperwallet API Configuration
 */

@Data
public abstract class AbstractHyperwalletApiConfig extends AbstractProgramHierarchyHyperwalletApiConfig {

	protected String server;

	protected String username;

	protected String password;

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AbstractHyperwalletApiConfig)) {
			return false;
		}
		final AbstractHyperwalletApiConfig that = (AbstractHyperwalletApiConfig) o;

		return EqualsBuilder.reflectionEquals(this, that);

	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
