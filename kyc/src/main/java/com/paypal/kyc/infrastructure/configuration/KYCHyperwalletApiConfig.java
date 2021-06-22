package com.paypal.kyc.infrastructure.configuration;

import com.paypal.infrastructure.configuration.AbstractHyperwalletApiConfig;
import com.paypal.infrastructure.exceptions.InvalidConfigurationException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@ConfigurationProperties(prefix = "kyc.hyperwallet.api")
@EqualsAndHashCode(callSuper = true)
@PropertySource({ "classpath:kyc.properties" })
@Configuration
public class KYCHyperwalletApiConfig extends AbstractHyperwalletApiConfig {

	private Map<String, String> userStoreTokens = new HashMap<>();

	private static final String USER_IS_PREFIX = "kyc.hyperwallet.api.hyperwalletprogram.token.";

	/**
	 * Loads Hyperwallet Program token architecture
	 */
	@PostConstruct
	public void loadKYCTokenMapConfiguration() throws InvalidConfigurationException {
		this.userStoreTokens = callSuperLoad(USER_IS_PREFIX);
	}

	protected Map<String, String> callSuperLoad(final String prefixHyperwalletProgram)
			throws InvalidConfigurationException {
		return super.loadTokenMapConfiguration(prefixHyperwalletProgram);
	}

}
