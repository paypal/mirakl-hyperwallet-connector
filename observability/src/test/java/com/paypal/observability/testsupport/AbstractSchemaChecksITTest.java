package com.paypal.observability.testsupport;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryIncorrectAttributeValue;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryType;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReport;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReportEntry;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReportSeverity;
import org.assertj.core.api.Condition;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractSchemaChecksITTest extends AbstractMockServerITTest {

	protected void assertThatContainsEntry(final MiraklSchemaDiffReport diffReport,
			final MiraklSchemaDiffEntryType entryType, final String affectedItem) {
		//@formatter:off
		final Condition<MiraklSchemaDiffReportEntry> condition = new Condition<>(entry -> {
			if (entry.getDiff().getDiffType().equals(entryType)) {
				return matchsItem(entry, affectedItem);
			} else {
				return false;
			}
		}, "Entry of type " + entryType + " affecting field: " + affectedItem);
		//@formatter:on
		assertThat(diffReport.getDiffs()).haveAtLeast(1, condition);
	}

	protected void assertThatContainsEntry(final MiraklSchemaDiffReport diffReport,
			final MiraklSchemaDiffEntryType entryType, final String affectedItem, String affectedField) {
		//@formatter:off
		final Condition<MiraklSchemaDiffReportEntry> condition = new Condition<>(entry -> {
			if (entry.getDiff().getDiffType().equals(entryType)) {
				return matchsItem(entry, affectedItem) && matchsField(entry, affectedField);
			} else {
				return false;
			}
		}, "Entry of type " + entryType + " affecting field: " + affectedItem);
		//@formatter:on
		assertThat(diffReport.getDiffs()).haveAtLeast(1, condition);
	}

	private boolean matchsField(MiraklSchemaDiffReportEntry entry, String affectedField) {
		return ((MiraklSchemaDiffEntryIncorrectAttributeValue) entry.getDiff()).getAttributeName()
				.equals(affectedField);
	}

	private boolean matchsItem(MiraklSchemaDiffReportEntry entry, String affectedItem) {
		if (ReflectionUtils.findField(entry.getDiff().getClass(), "expected") != null) {
			final MiraklSchemaItem item = (MiraklSchemaItem) ReflectionTestUtils.getField(entry.getDiff(),
					entry.getDiff().getClass(), "expected");
			return item.getCode().equals(affectedItem);
		}
		else {
			final MiraklSchemaItem item = (MiraklSchemaItem) ReflectionTestUtils.getField(entry.getDiff(),
					entry.getDiff().getClass(), "item");
			return item.getCode().equals(affectedItem);
		}
	}

	protected void assertSeverityForDiffType(final MiraklSchemaDiffReport diffReport,
			final MiraklSchemaDiffReportSeverity severity, final MiraklSchemaDiffEntryType miraklSchemaDiffEntryType) {
		//@formatter:off
		diffReport.getDiffs().stream()
				.filter(p -> p.getDiff().getDiffType() == miraklSchemaDiffEntryType)
				.forEach(p -> assertThat(p.getSeverity()).as("The severity [%s] of entry [%s] is not [%s]",
						p.getSeverity(), miraklSchemaDiffEntryType, severity).isEqualTo(severity));
		//@formatter:on
	}

	protected void assertSeverityForDiffType(final MiraklSchemaDiffReport diffReport,
			final MiraklSchemaDiffReportSeverity severity, String affectedField) {
		//@formatter:off
		diffReport.getDiffs().stream()
				.filter(p -> p.getDiff().getDiffType() == MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE && ((MiraklSchemaDiffEntryIncorrectAttributeValue) p.getDiff()).getAttributeName().equals(affectedField))
				.forEach(p -> assertThat(p.getSeverity()).as("The severity [%s] of entry [%s] is not [%s]",
						p.getSeverity(), MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE, severity).isEqualTo(severity));
		//@formatter:on
	}

}
