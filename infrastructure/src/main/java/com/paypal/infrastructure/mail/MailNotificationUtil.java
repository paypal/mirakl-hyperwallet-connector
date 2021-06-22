package com.paypal.infrastructure.mail;

/**
 * Service that offers email sending functionality
 */
public interface MailNotificationUtil {

	/**
	 * Sends an email with the attributes received without a message prefix
	 * @param subject Email's subject
	 * @param body Email's body message
	 */
	void sendPlainTextEmail(String subject, String body);

}
