package com.paypal.reports.infraestructure.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class to define report's headers
 */
@Getter
@PropertySource({ "classpath:reports.properties" })
@Configuration
public class ReportsConfig {

	@Value("#{'${reports.financialReport.header}'}")
	private String financialReportHeader;

	@Value("#{'${reports.financialReport.prefixFileName}'}")
	private String financialReportPrefixFileName;

	@Value("#{'${reports.hmc.financialRepoLocation}'}")
	protected String repoPath;

	@Value("#{'${reports.server.hmcServerUri}'}")
	protected String hmcServerUri;

}
