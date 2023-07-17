package com.paypal.invoices.management.services;

import com.paypal.invoices.extractioninvoices.configuration.InvoicesOperatorCommissionsConfig;
import org.springframework.stereotype.Service;

@Service
public class InvoicesConfigurationServiceImpl implements InvoicesConfigurationService {

	private final InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig;

	public InvoicesConfigurationServiceImpl(final InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig) {
		this.invoicesOperatorCommissionsConfig = invoicesOperatorCommissionsConfig;
	}

	@Override
	public boolean isOperatorCommissionsEnabled() {
		return invoicesOperatorCommissionsConfig.isEnabled();
	}

	@Override
	public void setOperatorCommissionsEnabled(final boolean enabled) {
		invoicesOperatorCommissionsConfig.setEnabled(enabled);
	}

}
