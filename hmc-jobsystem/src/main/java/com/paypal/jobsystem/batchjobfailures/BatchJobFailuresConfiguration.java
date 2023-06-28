package com.paypal.jobsystem.batchjobfailures;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableWebMvc
public class BatchJobFailuresConfiguration {

}
