package com.paypal.observability.loggingcontext.batchjobs.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.paypal.observability.batchjoblogging.model.BatchJobLoggingTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BatchJobLoggingTransactionTest {

	@InjectMocks
	private BatchJobLoggingTransaction testObj;

	@Test
	void toJson_shouldConvertToJson_whenAllFieldsAreFilled() {
		testObj.setItemId("ITEM_ID");
		testObj.setId("ID");
		testObj.setSubtype("SUBTYPE");
		testObj.setItemType("ITEM_TYPE");

		final ObjectNode result = testObj.toJson();
		assertThat(result).isNotNull();
	}

	@Test
	void toJson_shouldConvertToJson_whenNotAllFieldsAreFilled() {
		testObj.setId("ID");

		final ObjectNode result = testObj.toJson();
		assertThat(result).isNotNull();
	}

}
