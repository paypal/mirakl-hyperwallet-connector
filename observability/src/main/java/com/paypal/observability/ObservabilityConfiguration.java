package com.paypal.observability;

import com.paypal.observability.miraklapichecks.MiraklAPIHealthCheckerConfig;
import com.paypal.observability.mirakldocschecks.MiraklDocsCheckerConfig;
import com.paypal.observability.miraklfieldschecks.MiraklFieldsCheckerConfig;
import com.paypal.observability.miraklschemadiffs.MiraklSchemaDiffsConfig;
import com.paypal.observability.startupchecks.StartupCheckConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
//@formatter:off
@Import({MiraklClientConfiguration.class,
		StartupCheckConfig.class,
		MiraklDocsCheckerConfig.class,
		MiraklFieldsCheckerConfig.class,
		MiraklSchemaDiffsConfig.class,
		MiraklAPIHealthCheckerConfig.class})
//@formatter:on
public class ObservabilityConfiguration {

}
