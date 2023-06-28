package com.paypal.notifications.events.services.eventpublishers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.events.model.HMCEvent;
import com.paypal.notifications.events.services.eventpublishers.AbstractHMCEventSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AbstractHMCEventSenderTest {

	@InjectMocks
	private MyHMCEventSender testObj;

	@Mock
	private ApplicationEventPublisher applicationEventPublisherMock;

	@Mock
	private HyperwalletWebhookNotification notificationMock;

	@Captor
	private ArgumentCaptor<HMCEvent> hmcEventArgumentCaptor;

	@Test
	void execute_shouldPublishEventWithNotification() {
		testObj.execute(notificationMock);

		verify(applicationEventPublisherMock).publishEvent(hmcEventArgumentCaptor.capture());
		final HMCEvent event = hmcEventArgumentCaptor.getValue();
		assertThat(event.getNotification()).isEqualTo(notificationMock);
	}

	private static class MyHMCEventSender extends AbstractHMCEventSender {

		@Override
		protected HMCEvent getEvent(final HyperwalletWebhookNotification notification) {
			return new MyEvent(this, notification);
		}

	}

	private static class MyEvent extends HMCEvent {

		MyEvent(final Object source, final HyperwalletWebhookNotification notification) {
			super(source, notification);
		}

	}

}
