package com.paypal.observability;

import com.paypal.observability.batchjoblogging.BatchJobLoggingConfig;
import com.paypal.observability.hyperwalletapichecks.HyperwalletAPIHealthCheckerConfig;
import com.paypal.observability.ignoredprogramschecks.HmcIgnoredProgramsChecksConfig;
import com.paypal.observability.loggingcontext.LoggingContextConfig;
import com.paypal.observability.miraklapichecks.MiraklAPIHealthCheckerConfig;
import com.paypal.observability.mirakldocschecks.MiraklDocsCheckerConfig;
import com.paypal.observability.miraklfieldschecks.MiraklFieldsCheckerConfig;
import com.paypal.observability.miraklschemadiffs.MiraklSchemaDiffsConfig;
import com.paypal.observability.notificationslogging.NotificationsLoggingConfiguration;
import com.paypal.observability.startupchecks.StartupCheckConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
//@formatter:off
@Import({StartupCheckConfig.class,
		MiraklDocsCheckerConfig.class,
		MiraklFieldsCheckerConfig.class,
		MiraklSchemaDiffsConfig.class,
		MiraklAPIHealthCheckerConfig.class,
		HyperwalletAPIHealthCheckerConfig.class,
		LoggingContextConfig.class,
		BatchJobLoggingConfig.class,
		NotificationsLoggingConfiguration.class,
		HmcIgnoredProgramsChecksConfig.class
})
//@formatter:on
@PropertySource({ "classpath:observability.properties" })
public class ObservabilityConfiguration {

}
