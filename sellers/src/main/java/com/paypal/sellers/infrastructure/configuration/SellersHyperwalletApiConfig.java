package com.paypal.sellers.infrastructure.configuration;

import com.paypal.infrastructure.configuration.AbstractHyperwalletApiConfig;
import com.paypal.infrastructure.exceptions.InvalidConfigurationException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class to instantiate Mirakl API client
 */
@Data
@Configuration
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "sellers.hyperwallet.api")
@PropertySource({ "classpath:sellers.properties" })
public class SellersHyperwalletApiConfig extends AbstractHyperwalletApiConfig {

	private Map<String, String> userStoreTokens = new HashMap<>();

	private static final String USER_IS_PREFIX = "sellers.hyperwallet.api.hyperwalletprogram.token.";

	/**
	 * Loads Hyperwallet Program token architecture
	 */
	@PostConstruct
	public void loadSellerTokenMapConfiguration() throws InvalidConfigurationException {
		this.userStoreTokens = callSuperLoad(USER_IS_PREFIX);
	}

	protected Map<String, String> callSuperLoad(final String prefixHyperwalletProgram)
			throws InvalidConfigurationException {
		return super.loadTokenMapConfiguration(prefixHyperwalletProgram);
	}

}
