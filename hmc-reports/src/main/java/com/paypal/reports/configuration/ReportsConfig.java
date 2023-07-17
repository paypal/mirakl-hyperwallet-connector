package com.paypal.reports.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration class to define report's headers
 */
@Getter
@Component
public class ReportsConfig {

	@Value("#{'${hmc.financial-reports.header}'}")
	private String financialReportHeader;

	@Value("#{'${hmc.financial-reports.filename-prefix}'}")
	private String financialReportPrefixFileName;

	@Value("#{'${hmc.financial-reports.outputdir}'}")
	protected String repoPath;

	@Value("#{'${hmc.server.public-url}'}")
	protected String hmcServerUri;

}
