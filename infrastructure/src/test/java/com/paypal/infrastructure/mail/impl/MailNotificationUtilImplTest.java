package com.paypal.infrastructure.mail.impl;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.infrastructure.configuration.MailConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailNotificationUtilImplTest {

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.ERROR)
			.recordForType(MailNotificationUtilImpl.class);

	private static final String SUBJECT = "Subject";

	private static final String BODY = "Body";

	private static final String FROM_EMAIL = "from@mail.com";

	private static final String RECIPIENTS_EMAILS = "recipient1@mail.com";

	@InjectMocks
	private MailNotificationUtilImpl testObj;

	@Captor
	private ArgumentCaptor<SimpleMailMessage> simpleMailMessageCaptor;

	@Mock
	private MailConfiguration sellersMailConfigurationMock;

	@Mock
	private JavaMailSender javaMailSenderMock;

	@Test
	void sendPlainTextEmailWithMessagePrefix_shouldSendAnEmailWithInformationProvided() {
		when(sellersMailConfigurationMock.getFromNotificationEmail()).thenReturn(FROM_EMAIL);
		when(sellersMailConfigurationMock.getNotificationRecipients()).thenReturn(RECIPIENTS_EMAILS);

		testObj.sendPlainTextEmail(SUBJECT, BODY);

		verify(javaMailSenderMock).send(simpleMailMessageCaptor.capture());

		final SimpleMailMessage emailContent = simpleMailMessageCaptor.getValue();
		assertThat(emailContent.getSubject()).isEqualTo(SUBJECT);
		assertThat(emailContent.getText()).isEqualTo(BODY);
		assertThat(emailContent.getFrom()).isEqualTo(FROM_EMAIL);
		assertThat(emailContent.getTo()).containsExactlyInAnyOrder(RECIPIENTS_EMAILS);
	}

	@Test
	void sendPlainTextEmailWithMessagePrefix_shouldNotRethrowAnExceptionWhenSendingEmailFails() {
		logTrackerStub.recordForLevel(LogTracker.LogLevel.ERROR);
		doThrow(new RuntimeException("Something went wrong")).when(javaMailSenderMock)
				.send(any(SimpleMailMessage.class));

		testObj.sendPlainTextEmail(SUBJECT, BODY);

		assertThat(logTrackerStub.contains("Something went wrong")).isTrue();
	}

}
