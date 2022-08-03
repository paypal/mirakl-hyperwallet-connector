package com.paypal.infrastructure.hyperwallet.api;

import com.paypal.infrastructure.configuration.AbstractHyperwalletApiConfig;
import com.paypal.infrastructure.exceptions.InvalidConfigurationException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Configuration
@ConfigurationProperties(prefix = "infrastructure.hyperwallet.api")
@PropertySource({ "classpath:infrastructure.properties" })
public class DefaultHyperwalletApiConfig extends AbstractHyperwalletApiConfig {

	private Map<String, String> tokens = new HashMap<>();

	@Value("#{'${infrastructure.hyperwallet.api.hyperwalletprogram.rootToken}'}")
	private String rootProgramToken;

	private static final String HYPERWALLET_PROGRAM_PREFIX = "infrastructure.hyperwallet.api.hyperwalletprogram.token.";

	/**
	 * Loads Hyperwallet Program token architecture
	 */
	@PostConstruct
	public void loadInvoicesTokenMapConfiguration() throws InvalidConfigurationException {
		tokens = callSuperLoad(HYPERWALLET_PROGRAM_PREFIX);
	}

	protected Map<String, String> callSuperLoad(final String prefixHyperwalletProgram)
			throws InvalidConfigurationException {
		return super.loadTokenMapConfiguration(prefixHyperwalletProgram);
	}

}
