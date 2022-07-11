package com.paypal.observability.mirakldocschecks.services.converters;

import com.paypal.observability.mirakldocschecks.model.MiraklDoc;
import com.paypal.observability.mirakldocschecks.repository.model.MiraklDocSchemaYaml;
import com.paypal.observability.mirakldocschecks.repository.model.MiraklDocYaml;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchema;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MiraklDocSchemaRepositoryConverter {

	List<MiraklDoc> from(List<MiraklDocYaml> miraklDocYamls);

	MiraklDoc from(MiraklDocYaml miraklDocYaml);

	default MiraklSchema from(final MiraklDocSchemaYaml miraklDocSchemaYaml) {
		//@formatter:off
		return new MiraklSchema(from(miraklDocSchemaYaml.getDocuments()).stream()
				.map(MiraklSchemaItem.class::cast)
				.collect(Collectors.toList()), MiraklDoc.class);
		//@formatter:on
	}

}
