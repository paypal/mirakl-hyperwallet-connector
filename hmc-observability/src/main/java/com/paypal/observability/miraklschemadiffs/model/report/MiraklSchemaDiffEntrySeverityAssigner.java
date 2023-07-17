package com.paypal.observability.miraklschemadiffs.model.report;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;

public interface MiraklSchemaDiffEntrySeverityAssigner {

	MiraklSchemaDiffReportSeverity getSeverityFor(MiraklSchemaDiffEntry entry);

	Class<? extends MiraklSchemaItem> getTargetSchemaType();

}
