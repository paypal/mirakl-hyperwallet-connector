package com.paypal.infrastructure.changestaging.controllers.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class StagedChangeDto {

	private String id;

	private String type;

	private String operation;

	private String target;

	private Object payload;

	private Date creationDate;

}
