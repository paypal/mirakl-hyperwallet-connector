package com.paypal.invoices.infraestructure.configuration;

import com.paypal.infrastructure.configuration.AbstractProgramHierarchyHyperwalletApiConfig;
import com.paypal.infrastructure.exceptions.InvalidConfigurationException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Configuration class to handle the operator commissions behaviour
 */
@Data
@EqualsAndHashCode(callSuper = true)
@PropertySource({ "classpath:invoices.properties" })
@Configuration
public class InvoicesOperatorCommissionsConfig extends AbstractProgramHierarchyHyperwalletApiConfig {

	@Value("#{'${invoices.operator.commissions.enabled}'}")
	private boolean enabled;

	private Map<String, String> bankAccountTokens = new HashMap<>();

	private static final String BANK_ACCOUNT_TOKENS_PREFIX = "invoices.operator.commissions.bankAccount.token.";

	/**
	 * Loads Hyperwallet Program token architecture
	 */
	@PostConstruct
	public void loadBankAccountTokensMapConfiguration() throws InvalidConfigurationException {
		this.bankAccountTokens = callSuperLoad(BANK_ACCOUNT_TOKENS_PREFIX);
	}

	protected Map<String, String> callSuperLoad(final String bankAccountsPrefix) throws InvalidConfigurationException {
		return super.loadTokenMapConfiguration(bankAccountsPrefix);
	}

	public String getBankAccountToken(final String hyperwalletProgram) {
		if (Objects.nonNull(hyperwalletProgram) && StringUtils.isNotEmpty(hyperwalletProgram)) {
			return Optional.ofNullable(getBankAccountTokens()).orElse(Map.of()).get(hyperwalletProgram);
		}
		return StringUtils.EMPTY;
	}

}
