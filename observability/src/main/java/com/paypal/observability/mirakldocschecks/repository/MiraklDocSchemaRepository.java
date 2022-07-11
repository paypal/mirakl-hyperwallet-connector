package com.paypal.observability.mirakldocschecks.repository;

import com.paypal.observability.mirakldocschecks.repository.model.MiraklDocSchemaYaml;

public interface MiraklDocSchemaRepository {

	MiraklDocSchemaYaml loadCustomFieldsSchema();

}
