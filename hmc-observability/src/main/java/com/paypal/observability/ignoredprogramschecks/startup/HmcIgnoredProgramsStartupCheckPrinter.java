package com.paypal.observability.ignoredprogramschecks.startup;

import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckPrinter;
import com.paypal.observability.startupchecks.model.StartupCheckProvider;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

@Component
public class HmcIgnoredProgramsStartupCheckPrinter implements StartupCheckPrinter {

	@Override
	public String[] print(final StartupCheck check) {
		final StringWriter stringWriter = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(stringWriter);

		check.getStatusMessage().ifPresent(printWriter::println);
		printWriter.flush();
		return new String[] { stringWriter.toString() };
	}

	@Override
	public Class<? extends StartupCheckProvider> getAssociatedStartupCheck() {
		return HmcIgnoredProgramsStartupCheckProvider.class;
	}

}
