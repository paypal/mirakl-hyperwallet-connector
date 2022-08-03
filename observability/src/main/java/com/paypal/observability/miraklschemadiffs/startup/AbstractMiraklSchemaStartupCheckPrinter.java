package com.paypal.observability.miraklschemadiffs.startup;

import com.paypal.observability.mirakldocschecks.startup.MiraklDocSchemaStartupCheckProvider;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReportEntry;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReportSeverity;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckPrinter;

import java.util.List;

public abstract class AbstractMiraklSchemaStartupCheckPrinter implements StartupCheckPrinter {

	@SuppressWarnings("unchecked")
	@Override
	public String[] print(final StartupCheck check) {
		final List<MiraklSchemaDiffReportEntry> diffs = (List<MiraklSchemaDiffReportEntry>) check.getDetails()
				.get(MiraklDocSchemaStartupCheckProvider.STATUS_CHECK_DETAILS_DIFF_KEY);
		if (diffs != null) {
			//@formatter:off
			return diffs.stream()
					.map(this::printReportEntry)
					.toArray(String[]::new);
			//@formatter:on
		}
		else {
			return new String[0];
		}
	}

	private String printReportEntry(MiraklSchemaDiffReportEntry entry) {
		return String.format("%s%n%s", printMessage(entry), printSeverity(entry));
	}

	private String printMessage(MiraklSchemaDiffReportEntry entry) {
		return entry.getDiff().getMessage();
	}

	private String printSeverity(MiraklSchemaDiffReportEntry entry) {
		//@formatter:off
		String severityPrintMessage = MiraklSchemaDiffReportSeverity.FAIL.equals(entry.getSeverity()) ?
				"BLOCKER" : "RECOMMENDATION";

		return String.format("Severity: %s", severityPrintMessage);
		//@formatter:on
	}

}
