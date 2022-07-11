package com.paypal.observability.miraklschemadiffs.service;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchema;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiff;

public interface MiraklSchemaComparator {

	MiraklSchemaDiff compareSchemas(MiraklSchema customFieldsSchema1, MiraklSchema customFieldsSchema2);

}
