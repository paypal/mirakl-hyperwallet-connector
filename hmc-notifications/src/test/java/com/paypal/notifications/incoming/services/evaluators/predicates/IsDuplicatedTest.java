package com.paypal.notifications.incoming.services.evaluators.predicates;

import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.services.NotificationStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IsDuplicatedTest {

	private static final String WEB_HOOK_TOKEN = "webHookToken";

	@InjectMocks
	private IsDuplicated testObj;

	@Mock
	private NotificationStorageService notificationStorageService;

	@Mock
	private NotificationEntity notificationEntityMock;

	@BeforeEach
	public void setUp() {

		when(notificationEntityMock.getWebHookToken()).thenReturn(WEB_HOOK_TOKEN);
	}

	@Test
	void test_ShouldReturnFalse_WhenThereAreNotNotificationWithTheSameWebHookToken() {

		when(notificationStorageService.getNotificationsByWebHookToken(WEB_HOOK_TOKEN)).thenReturn(List.of());

		final boolean result = testObj.test(notificationEntityMock);

		assertThat(result).isFalse();
	}

	@Test
	void test_ShouldReturnFalse_WhenThereIsOneNotificationWithTheSameWebHookToken() {

		when(notificationStorageService.getNotificationsByWebHookToken(WEB_HOOK_TOKEN))
				.thenReturn(List.of(new NotificationEntity()));

		final boolean result = testObj.test(notificationEntityMock);

		assertThat(result).isFalse();
	}

	@Test
	void test_ShouldReturnTrue_WhenThereAreMoreThanOneNotificationWithTheSameWebHookToken() {

		when(notificationStorageService.getNotificationsByWebHookToken(WEB_HOOK_TOKEN))
				.thenReturn(List.of(new NotificationEntity(), new NotificationEntity()));

		final boolean result = testObj.test(notificationEntityMock);

		assertThat(result).isTrue();
	}

}
