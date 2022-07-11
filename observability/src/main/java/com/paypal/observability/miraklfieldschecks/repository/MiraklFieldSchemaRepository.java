package com.paypal.observability.miraklfieldschecks.repository;

import com.paypal.observability.miraklfieldschecks.repository.model.MiraklFieldSchemaYaml;

public interface MiraklFieldSchemaRepository {

	MiraklFieldSchemaYaml loadCustomFieldsSchema(boolean includeKycRequiredFields);

}
