package com.paypal.observability.startupchecks.model;

public interface StartupCheckProvider {

	StartupCheck check();

	String getName();

}
