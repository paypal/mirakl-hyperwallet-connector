package com.paypal.notifications.incoming.cache;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookNotificationRetrieverImplTest {

	private static final String WEBHOOK_TOKEN = "wbh-token-1";

	private static final String PROGRAM_TOKEN = "prg-token-1";

	@InjectMocks
	private WebhookNotificationRetrieverImpl testObj;

	@Mock
	private CacheManager cacheManagerMock;

	@Mock
	private UserHyperwalletSDKService userHyperwalletSDKServiceMock;

	@Mock
	private Cache cacheMock;

	@Mock
	private Cache.ValueWrapper valueWrapperMock;

	@Mock
	private Hyperwallet hyperwalletMock;

	@BeforeEach
	void setUp() {
		lenient().when(cacheManagerMock.getCache(NotificationCacheConfiguration.WEBHOOK_NOTIFICATION_CACHE))
				.thenReturn(cacheMock);
	}

	// ── get ───────────────────────────────────────────────────────────────────────

	@Test
	void get_whenCacheHit_shouldReturnCachedValueWithoutCallingSDK() {
		final HyperwalletWebhookNotification cached = new HyperwalletWebhookNotification();
		when(cacheMock.get(WEBHOOK_TOKEN)).thenReturn(valueWrapperMock);
		when(valueWrapperMock.get()).thenReturn(cached);

		final HyperwalletWebhookNotification result = testObj.get(PROGRAM_TOKEN, WEBHOOK_TOKEN);

		assertThat(result).isSameAs(cached);
		verifyNoInteractions(userHyperwalletSDKServiceMock);
	}

	@Test
	void get_whenCacheMiss_shouldFetchFromSdkAndCacheResult() {
		final HyperwalletWebhookNotification sdkResult = new HyperwalletWebhookNotification();
		sdkResult.setToken(WEBHOOK_TOKEN);

		when(cacheMock.get(WEBHOOK_TOKEN)).thenReturn(null);
		when(userHyperwalletSDKServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN))
				.thenReturn(hyperwalletMock);
		when(hyperwalletMock.getWebhookEvent(WEBHOOK_TOKEN)).thenReturn(sdkResult);

		final HyperwalletWebhookNotification result = testObj.get(PROGRAM_TOKEN, WEBHOOK_TOKEN);

		assertThat(result).isSameAs(sdkResult);
		verify(cacheMock).put(WEBHOOK_TOKEN, sdkResult);
	}

	@Test
	void get_whenCacheMissAndSdkThrows_shouldReturnNullAndNotCache() {
		when(cacheMock.get(WEBHOOK_TOKEN)).thenReturn(null);
		when(userHyperwalletSDKServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN))
				.thenReturn(hyperwalletMock);
		when(hyperwalletMock.getWebhookEvent(WEBHOOK_TOKEN)).thenThrow(new HyperwalletException("error"));

		final HyperwalletWebhookNotification result = testObj.get(PROGRAM_TOKEN, WEBHOOK_TOKEN);

		assertThat(result).isNull();
		verify(cacheMock, never()).put(any(), any());
	}

	// ── put ───────────────────────────────────────────────────────────────────────

	@Test
	void put_shouldStoreNotificationByToken() {
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();
		notification.setToken(WEBHOOK_TOKEN);

		testObj.put(notification);

		verify(cacheMock).put(WEBHOOK_TOKEN, notification);
	}

	@Test
	void put_whenNotificationIsNull_shouldDoNothing() {
		testObj.put(null);

		verify(cacheMock, never()).put(any(), any());
	}

	@Test
	void put_whenNotificationTokenIsNull_shouldDoNothing() {
		final HyperwalletWebhookNotification notification = new HyperwalletWebhookNotification();

		testObj.put(notification);

		verify(cacheMock, never()).put(any(), any());
	}

}
