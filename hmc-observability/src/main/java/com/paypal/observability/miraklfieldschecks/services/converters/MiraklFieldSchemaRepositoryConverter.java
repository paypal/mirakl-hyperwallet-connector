package com.paypal.observability.miraklfieldschecks.services.converters;

import com.paypal.observability.miraklfieldschecks.model.MiraklField;
import com.paypal.observability.miraklfieldschecks.model.MiraklFieldPermissions;
import com.paypal.observability.miraklfieldschecks.model.MiraklFieldType;
import com.paypal.observability.miraklfieldschecks.repository.model.MiraklFieldPermissionsYaml;
import com.paypal.observability.miraklfieldschecks.repository.model.MiraklFieldSchemaYaml;
import com.paypal.observability.miraklfieldschecks.repository.model.MiraklFieldTypeYaml;
import com.paypal.observability.miraklfieldschecks.repository.model.MiraklFieldYaml;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchema;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MiraklFieldSchemaRepositoryConverter {

	List<MiraklField> from(List<MiraklFieldYaml> miraklFieldYaml);

	MiraklField from(MiraklFieldYaml miraklFieldYaml);

	MiraklFieldType from(MiraklFieldTypeYaml miraklFieldTypeYaml);

	MiraklFieldPermissions from(MiraklFieldPermissionsYaml miraklFieldPermissionsYaml);

	//@formatter:off
	default MiraklSchema from(final MiraklFieldSchemaYaml miraklFieldSchemaYaml) {
		return new MiraklSchema(miraklFieldSchemaYaml.getCustomFieldGroups()
				.stream()
				.flatMap(x -> x.getFields().stream())
				.map(this::from)
				.map(MiraklSchemaItem.class::cast)
				.collect(Collectors.toList()), MiraklField.class);
	}
	//@formatter:on

}
