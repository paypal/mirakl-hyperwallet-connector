package com.paypal.observability.loggingcontext.service;

@FunctionalInterface
public interface TransactionContextRunnable {

	@SuppressWarnings("java:S112")
	void run() throws Throwable;

}
