package com.paypal.observability.miraklapichecks.startup;

import com.paypal.observability.startupchecks.model.StartupCheckPrinter;
import com.paypal.observability.startupchecks.model.StartupCheckProvider;
import org.springframework.stereotype.Component;

@Component
public class MiraklHealthCheckStartupCheckPrinter implements StartupCheckPrinter {

	@Override
	public Class<? extends StartupCheckProvider> getAssociatedStartupCheck() {
		return MiraklHealthCheckStartupProvider.class;
	}

}
