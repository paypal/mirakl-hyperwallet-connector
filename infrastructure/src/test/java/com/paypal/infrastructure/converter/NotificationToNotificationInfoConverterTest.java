package com.paypal.infrastructure.converter;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationToNotificationInfoConverterTest {

	private static final String TARGET_TOKEN = "targetToken";

	private static final String NOTIFICATION_TYPE = "PAYMENT";

	private static final String PROGRAM_TOKEN = "programToken";

	private static final String NOTIFICATION_TOKEN = "not-1234-1234";

	private final NotificationToNotificationInfoConverter testObj = new NotificationToNotificationInfoConverter();

	@Mock
	private Date dateMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@BeforeEach
	void setUp() {
		when(hyperwalletWebhookNotificationMock.getToken()).thenReturn(NOTIFICATION_TOKEN);
		when(hyperwalletWebhookNotificationMock.getType()).thenReturn(NOTIFICATION_TYPE);
		when(hyperwalletWebhookNotificationMock.getCreatedOn()).thenReturn(dateMock);
	}

	@Test
	void convert_whenNotificationObjectIsNotAMap_shouldSetNotificationTokenAndTypeAndDate() {
		when(hyperwalletWebhookNotificationMock.getObject()).thenReturn("not a map");

		final NotificationInfoEntity result = testObj.convert(hyperwalletWebhookNotificationMock);

		assertThat(result).hasNoNullFieldsOrPropertiesExcept("program", "target");
		assertThat(result.getNotificationToken()).isEqualTo(NOTIFICATION_TOKEN);
		assertThat(result.getType()).isEqualTo(NOTIFICATION_TYPE);
		assertThat(result.getCreationDate()).isEqualTo(dateMock);
	}

	@Test
	void convert_whenNotificationObjectIsAMap_andDoesNotContainTokens_shouldSetNotificationTokenAndTypeAndDate() {
		when(hyperwalletWebhookNotificationMock.getObject()).thenReturn(Map.of("somethingThatIsNotAToken", 1234));

		final NotificationInfoEntity result = testObj.convert(hyperwalletWebhookNotificationMock);

		assertThat(result).hasNoNullFieldsOrPropertiesExcept("program", "target");
		assertThat(result.getNotificationToken()).isEqualTo(NOTIFICATION_TOKEN);
		assertThat(result.getType()).isEqualTo(NOTIFICATION_TYPE);
		assertThat(result.getCreationDate()).isEqualTo(dateMock);
	}

	@Test
	void convert_whenNotificationObjectIsAMap_andMapContainsTargetTokenAndProgramToken_shouldPopulateAllFields() {
		when(hyperwalletWebhookNotificationMock.getObject())
				.thenReturn(Map.of("token", TARGET_TOKEN, "programToken", PROGRAM_TOKEN));

		final NotificationInfoEntity result = testObj.convert(hyperwalletWebhookNotificationMock);

		assertThat(result.getNotificationToken()).isEqualTo(NOTIFICATION_TOKEN);
		assertThat(result.getType()).isEqualTo(NOTIFICATION_TYPE);
		assertThat(result.getTarget()).isEqualTo(TARGET_TOKEN);
		assertThat(result.getProgram()).isEqualTo(PROGRAM_TOKEN);
		assertThat(result.getCreationDate()).isEqualTo(dateMock);
	}

}
