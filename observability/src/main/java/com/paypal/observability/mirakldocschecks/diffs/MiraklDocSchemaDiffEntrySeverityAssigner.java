package com.paypal.observability.mirakldocschecks.diffs;

import com.paypal.observability.mirakldocschecks.model.MiraklDoc;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryType;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffEntrySeverityAssigner;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReportSeverity;
import org.springframework.stereotype.Component;

@Component
public class MiraklDocSchemaDiffEntrySeverityAssigner implements MiraklSchemaDiffEntrySeverityAssigner {

	@Override
	public MiraklSchemaDiffReportSeverity getSeverityFor(final MiraklSchemaDiffEntry entry) {
		MiraklSchemaDiffReportSeverity severity = MiraklSchemaDiffReportSeverity.WARN;
		if (MiraklSchemaDiffEntryType.ITEM_NOT_FOUND.equals(entry.getDiffType())) {
			severity = MiraklSchemaDiffReportSeverity.FAIL;
		}

		return severity;
	}

	@Override
	public Class<? extends MiraklSchemaItem> getTargetSchemaType() {
		return MiraklDoc.class;
	}

}
