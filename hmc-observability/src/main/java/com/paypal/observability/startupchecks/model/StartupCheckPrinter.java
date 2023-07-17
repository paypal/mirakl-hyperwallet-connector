package com.paypal.observability.startupchecks.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Collectors;

public interface StartupCheckPrinter {

	default String[] print(StartupCheck check) {
		final StringWriter stringWriter = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(stringWriter);

		check.getStatusMessage().ifPresent(printWriter::println);
		//@formatter:off
		printWriter.print(check.getDetails().entrySet().stream()
				.filter(x -> x.getValue() != null)
				.map(x -> String.format("%s: %s", x.getKey(), x.getValue()))
				.collect(Collectors.joining(System.lineSeparator())));
		//@formatter:on

		printWriter.flush();
		return new String[] { stringWriter.toString() };
	}

	Class<? extends StartupCheckProvider> getAssociatedStartupCheck();

}
