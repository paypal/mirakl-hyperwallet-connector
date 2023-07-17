package com.paypal.observability.loggingcontext.service.serializer;

import com.paypal.observability.loggingcontext.model.LoggingTransaction;

public interface LoggingTransactionSerializer {

	String serialize(LoggingTransaction loggingTransaction);

}
