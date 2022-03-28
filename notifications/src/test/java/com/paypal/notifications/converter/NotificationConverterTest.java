package com.paypal.notifications.converter;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.notifications.model.entity.NotificationEntity;
import com.paypal.notifications.model.notification.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationConverterTest {

	private static final String OBJECT_TOKEN_MAP_KEY = "token";

	private static final String PAYMENT_OBJECT_TOKEN_MAP_VALUE = "pmt-ffffffff-fff-ffff-4444-444444444444";

	private static final String UNKNOWN_OBJECT_TOKEN_MAP_VALUE = "ffffffff-fff-ffff-4444-444444444444";

	private static final String WEB_HOOK_TOKEN = "webHookToken";

	private static final Date CREATION_DATE = new GregorianCalendar(2015, Calendar.FEBRUARY, 11).getTime();

	private final NotificationConverter testObj = new NotificationConverter();

	@Mock
	private HyperwalletWebhookNotification sourceMock;

	@BeforeEach
	public void setUp() {

		when(sourceMock.getToken()).thenReturn(WEB_HOOK_TOKEN);
		when(sourceMock.getCreatedOn()).thenReturn(CREATION_DATE);
	}

	@Test
	void convert_ShouldPopulateWebHookTokenAndCreationDateAndReceptionDateAndNotPopulateObjectTokenAndNotPopulateNotificationType_WhenObjectIsNull() {

		TimeMachine.useFixedClockAt(LocalDateTime.now());
		final LocalDateTime now = TimeMachine.now();
		final Date nowAsDate = DateUtil.convertToDate(now, ZoneId.systemDefault());

		final NotificationEntity result = testObj.convert(sourceMock);

		assertThat(result.getWebHookToken()).isEqualTo(WEB_HOOK_TOKEN);
		assertThat(result.getCreationDate()).isEqualTo(CREATION_DATE);
		assertThat(result.getReceptionDate()).isEqualTo(nowAsDate);
		assertThat(result.getObjectToken()).isNull();
		assertThat(result.getNotificationType()).isNull();
	}

	@Test
	void convert_ShouldNotPopulateObjectTokenAndNotPopulateNotificationType_WhenObjectIsNotAMap() {

		when(sourceMock.getObject()).thenReturn(new Object());

		final NotificationEntity result = testObj.convert(sourceMock);

		assertThat(result.getObjectToken()).isNull();
		assertThat(result.getNotificationType()).isNull();
	}

	@Test
	void convert_ShouldNotPopulateObjectTokenAndNotPopulateNotificationType_WhenObjectTokenIsNotAString() {

		when(sourceMock.getObject()).thenReturn(Map.of(OBJECT_TOKEN_MAP_KEY, new Object()));

		final NotificationEntity result = testObj.convert(sourceMock);

		assertThat(result.getObjectToken()).isNull();
		assertThat(result.getNotificationType()).isNull();
	}

	@Test
	void convert_ShouldPopulateObjectTokenAndPopulateNotificationType_WhenObjectTokenIsAString() {

		when(sourceMock.getObject()).thenReturn(Map.of(OBJECT_TOKEN_MAP_KEY, PAYMENT_OBJECT_TOKEN_MAP_VALUE));

		final NotificationEntity result = testObj.convert(sourceMock);

		assertThat(result.getObjectToken()).isEqualTo(PAYMENT_OBJECT_TOKEN_MAP_VALUE);
		assertThat(result.getNotificationType()).isEqualTo(NotificationType.PMT);
	}

	@Test
	void convert_ShouldPopulateObjectTokenAndPopulateAnUnknownNotificationType_WhenObjectTokenIsAStringAndObjectTokenHasNotAKnownPrefix() {

		when(sourceMock.getObject()).thenReturn(Map.of(OBJECT_TOKEN_MAP_KEY, UNKNOWN_OBJECT_TOKEN_MAP_VALUE));

		final NotificationEntity result = testObj.convert(sourceMock);

		assertThat(result.getObjectToken()).isEqualTo(UNKNOWN_OBJECT_TOKEN_MAP_VALUE);
		assertThat(result.getNotificationType()).isEqualTo(NotificationType.UNK);
	}

}
