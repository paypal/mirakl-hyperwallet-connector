package com.paypal.observability.loggingcontext.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.paypal.observability.loggingcontext.model.LoggingTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LoggingContextHolderTest {

	@InjectMocks
	private LoggingContextHolder testObj;

	@BeforeEach
	void setUp() {
		testObj = new LoggingContextHolder();
	}

	@Test
	void getCurrentBusinessTransaction_shouldReturnAnOptionalEmptyWhenNoTransactionIsOpen() {
		final Optional<LoggingTransaction> result = testObj.getCurrentBusinessTransaction();

		assertThat(result).isEmpty();
	}

	@Test
	void getCurrentBusinessTransaction_shouldReturnAnOptionalNonEmptyWhenTransactionIsOpen() {
		testObj.refreshBusinessTransaction(new MyLoggingTransaction());
		final Optional<LoggingTransaction> result = testObj.getCurrentBusinessTransaction();

		assertThat(result).isNotEmpty();
	}

	@Test
	void getCurrentBusinessTransaction_shouldEnsureTheBusinessTransactionIsSetInTheThreadLocal() {
		final ThreadLocal<LoggingTransaction> businessTransactionInfoHolder = testObj
				.getBusinessTransactionInfoHolder();

		testObj.refreshBusinessTransaction(new MyLoggingTransaction());

		assertThat(businessTransactionInfoHolder.get()).isInstanceOf(MyLoggingTransaction.class);
	}

	@Test
	void closeBusinessTransaction_shouldRemoveFromThreadLocalTheLoggingTransactionAlreadyOpened() {
		final ThreadLocal<LoggingTransaction> businessTransactionInfoHolder = testObj
				.getBusinessTransactionInfoHolder();

		testObj.refreshBusinessTransaction(new MyLoggingTransaction());
		testObj.closeBusinessTransaction();

		assertThat(businessTransactionInfoHolder.get()).isNull();
	}

	@Test
	void closeBusinessTransaction_shouldNotFailWhenClosingANonExistingBusinessTransaction() {
		final ThreadLocal<LoggingTransaction> businessTransactionInfoHolder = testObj
				.getBusinessTransactionInfoHolder();

		testObj.closeBusinessTransaction();

		assertThat(businessTransactionInfoHolder.get()).isNull();
	}

	private static class MyLoggingTransaction implements LoggingTransaction {

		@Override
		public String getId() {
			return "ID";
		}

		@Override
		public String getType() {
			return "MyLoggingTransaction";
		}

		@Override
		public String getSubtype() {
			return "Subtype";
		}

		@Override
		public ObjectNode toJson() {
			final ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

			return objectMapper.valueToTree(this);
		}

	}

}
