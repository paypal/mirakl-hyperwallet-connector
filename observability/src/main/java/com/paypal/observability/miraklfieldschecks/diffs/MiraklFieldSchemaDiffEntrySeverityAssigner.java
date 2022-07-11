package com.paypal.observability.miraklfieldschecks.diffs;

import com.paypal.observability.miraklfieldschecks.model.MiraklField;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryIncorrectAttributeValue;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryType;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffEntrySeverityAssigner;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReportSeverity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MiraklFieldSchemaDiffEntrySeverityAssigner implements MiraklSchemaDiffEntrySeverityAssigner {

	//@formatter:off
	private Map<String, MiraklSchemaDiffReportSeverity> fieldNameToSeverityMap = Map.of(
			"type", MiraklSchemaDiffReportSeverity.FAIL,
			"label", MiraklSchemaDiffReportSeverity.WARN,
			"permissions", MiraklSchemaDiffReportSeverity.FAIL,
			"regexp", MiraklSchemaDiffReportSeverity.FAIL,
			"allowedValues", MiraklSchemaDiffReportSeverity.FAIL,
			"description", MiraklSchemaDiffReportSeverity.WARN,
			"required", MiraklSchemaDiffReportSeverity.WARN
	);
	//@formatter:on

	@Override
	public MiraklSchemaDiffReportSeverity getSeverityFor(MiraklSchemaDiffEntry entry) {
		MiraklSchemaDiffReportSeverity severity = MiraklSchemaDiffReportSeverity.WARN;
		if (MiraklSchemaDiffEntryType.ITEM_NOT_FOUND.equals(entry.getDiffType())) {
			severity = MiraklSchemaDiffReportSeverity.FAIL;
		}
		else if (MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE.equals(entry.getDiffType())) {
			severity = fieldNameToSeverityMap
					.get(((MiraklSchemaDiffEntryIncorrectAttributeValue) entry).getAttributeName());
		}

		return severity;
	}

	@Override
	public Class<? extends MiraklSchemaItem> getTargetSchemaType() {
		return MiraklField.class;
	}

}
