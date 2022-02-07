package com.paypal.infrastructure.mail.impl;

import com.paypal.infrastructure.configuration.MailConfiguration;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Default implementation of {@link MailNotificationUtil}
 */
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
