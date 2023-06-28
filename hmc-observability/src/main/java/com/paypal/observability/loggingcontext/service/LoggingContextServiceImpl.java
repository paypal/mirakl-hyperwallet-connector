package com.paypal.observability.loggingcontext.service;

import com.paypal.observability.loggingcontext.model.LoggingTransaction;
import com.paypal.observability.loggingcontext.service.serializer.LoggingTransactionSerializer;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoggingContextServiceImpl implements LoggingContextService {

	public static final String KEY_BUSINESS_TRANSACTION = "businessTransaction";

	private final LoggingContextHolder loggingTransactionContext;

	private final LoggingTransactionSerializer loggingTransactionSerializer;

	public LoggingContextServiceImpl(final LoggingContextHolder loggingTransactionContext,
			final LoggingTransactionSerializer loggingTransactionSerializer) {
		this.loggingTransactionContext = loggingTransactionContext;
		this.loggingTransactionSerializer = loggingTransactionSerializer;
	}

	@Override
	public Optional<LoggingTransaction> getCurrentLoggingTransaction() {
		return loggingTransactionContext.getCurrentBusinessTransaction();
	}

	@Override
	public void updateLoggingTransaction(final LoggingTransaction loggingTransaction) {
		loggingTransactionContext.refreshBusinessTransaction(loggingTransaction);
		MDC.put(KEY_BUSINESS_TRANSACTION, loggingTransactionSerializer.serialize(loggingTransaction));
	}

	@Override
	public void closeLoggingTransaction() {
		loggingTransactionContext.closeBusinessTransaction();
		MDC.clear();
	}

	@Override
	public void executeInLoggingContext(final TransactionContextRunnable runnable,
			final LoggingTransaction loggingTransaction) throws Throwable {
		final LoggingTransaction currentTransaction = loggingTransactionContext.getCurrentBusinessTransaction()
				.orElse(loggingTransaction);
		updateLoggingTransaction(currentTransaction);
		runnable.run();
		closeLoggingTransaction();
	}

}
