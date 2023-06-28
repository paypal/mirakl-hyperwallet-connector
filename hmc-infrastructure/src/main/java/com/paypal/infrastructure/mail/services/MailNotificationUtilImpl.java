package com.paypal.infrastructure.mail.services;

import com.paypal.infrastructure.mail.configuration.MailConfiguration;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link MailNotificationUtil}
 */
@ConditionalOnProperty(prefix = "hmc.toggle-features.", name = "mail-alerts", havingValue = "true")
@Slf4j
@Service
public class MailNotificationUtilImpl implements MailNotificationUtil {

	@Resource
	private JavaMailSender emailSender;

	@Resource
	private MailConfiguration mailConfiguration;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendPlainTextEmail(final String subject, final String body) {
		final SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(mailConfiguration.getFromNotificationEmail());
		message.setTo(mailConfiguration.getNotificationRecipients());
		message.setSubject(subject);
		message.setText(body);
		try {
			emailSender.send(message);
		}
		catch (final RuntimeException e) {
			log.error("Email could not be sent. Reason: ", e);
		}
	}

}
