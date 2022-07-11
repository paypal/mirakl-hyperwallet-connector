package com.paypal.observability.miraklschemadiffs.startup;

import com.paypal.observability.mirakldocschecks.startup.MiraklDocSchemaStartupCheckProvider;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReportEntry;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckPrinter;

import java.util.List;

public abstract class AbstractMiraklSchemaStartupCheckPrinter implements StartupCheckPrinter {

	@Override
	public String[] print(final StartupCheck check) {
		final List<MiraklSchemaDiffReportEntry> diffs = (List<MiraklSchemaDiffReportEntry>) check.getDetails()
				.get(MiraklDocSchemaStartupCheckProvider.STATUS_CHECK_DETAILS_DIFF_KEY);
		if (diffs != null) {
			//@formatter:off
			return diffs.stream()
					.map(MiraklSchemaDiffReportEntry::getDiff)
					.map(MiraklSchemaDiffEntry::getMessage)
					.toArray(String[]::new);
            //@formatter:on
		}
		else {
			return new String[0];
		}
	}

}
