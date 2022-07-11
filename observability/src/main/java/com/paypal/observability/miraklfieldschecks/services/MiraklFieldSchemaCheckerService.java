package com.paypal.observability.miraklfieldschecks.services;

import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReport;

public interface MiraklFieldSchemaCheckerService {

	MiraklSchemaDiffReport checkMiraklSchema();

}
