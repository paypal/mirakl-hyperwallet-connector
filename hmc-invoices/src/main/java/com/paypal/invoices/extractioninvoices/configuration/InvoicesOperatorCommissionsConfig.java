package com.paypal.invoices.extractioninvoices.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration class to handle the operator commissions behaviour
 */
@Data
@Component
public class InvoicesOperatorCommissionsConfig {

	@Value("#{'${hmc.toggle-features.operator-commissions}'}")
	private boolean enabled;

}
