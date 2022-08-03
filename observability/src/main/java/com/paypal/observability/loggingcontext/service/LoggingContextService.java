package com.paypal.observability.loggingcontext.service;

import com.paypal.observability.loggingcontext.model.LoggingTransaction;

import java.util.Optional;

public interface LoggingContextService {

	Optional<LoggingTransaction> getCurrentLoggingTransaction();

	void updateLoggingTransaction(LoggingTransaction loggingTransaction);

	void closeLoggingTransaction();

	@SuppressWarnings("java:S112")
	void executeInLoggingContext(TransactionContextRunnable runnable, LoggingTransaction loggingTransaction)
			throws Throwable;

}
