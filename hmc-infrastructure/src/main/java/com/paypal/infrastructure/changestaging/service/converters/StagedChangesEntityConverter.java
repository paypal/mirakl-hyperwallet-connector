package com.paypal.infrastructure.changestaging.service.converters;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mirakl.client.core.internal.mapper.PatchSerializer;
import com.mirakl.client.core.internal.util.Patch;
import com.paypal.infrastructure.changestaging.model.StagedChange;
import com.paypal.infrastructure.changestaging.repositories.entities.StagedChangeEntity;
import lombok.SneakyThrows;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StagedChangesEntityConverter {

	@Mapping(target = "payload", source = "source")
	StagedChangeEntity from(StagedChange source);

	@Mapping(target = "payload", qualifiedByName = "payloadDeserialization", source = "source")
	StagedChange from(StagedChangeEntity source);

	List<StagedChange> from(List<StagedChangeEntity> source);

	@SneakyThrows
	default String payloadFrom(final StagedChange source) {
		return getObjectMapper().writeValueAsString(source.getPayload());
	}

	@SneakyThrows
	@Named("payloadDeserialization")
	default Object payloadFrom(final StagedChangeEntity source) {
		return getObjectMapper().readValue(source.getPayload(), Class.forName(source.getType()));
	}

	default String from(final Class<?> value) {
		return value.getName();
	}

	@SneakyThrows
	default Class<?> map(final String value) {
		return Class.forName(value);
	}

	@SuppressWarnings("java:S3740")
	default ObjectMapper getObjectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		final SimpleModule module = new SimpleModule();
		module.addSerializer(Patch.class, new PatchSerializer());
		objectMapper.registerModule(module);
		return objectMapper;
	}

}
