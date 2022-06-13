package com.paypal.infrastructure.itemlinks;

import com.paypal.infrastructure.configuration.InfrastructureDatasourceConfig;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(
		basePackages = { "com.paypal.infrastructure.itemlinks", "com.paypal.infrastructure.itemlinks.repository" })
//@formatter:off
@Import({
		InfrastructureDatasourceConfig.class,
		ItemLinksConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
//@formatter:on
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class ItemLinksITTestContext {

}
