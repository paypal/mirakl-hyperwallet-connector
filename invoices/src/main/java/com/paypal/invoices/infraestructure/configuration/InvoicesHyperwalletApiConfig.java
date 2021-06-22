package com.paypal.invoices.infraestructure.configuration;

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
@EqualsAndHashCode(callSuper = true)
@Configuration
@ConfigurationProperties(prefix = "invoices.hyperwallet.api")
@PropertySource({ "classpath:invoices.properties" })
public class InvoicesHyperwalletApiConfig extends AbstractHyperwalletApiConfig {

	private Map<String, String> paymentStoreTokens = new HashMap<>();

	private static final String PAYMENTS_HYPERWALLET_PROGRAM_PREFIX = "invoices.hyperwallet.api.hyperwalletprogram.token.";

	/**
	 * Loads Hyperwallet Program token architecture
	 */
	@PostConstruct
	public void loadInvoicesTokenMapConfiguration() throws InvalidConfigurationException {
		this.paymentStoreTokens = callSuperLoad(PAYMENTS_HYPERWALLET_PROGRAM_PREFIX);
	}

	protected Map<String, String> callSuperLoad(final String prefixHyperwalletProgram)
			throws InvalidConfigurationException {
		return super.loadTokenMapConfiguration(prefixHyperwalletProgram);
	}

}
