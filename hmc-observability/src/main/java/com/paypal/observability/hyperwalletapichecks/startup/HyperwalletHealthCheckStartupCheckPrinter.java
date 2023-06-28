package com.paypal.observability.hyperwalletapichecks.startup;

import com.paypal.observability.startupchecks.model.StartupCheckPrinter;
import com.paypal.observability.startupchecks.model.StartupCheckProvider;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletHealthCheckStartupCheckPrinter implements StartupCheckPrinter {

	@Override
	public Class<? extends StartupCheckProvider> getAssociatedStartupCheck() {
		return HyperwalletHealthCheckStartupProvider.class;
	}

}
