package com.paypal.observability.notificationslogging.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.paypal.observability.loggingcontext.model.LoggingTransaction;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationLoggingTransaction implements LoggingTransaction {

	public static final String TRANSACTION_TYPE = "Notification";

	private String id;

	private String subtype;

	private String targetToken;

	private String miraklShopId;

	private String clientPaymentId;

	@Override
	public String getType() {
		return TRANSACTION_TYPE;
	}

	public NotificationLoggingTransaction(final String id, final String subtype) {
		this.id = id;
		this.subtype = subtype;
	}

	@Override
	public ObjectNode toJson() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		return objectMapper.valueToTree(this);
	}

}
