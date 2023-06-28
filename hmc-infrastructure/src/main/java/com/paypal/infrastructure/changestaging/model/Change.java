package com.paypal.infrastructure.changestaging.model;

import lombok.Data;

@Data
public class Change {

	private Class<?> type;

	private ChangeOperation operation;

	private ChangeTarget target;

	private Object payload;

}
