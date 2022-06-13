package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.configuration.InfrastructureDatasourceConfig;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = { "com.paypal.infrastructure.batchjob" })
//@formatter:off
@Import({
		InfrastructureDatasourceConfig.class,
		HibernateJpaAutoConfiguration.class
})
//@formatter:on
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class BatchJobFailedItemServiceImplITTestContext {

}
