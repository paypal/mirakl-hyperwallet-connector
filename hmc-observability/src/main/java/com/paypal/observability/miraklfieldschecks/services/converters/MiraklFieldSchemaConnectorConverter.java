package com.paypal.observability.miraklfieldschecks.services.converters;

import com.mirakl.client.mmp.domain.additionalfield.FieldPermission;
import com.mirakl.client.mmp.domain.additionalfield.MiraklAdditionalFieldType;
import com.mirakl.client.mmp.domain.additionalfield.MiraklFrontOperatorAdditionalField;
import com.paypal.observability.miraklfieldschecks.model.MiraklField;
import com.paypal.observability.miraklfieldschecks.model.MiraklFieldPermissions;
import com.paypal.observability.miraklfieldschecks.model.MiraklFieldType;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchema;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MiraklFieldSchemaConnectorConverter {

	List<MiraklField> miraklFieldListFrom(List<MiraklFrontOperatorAdditionalField> miraklAdditionalFields);

	@Mapping(target = "allowedValues", source = "acceptedValues")
	@Mapping(target = "regexpPattern", source = "regex")
	@Mapping(target = "permissions", source = "shopPermission")
	MiraklField from(MiraklFrontOperatorAdditionalField miraklAdditionalField);

	@ValueMapping(target = "TEXT", source = "STRING")
	@ValueMapping(target = "TEXT_AREA", source = "TEXTAREA")
	@ValueMapping(target = "REGULAR_EXPRESSION", source = "REGEX")
	@ValueMapping(target = "SINGLE_VALUE_LIST", source = "LIST")
	MiraklFieldType from(MiraklAdditionalFieldType miraklAdditionalFieldType);

	MiraklFieldPermissions from(FieldPermission miraklFieldPermission);

	//@formatter:off
	default MiraklSchema from(final List<MiraklFrontOperatorAdditionalField> miraklAdditionalFields) {
		return new MiraklSchema(miraklAdditionalFields.stream()
				.map(this::from)
				.map(MiraklSchemaItem.class::cast)
				.collect(Collectors.toList()), MiraklField.class);
	}
	//@formatter:on

}
