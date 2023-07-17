package com.paypal.invoices.management.services;

public interface InvoicesConfigurationService {

	boolean isOperatorCommissionsEnabled();

	void setOperatorCommissionsEnabled(boolean enabled);

}
