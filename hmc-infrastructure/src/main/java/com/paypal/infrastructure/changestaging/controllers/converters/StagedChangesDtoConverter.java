package com.paypal.infrastructure.changestaging.controllers.converters;

import com.paypal.infrastructure.changestaging.controllers.dtos.StagedChangeDto;
import com.paypal.infrastructure.changestaging.repositories.entities.StagedChangeEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface StagedChangesDtoConverter {

	StagedChangeDto from(StagedChangeEntity source);

	default String from(final Class<?> value) {
		return value.getName();
	}

	default Page<StagedChangeDto> from(final Page<StagedChangeEntity> source) {
		return source.map(this::from);
	}

}
