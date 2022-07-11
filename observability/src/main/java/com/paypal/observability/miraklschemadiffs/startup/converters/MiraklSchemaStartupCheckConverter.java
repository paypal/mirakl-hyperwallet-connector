package com.paypal.observability.miraklschemadiffs.startup.converters;

import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReport;
import com.paypal.observability.startupchecks.model.StartupCheck;

public interface MiraklSchemaStartupCheckConverter {

	StartupCheck startupCheckFrom(MiraklSchemaDiffReport miraklDocSchemaDiffReport);

}
