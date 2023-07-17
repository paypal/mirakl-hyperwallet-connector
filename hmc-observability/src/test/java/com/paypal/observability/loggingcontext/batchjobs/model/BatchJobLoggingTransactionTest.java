package com.paypal.observability.loggingcontext.batchjobs.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.paypal.observability.batchjoblogging.model.BatchJobLoggingTransaction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BatchJobLoggingTransactionTest {

	private BatchJobLoggingTransaction testObj;

	@Test
	void toJson_shouldConvertToJson_whenAllFieldsAreFilled() {
		final BatchJobLoggingTransaction testObj = new BatchJobLoggingTransaction();
		testObj.setItemId("ITEM_ID");
		testObj.setId("ID");
		testObj.setSubtype("SUBTYPE");
		testObj.setItemType("ITEM_TYPE");

		final ObjectNode result = testObj.toJson();
		assertThat(result).isNotNull();
	}

	@Test
	void toJson_shouldConvertToJson_whenNotAllFieldsAreFilled() {
		final BatchJobLoggingTransaction testObj = new BatchJobLoggingTransaction();
		testObj.setId("ID");

		final ObjectNode result = testObj.toJson();
		assertThat(result).isNotNull();
	}

}
