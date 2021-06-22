package com.paypal.infrastructure.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Mail configuration class
 */
@Getter
@Configuration
@PropertySource({ "classpath:infrastructure.properties" })
public class MailConfiguration {

	@Value("${mail.notifications.recipients}")
	private String notificationRecipients;

	@Value("${mail.notifications.from}")
	private String fromNotificationEmail;

}
