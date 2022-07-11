package com.paypal.observability.startupchecks.model;

public interface StartupCheckPrinter {

	String[] print(StartupCheck check);

	Class<? extends StartupCheckProvider> getAssociatedStartupCheck();

}
