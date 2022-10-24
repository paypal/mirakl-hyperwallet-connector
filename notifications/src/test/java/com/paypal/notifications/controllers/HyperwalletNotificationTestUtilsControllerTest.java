package com.paypal.notifications.controllers;

import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import com.paypal.infrastructure.repository.FailedNotificationInformationRepository;
import com.paypal.notifications.dto.NotificationInfoDTO;
import com.paypal.notifications.dto.converter.NotificationInfoDTOToNotificationInfoEntityConverter;
import com.paypal.notifications.dto.converter.NotificationInfoEntityToNotificationInfoDTOConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.paypal.notifications.controllers.HyperwalletNotificationTestUtilsController.NOTIFICATION_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class HyperwalletNotificationTestUtilsControllerTest {

	private static final String TARGET_TOKEN = "targetToken";

	private static final String NOTIFICATION_TYPE = "notificationType";

	private static final String NOTIFICATION_TOKEN = "notificationToken";

	private HyperwalletNotificationTestUtilsController testObj;

	@Mock
	private FailedNotificationInformationRepository failedNotificationInformationRepositoryMock;

	@Mock
	private NotificationInfoDTOToNotificationInfoEntityConverter notificationInfoDTOToNotificationInfoEntityConverterMock;

	@Mock
	private NotificationInfoEntityToNotificationInfoDTOConverter notificationInfoEntityToNotificationInfoDTOConverterMock;

	@Mock
	private NotificationInfoDTO notificationInfoDTO1Mock, notificationInfoDTO2Mock;

	@Mock
	private NotificationInfoEntity notificationInfoEntity1Mock, notificationInfoEntity2Mock;

	@BeforeEach
	void setUp() {
		testObj = new HyperwalletNotificationTestUtilsController(failedNotificationInformationRepositoryMock,
				notificationInfoDTOToNotificationInfoEntityConverterMock,
				notificationInfoEntityToNotificationInfoDTOConverterMock);
	}

	@Test
	void checkNotificationExistsInDatabaseByToken_whenNotificationExists_shouldReturnNotificationInfoEntity() {
		when(failedNotificationInformationRepositoryMock.findByNotificationToken(NOTIFICATION_TOKEN))
				.thenReturn(notificationInfoEntity1Mock);
		when(notificationInfoEntityToNotificationInfoDTOConverterMock.convert(notificationInfoEntity1Mock))
				.thenReturn(notificationInfoDTO1Mock);

		final ResponseEntity<Object> result = testObj.checkNotificationExistsInDatabaseByToken(NOTIFICATION_TOKEN);

		assertThat(result).extracting("status").isEqualTo(OK);
		assertThat(result.getBody()).isEqualTo(notificationInfoDTO1Mock);
	}

	@Test
	void checkNotificationExistsInDatabaseByToken_whenNotificationDoesNotExist_shouldReturnNoFound() {
		final ResponseEntity<Object> result = testObj.checkNotificationExistsInDatabaseByToken(NOTIFICATION_TOKEN);

		assertThat(result).extracting("status").isEqualTo(NOT_FOUND);
		assertThat(result.getBody()).isEqualTo(NOTIFICATION_NOT_FOUND);
	}

	@Test
	void checkNotificationExistsInDatabaseByTypeAndTarget_whenNotificationExists_shouldReturnNotification() {
		when(failedNotificationInformationRepositoryMock.findByTypeAndTarget(NOTIFICATION_TYPE, TARGET_TOKEN))
				.thenReturn(notificationInfoEntity1Mock);
		when(notificationInfoEntityToNotificationInfoDTOConverterMock.convert(notificationInfoEntity1Mock))
				.thenReturn(notificationInfoDTO1Mock);

		final ResponseEntity<Object> result = testObj
				.checkNotificationExistsInDatabaseByTypeAndTarget(NOTIFICATION_TYPE, TARGET_TOKEN);

		assertThat(result).extracting("status").isEqualTo(OK);
		assertThat(result.getBody()).isEqualTo(notificationInfoDTO1Mock);
	}

	@Test
	void checkNotificationExistsInDatabaseByTypeAndTarget_whenNotificationDoesNotExist_shouldReturnNoTFound() {
		final ResponseEntity<Object> result = testObj.checkNotificationExistsInDatabaseByToken(NOTIFICATION_TOKEN);

		assertThat(result).extracting("status").isEqualTo(NOT_FOUND);
		assertThat(result.getBody()).isEqualTo(NOTIFICATION_NOT_FOUND);
	}

	@Test
	void saveNotificationInDatabase_whenNotificationIsStoredSuccessfully_shouldStoreNotification() {
		when(notificationInfoDTOToNotificationInfoEntityConverterMock.convert(notificationInfoDTO1Mock))
				.thenReturn(notificationInfoEntity1Mock);
		testObj.saveNotificationInDatabase(notificationInfoDTO1Mock);

		verify(failedNotificationInformationRepositoryMock).save(notificationInfoEntity1Mock);
	}

	@Test
	void saveNotificationInDatabase_whenNotificationIsNotStoredSuccessfully_shouldThrowException() {
		when(notificationInfoDTOToNotificationInfoEntityConverterMock.convert(notificationInfoDTO1Mock))
				.thenReturn(notificationInfoEntity1Mock);
		when(failedNotificationInformationRepositoryMock.save(notificationInfoEntity1Mock))
				.thenThrow(new IllegalArgumentException("Something went wrong"));

		final Throwable throwable = catchThrowable(() -> testObj.saveNotificationInDatabase(notificationInfoDTO1Mock));

		verify(failedNotificationInformationRepositoryMock).save(notificationInfoEntity1Mock);
		checkExceptionWithType(throwable, INTERNAL_SERVER_ERROR);
	}

	@Test
	void replaceNotificationInDatabase_shouldClearAndAddNotifications() {
		List<NotificationInfoDTO> notificationDTOs = List.of(notificationInfoDTO1Mock, notificationInfoDTO2Mock);
		List<NotificationInfoEntity> notifications = List.of(notificationInfoEntity1Mock, notificationInfoEntity2Mock);
		when(notificationInfoDTOToNotificationInfoEntityConverterMock.convert(notificationInfoDTO1Mock))
				.thenReturn(notificationInfoEntity1Mock);
		when(notificationInfoDTOToNotificationInfoEntityConverterMock.convert(notificationInfoDTO2Mock))
				.thenReturn(notificationInfoEntity2Mock);

		testObj.replaceNotificationInDatabase(notificationDTOs);

		verify(failedNotificationInformationRepositoryMock).deleteAll();
		verify(failedNotificationInformationRepositoryMock).saveAll(notifications);
	}

	@Test
	void replaceNotificationInDatabase_whenNotificationsAreNotReplacedSuccessfully_shouldThrowException() {
		List<NotificationInfoDTO> notificationDTOs = List.of(notificationInfoDTO1Mock, notificationInfoDTO2Mock);

		doThrow(new RuntimeException("Something went wrong")).when(failedNotificationInformationRepositoryMock)
				.deleteAll();

		final Throwable throwable = catchThrowable(() -> testObj.replaceNotificationInDatabase(notificationDTOs));

		checkExceptionWithType(throwable, INTERNAL_SERVER_ERROR);
	}

	private void checkExceptionWithType(final Throwable throwable, final HttpStatus httpStatus) {
		assertThat(throwable).isInstanceOf(ResponseStatusException.class);
		assertThat(((ResponseStatusException) throwable).getStatus()).isEqualTo(httpStatus);
	}

}
