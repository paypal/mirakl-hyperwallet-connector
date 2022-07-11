package com.paypal.observability.miraklschemadiffs.model.report;

import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class MiraklSchemaDiffReportEntry {

	private MiraklSchemaDiffEntry diff;

	private MiraklSchemaDiffReportSeverity severity;

}
