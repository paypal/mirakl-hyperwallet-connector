package com.paypal.infrastructure.util;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletError;
import com.hyperwallet.clientsdk.model.HyperwalletErrorList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface HyperwalletErrorLogEntryConverter {

	HyperwalletErrorLogEntryConverter INSTANCE = Mappers.getMapper(HyperwalletErrorLogEntryConverter.class);

	HyperwalletErrorLogEntry.ErrorDetail from(HyperwalletError hyperwalletError);

	List<HyperwalletErrorLogEntry.ErrorDetail> from(List<HyperwalletError> hyperwalletError);

	default List<HyperwalletError> from(HyperwalletErrorList hyperwalletError) {
		return hyperwalletError.getErrors();
	}

	@Mapping(target = "responseStatusCode", source = "response.responseCode")
	@Mapping(target = "responseMessage", source = "response.responseMessage")
	@Mapping(target = "responseBody", source = "response.body")
	@Mapping(target = "exceptionMessage", source = "message")
	@Mapping(target = "errorDetailList", source = "hyperwalletErrors")
	HyperwalletErrorLogEntry from(HyperwalletException exception);

}
