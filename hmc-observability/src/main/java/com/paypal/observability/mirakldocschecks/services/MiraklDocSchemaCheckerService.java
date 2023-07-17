package com.paypal.observability.mirakldocschecks.services;

import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReport;

public interface MiraklDocSchemaCheckerService {

	MiraklSchemaDiffReport checkMiraklDocs();

}
