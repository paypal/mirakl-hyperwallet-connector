package com.paypal.infrastructure.batchjob.integrationtests;

import com.paypal.infrastructure.configuration.CacheConfig;
import com.paypal.infrastructure.configuration.InfrastructureDatasourceConfig;
import com.paypal.infrastructure.configuration.MailConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = { "com.paypal.infrastructure.batchjob", "com.paypal.infrastructure.mail" })
//@formatter:off
@Import({ CacheConfig.class,
		InfrastructureDatasourceConfig.class,
		MailConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		MailSenderAutoConfiguration.class,
		QuartzAutoConfiguration.class})
//@formatter:on
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class BatchJobFailedItemCacheTestContext {

}
