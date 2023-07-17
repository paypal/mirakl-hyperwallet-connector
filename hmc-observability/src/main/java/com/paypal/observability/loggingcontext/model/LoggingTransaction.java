package com.paypal.observability.loggingcontext.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface LoggingTransaction {

	String getId();

	String getType();

	String getSubtype();

	ObjectNode toJson();

}
