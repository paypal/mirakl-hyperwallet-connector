package com.paypal.observability.mirakldocschecks.startup;

import com.paypal.observability.mirakldocschecks.services.MiraklDocSchemaCheckerService;
import com.paypal.observability.miraklschemadiffs.startup.converters.MiraklSchemaStartupCheckConverter;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReport;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckProvider;
import org.springframework.stereotype.Component;

@Component
public class MiraklDocSchemaStartupCheckProvider implements StartupCheckProvider {

	public static final String STATUS_CHECK_DETAILS_DIFF_KEY = "diffs";

	private final MiraklDocSchemaCheckerService miraklDocSchemaCheckerService;

	private final MiraklSchemaStartupCheckConverter miraklSchemaStartupCheckConverter;

	public MiraklDocSchemaStartupCheckProvider(final MiraklDocSchemaCheckerService miraklDocSchemaCheckerService,
			final MiraklSchemaStartupCheckConverter miraklSchemaStartupCheckConverter) {
		this.miraklDocSchemaCheckerService = miraklDocSchemaCheckerService;
		this.miraklSchemaStartupCheckConverter = miraklSchemaStartupCheckConverter;
	}

	@Override
	public StartupCheck check() {
		final MiraklSchemaDiffReport miraklDocSchemaDiffReport = miraklDocSchemaCheckerService.checkMiraklDocs();

		return miraklSchemaStartupCheckConverter.startupCheckFrom(miraklDocSchemaDiffReport);
	}

	@Override
	public String getName() {
		return "miraklDocSchemaCheck";
	}

}
