package com.paypal.observability.loggingcontext.service;

import com.paypal.observability.loggingcontext.model.LoggingTransaction;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoggingContextHolder {

	private final ThreadLocal<LoggingTransaction> businessTransactionInfoHolder = new NamedThreadLocal<>(
			"loggingTransactionContext");

	public Optional<LoggingTransaction> getCurrentBusinessTransaction() {
		return Optional.ofNullable(businessTransactionInfoHolder.get());
	}

	public void refreshBusinessTransaction(final LoggingTransaction loggingTransaction) {
		businessTransactionInfoHolder.set(loggingTransaction);
	}

	public void closeBusinessTransaction() {
		businessTransactionInfoHolder.remove();
	}

	protected ThreadLocal<LoggingTransaction> getBusinessTransactionInfoHolder() {
		return businessTransactionInfoHolder;
	}

}
