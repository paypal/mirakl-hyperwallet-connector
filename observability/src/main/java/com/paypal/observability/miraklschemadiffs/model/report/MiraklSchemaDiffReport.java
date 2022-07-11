package com.paypal.observability.miraklschemadiffs.model.report;

import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiraklSchemaDiffReport {

	private String statusMessage;

	private List<MiraklSchemaDiffReportEntry> diffs = new ArrayList<>();

	public void addReportEntry(MiraklSchemaDiffEntry diff, MiraklSchemaDiffReportSeverity severity) {
		diffs.add(new MiraklSchemaDiffReportEntry(diff, severity));
	}

	public List<MiraklSchemaDiffReportEntry> getDiffs() {
		return Collections.unmodifiableList(diffs);
	}

	public boolean hasFailSeverityEntries() {
		return existsEntriesWithSeverity(MiraklSchemaDiffReportSeverity.FAIL);
	}

	public boolean hasWarnSeverityEntries() {
		return existsEntriesWithSeverity(MiraklSchemaDiffReportSeverity.WARN);
	}

	private boolean existsEntriesWithSeverity(MiraklSchemaDiffReportSeverity severity) {
		return diffs.stream().anyMatch(x -> x.getSeverity().equals(severity));
	}

	public boolean hasEntries() {
		return !diffs.isEmpty();
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

}
