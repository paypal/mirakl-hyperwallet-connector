package com.paypal.observability.miraklfieldschecks.diffs;

import com.paypal.observability.miraklfieldschecks.model.MiraklField;
import com.paypal.observability.miraklfieldschecks.model.MiraklFieldPermissions;
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

	// We assign a numeric value to each permission representing the amount of rights
	// for each of them. The bigger the value the more rights it grants.
	// This is going to be used to compare the amount of rights granted by to different
	// permissions.
	private Map<MiraklFieldPermissions, Integer> permissionsLevelMap = Map.of(
			MiraklFieldPermissions.INVISIBLE, 0,
			MiraklFieldPermissions.READ_ONLY, 1,
			MiraklFieldPermissions.READ_WRITE, 2
	);
	//@formatter:on

	@Override
	public MiraklSchemaDiffReportSeverity getSeverityFor(MiraklSchemaDiffEntry entry) {
		MiraklSchemaDiffReportSeverity severity = MiraklSchemaDiffReportSeverity.WARN;
		if (MiraklSchemaDiffEntryType.ITEM_NOT_FOUND.equals(entry.getDiffType())) {
			severity = MiraklSchemaDiffReportSeverity.FAIL;
		}
		else if (MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE.equals(entry.getDiffType())) {
			MiraklSchemaDiffEntryIncorrectAttributeValue incorrectAttributeEntry = ((MiraklSchemaDiffEntryIncorrectAttributeValue) entry);
			String fieldName = incorrectAttributeEntry.getAttributeName();
			severity = fieldName.equals("permissions") ? getPermissionsAttributeSeverity(incorrectAttributeEntry)
					: getGenericAttributeSeverity(incorrectAttributeEntry);
		}

		return severity;
	}

	private MiraklSchemaDiffReportSeverity getPermissionsAttributeSeverity(
			MiraklSchemaDiffEntryIncorrectAttributeValue diffEntry) {
		MiraklFieldPermissions actual = ((MiraklField) diffEntry.getActual()).getPermissions();
		MiraklFieldPermissions expected = ((MiraklField) diffEntry.getExpected()).getPermissions();

		return permissionsLevelMap.get(actual) >= permissionsLevelMap.get(expected)
				? MiraklSchemaDiffReportSeverity.FAIL : MiraklSchemaDiffReportSeverity.WARN;
	}

	private MiraklSchemaDiffReportSeverity getGenericAttributeSeverity(
			MiraklSchemaDiffEntryIncorrectAttributeValue diffEntry) {
		String fieldName = diffEntry.getAttributeName();
		return fieldNameToSeverityMap.get(fieldName);
	}

	@Override
	public Class<? extends MiraklSchemaItem> getTargetSchemaType() {
		return MiraklField.class;
	}

}
