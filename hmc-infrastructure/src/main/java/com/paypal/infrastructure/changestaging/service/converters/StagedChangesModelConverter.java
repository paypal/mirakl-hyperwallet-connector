package com.paypal.infrastructure.changestaging.service.converters;

import com.paypal.infrastructure.changestaging.model.Change;
import com.paypal.infrastructure.changestaging.model.StagedChange;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StagedChangesModelConverter {

	@Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
	@Mapping(target = "creationDate", expression = "java(new java.util.Date())")
	StagedChange from(Change source);

}
