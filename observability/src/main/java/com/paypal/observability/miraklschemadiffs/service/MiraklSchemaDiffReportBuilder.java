package com.paypal.observability.miraklschemadiffs.service;

import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiff;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReport;

public interface MiraklSchemaDiffReportBuilder {

	MiraklSchemaDiffReport getSchemaReport(MiraklSchemaDiff diff);

}
