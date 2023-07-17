package com.paypal.invoices.extractioncreditnotes.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to handle the operator credit notes
 */
@Data
@Configuration
public class CreditNotesConfig {

	@Value("#{'${hmc.toggle-features.creditnotes-processing}'}")
	private boolean enabled;

}
