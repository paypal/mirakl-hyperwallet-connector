package com.paypal.infrastructure.mail.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Mail configuration class
 */
@Getter
@Configuration
public class MailConfiguration {

	@Value("${hmc.mail-alerts.settings.recipients}")
	private String notificationRecipients;

	@Value("${hmc.mail-alerts.settings.from}")
	private String fromNotificationEmail;

}
