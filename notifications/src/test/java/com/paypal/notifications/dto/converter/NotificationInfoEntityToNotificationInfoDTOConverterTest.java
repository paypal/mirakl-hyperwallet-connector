package com.paypal.notifications.dto.converter;

import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.notifications.dto.NotificationInfoDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationInfoEntityToNotificationInfoDTOConverterTest {

	private static final String NOTIFICATION_TOKEN = "token";

	private static final String PROGRAM = "program";

	private static final String TARGET = "target";

	private static final String TYPE = "type";

	private static final int RETRY_COUNTER = 2;

	@InjectMocks
	private NotificationInfoEntityToNotificationInfoDTOConverter testObj;

	@Mock
	private NotificationInfoEntity notificationInfoEntityMock;

	@Test
	void convert_shouldConvertNotificationInfoEntityToNotificationInfoDTO() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());
		final LocalDateTime now = TimeMachine.now();
		final Date nowAsDate = DateUtil.convertToDate(now, ZoneId.systemDefault());
		when(notificationInfoEntityMock.getNotificationToken()).thenReturn(NOTIFICATION_TOKEN);
		when(notificationInfoEntityMock.getCreationDate()).thenReturn(nowAsDate);
		when(notificationInfoEntityMock.getProgram()).thenReturn(PROGRAM);
		when(notificationInfoEntityMock.getTarget()).thenReturn(TARGET);
		when(notificationInfoEntityMock.getType()).thenReturn(TYPE);
		when(notificationInfoEntityMock.getRetryCounter()).thenReturn(RETRY_COUNTER);

		NotificationInfoDTO result = testObj.convert(notificationInfoEntityMock);

		//@formatter:off
		assertThat(result).hasFieldOrPropertyWithValue("notificationToken", NOTIFICATION_TOKEN)
				.hasFieldOrPropertyWithValue("creationDate", nowAsDate)
				.hasFieldOrPropertyWithValue("program", PROGRAM)
				.hasFieldOrPropertyWithValue("target", TARGET)
				.hasFieldOrPropertyWithValue("type", TYPE)
				.hasFieldOrPropertyWithValue("retryCounter", RETRY_COUNTER);
		//@formatter:on
	}

}
