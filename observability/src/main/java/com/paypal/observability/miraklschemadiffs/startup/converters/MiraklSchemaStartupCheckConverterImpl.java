package com.paypal.observability.miraklschemadiffs.startup.converters;

import com.paypal.observability.mirakldocschecks.startup.MiraklDocSchemaStartupCheckProvider;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReport;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MiraklSchemaStartupCheckConverterImpl implements MiraklSchemaStartupCheckConverter {

	@Override
	public StartupCheck startupCheckFrom(final MiraklSchemaDiffReport miraklDocSchemaDiffReport) {
		//@formatter:off
		return StartupCheck.builder()
				.statusMessage(Optional.ofNullable(miraklDocSchemaDiffReport.getStatusMessage()))
				.status(from(miraklDocSchemaDiffReport))
				.detail(MiraklDocSchemaStartupCheckProvider.STATUS_CHECK_DETAILS_DIFF_KEY, miraklDocSchemaDiffReport.getDiffs())
				.build();
		//@formatter:on
	}

	private StartupCheckStatus from(final MiraklSchemaDiffReport miraklDocSchemaDiffReport) {
		if (miraklDocSchemaDiffReport.hasFailSeverityEntries()) {
			return StartupCheckStatus.NOT_READY;
		}
		else if (miraklDocSchemaDiffReport.hasWarnSeverityEntries()) {
			return StartupCheckStatus.READY_WITH_WARNINGS;
		}
		else {
			return StartupCheckStatus.READY;
		}
	}

}
