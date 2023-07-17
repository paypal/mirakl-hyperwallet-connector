package com.paypal.observability.mirakldocschecks.services.converters;

import com.mirakl.client.mmp.operator.domain.documents.MiraklDocumentsConfiguration;
import com.paypal.observability.mirakldocschecks.model.MiraklDoc;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchema;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MiraklDocSchemaConnectorConverter {

	MiraklDoc from(MiraklDocumentsConfiguration miraklDocumentsConfiguration);

	//@formatter:off
	default MiraklSchema from(final List<MiraklDocumentsConfiguration> miraklDocumentsConfigurations) {
		return new MiraklSchema(miraklDocumentsConfigurations.stream()
				.map(this::from)
				.map(MiraklSchemaItem.class::cast)
				.collect(Collectors.toList()), MiraklDoc.class);
	}
	//@formatter:on

}
