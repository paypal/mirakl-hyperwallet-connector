package com.paypal.sellers.sellerextractioncommons.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration class to instantiate Mirakl API client
 */
@Data
@Component
public class SellersMiraklApiConfig {

	@Value("${hmc.mirakl.settings.timezone}")
	private String timeZone;

}
