package com.paypal.notifications.evaluator.predicates;

import com.paypal.notifications.model.entity.NotificationEntity;
import com.paypal.notifications.service.hmc.NotificationEntityService;
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
	private NotificationEntityService notificationEntityService;

	@Mock
	private NotificationEntity notificationEntityMock;

	@BeforeEach
	public void setUp() {

		when(notificationEntityMock.getWebHookToken()).thenReturn(WEB_HOOK_TOKEN);
	}

	@Test
	void test_ShouldReturnFalse_WhenThereAreNotNotificationWithTheSameWebHookToken() {

		when(notificationEntityService.getNotificationsByWebHookToken(WEB_HOOK_TOKEN)).thenReturn(List.of());

		final boolean result = testObj.test(notificationEntityMock);

		assertThat(result).isFalse();
	}

	@Test
	void test_ShouldReturnFalse_WhenThereIsOneNotificationWithTheSameWebHookToken() {

		when(notificationEntityService.getNotificationsByWebHookToken(WEB_HOOK_TOKEN))
				.thenReturn(List.of(new NotificationEntity()));

		final boolean result = testObj.test(notificationEntityMock);

		assertThat(result).isFalse();
	}

	@Test
	void test_ShouldReturnTrue_WhenThereAreMoreThanOneNotificationWithTheSameWebHookToken() {

		when(notificationEntityService.getNotificationsByWebHookToken(WEB_HOOK_TOKEN))
				.thenReturn(List.of(new NotificationEntity(), new NotificationEntity()));

		final boolean result = testObj.test(notificationEntityMock);

		assertThat(result).isTrue();
	}

}
