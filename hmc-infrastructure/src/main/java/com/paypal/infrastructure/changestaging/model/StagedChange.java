package com.paypal.infrastructure.changestaging.model;

import lombok.Data;

import java.util.Date;

@Data
public class StagedChange {

	private String id;

	private Class<?> type;

	private ChangeOperation operation;

	private ChangeTarget target;

	private Object payload;

	private Date creationDate;

}
