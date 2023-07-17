package com.paypal.observability.miraklschemadiffs.service;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiff;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffEntrySeverityAssigner;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffEntrySeverityAssignerRegistry;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReport;
import org.springframework.stereotype.Service;

@Service
public class MiraklSchemaDiffReportBuilderImpl implements MiraklSchemaDiffReportBuilder {

	private static final String STATUS_MESSAGE_OK = "The current schema in Mirakl is the expected one.";

	private static final String STATUS_MESSAGE_FAIL = "The current schema in Mirakl server is not compatible with this version of HMC.";

	private static final String STATUS_MESSAGE_WARN = "The current schema in Mirakl server is compatible with this version of HMC although there are some differences that are recommended to be solved.";

	private final MiraklSchemaDiffEntrySeverityAssignerRegistry miraklSchemaDiffEntrySeverityAssignerRegistry;

	public MiraklSchemaDiffReportBuilderImpl(
			final MiraklSchemaDiffEntrySeverityAssignerRegistry miraklSchemaDiffEntrySeverityAssignerRegistry) {
		this.miraklSchemaDiffEntrySeverityAssignerRegistry = miraklSchemaDiffEntrySeverityAssignerRegistry;
	}

	@Override
	public MiraklSchemaDiffReport getSchemaReport(final MiraklSchemaDiff miraklSchemaDiff) {
		final MiraklSchemaDiffReport miraklSchemaDiffReport = new MiraklSchemaDiffReport();

		miraklSchemaDiff.getDifferences()
				.forEach(diff -> addReportEntry(miraklSchemaDiffReport, diff, miraklSchemaDiff.getSchemaType()));

		if (miraklSchemaDiffReport.hasFailSeverityEntries()) {
			miraklSchemaDiffReport.setStatusMessage(STATUS_MESSAGE_FAIL);
		}
		else if (miraklSchemaDiffReport.hasWarnSeverityEntries()) {
			miraklSchemaDiffReport.setStatusMessage(STATUS_MESSAGE_WARN);
		}
		else {
			miraklSchemaDiffReport.setStatusMessage(STATUS_MESSAGE_OK);
		}

		return miraklSchemaDiffReport;
	}

	private void addReportEntry(final MiraklSchemaDiffReport miraklSchemaDiffReport, final MiraklSchemaDiffEntry diff,
			final Class<? extends MiraklSchemaItem> schemaType) {
		final MiraklSchemaDiffEntrySeverityAssigner severityAssigner = miraklSchemaDiffEntrySeverityAssignerRegistry
				.getMiraklSchemaDiffEntrySeverityAssigner(schemaType);
		miraklSchemaDiffReport.addReportEntry(diff, severityAssigner.getSeverityFor(diff));
	}

}
