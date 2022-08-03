package com.paypal.observability.batchjoblogging.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.paypal.observability.loggingcontext.model.LoggingTransaction;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BatchJobLoggingTransaction implements LoggingTransaction {

	public static final String TRANSACTION_TYPE = "BatchJob";

	private String id;

	private String subtype;

	private String itemType;

	private String itemId;

	public String getType() {
		return TRANSACTION_TYPE;
	}

	public BatchJobLoggingTransaction(String id, String itemType) {
		this.id = id;
		this.itemType = itemType;
	}

	@Override
	public ObjectNode toJson() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		return objectMapper.valueToTree(this);
	}

}
