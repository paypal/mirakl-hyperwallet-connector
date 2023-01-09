package com.paypal.infrastructure.mail.impl;

import com.paypal.infrastructure.mail.MailNotificationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link MailNotificationUtil}
 */
@ConditionalOnProperty(prefix = "mail.notifications", name = "enabled", havingValue = "false")
@Slf4j
@Service
public class MailNotificationUtilDisabled implements MailNotificationUtil {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendPlainTextEmail(final String subject, final String body) {
		log.info("Send email subject: " + subject + " body: " + body);
	}

}
