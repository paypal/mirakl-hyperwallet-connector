package com.paypal.observability.miraklfieldschecks.startup;

import com.paypal.observability.miraklschemadiffs.startup.AbstractMiraklSchemaStartupCheckPrinter;
import com.paypal.observability.startupchecks.model.StartupCheckProvider;
import org.springframework.stereotype.Component;

@Component
public class MiraklFieldSchemaStartupCheckPrinter extends AbstractMiraklSchemaStartupCheckPrinter {

	@Override
	public Class<? extends StartupCheckProvider> getAssociatedStartupCheck() {
		return MiraklFieldSchemaStartupCheckProvider.class;
	}

}
