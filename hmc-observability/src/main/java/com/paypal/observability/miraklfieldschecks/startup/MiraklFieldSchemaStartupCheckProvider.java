package com.paypal.observability.miraklfieldschecks.startup;

import com.paypal.observability.miraklfieldschecks.services.MiraklFieldSchemaCheckerService;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReport;
import com.paypal.observability.miraklschemadiffs.startup.converters.MiraklSchemaStartupCheckConverter;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckProvider;
import org.springframework.stereotype.Component;

@Component
public class MiraklFieldSchemaStartupCheckProvider implements StartupCheckProvider {

	public static final String STATUS_CHECK_DETAILS_DIFF_KEY = "diffs";

	private final MiraklFieldSchemaCheckerService miraklFieldSchemaCheckerService;

	private final MiraklSchemaStartupCheckConverter miraklSchemaStartupCheckConverter;

	public MiraklFieldSchemaStartupCheckProvider(final MiraklFieldSchemaCheckerService miraklFieldSchemaCheckerService,
			final MiraklSchemaStartupCheckConverter miraklSchemaStartupCheckConverter) {
		this.miraklFieldSchemaCheckerService = miraklFieldSchemaCheckerService;
		this.miraklSchemaStartupCheckConverter = miraklSchemaStartupCheckConverter;
	}

	@Override
	public StartupCheck check() {
		final MiraklSchemaDiffReport miraklFieldSchemaDiffReport = miraklFieldSchemaCheckerService.checkMiraklSchema();

		return miraklSchemaStartupCheckConverter.startupCheckFrom(miraklFieldSchemaDiffReport);
	}

	@Override
	public String getName() {
		return "miraklCustomFieldsSchemaCheck";
	}

}
