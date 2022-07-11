package com.paypal.observability.mirakldocschecks.startup;

import com.paypal.observability.miraklschemadiffs.startup.AbstractMiraklSchemaStartupCheckPrinter;
import com.paypal.observability.startupchecks.model.StartupCheckProvider;
import org.springframework.stereotype.Component;

@Component
public class MiraklDocSchemaStartupCheckPrinter extends AbstractMiraklSchemaStartupCheckPrinter {

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends StartupCheckProvider> getAssociatedStartupCheck() {
		return MiraklDocSchemaStartupCheckProvider.class;
	}

}
