package com.paypal.invoices.infraestructure.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class to handle the operator credit notes
 */
@Data
@PropertySource({ "classpath:invoices.properties" })
@Configuration
public class CreditNotesConfig {

	@Value("#{'${invoices.operator.creditNotes.enabled}'}")
	private boolean enabled;

}
