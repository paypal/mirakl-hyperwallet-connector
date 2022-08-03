package com.paypal.observability.loggingcontext.service.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.observability.loggingcontext.model.LoggingTransaction;
import org.springframework.stereotype.Component;

@Component
public class DefaultLoggingTransactionSerializer implements LoggingTransactionSerializer {

	@Override
	public String serialize(final LoggingTransaction loggingTransaction) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(loggingTransaction.toJson());
		}
		catch (final JsonProcessingException e) {
			return "";
		}
	}

}
