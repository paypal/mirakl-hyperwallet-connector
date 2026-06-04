package com.paypal.notifications.storage.repositories.entities;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationEntityTest {

	@Test
	void newNotificationEntity_shouldHaveDefaultStatusPending() {
		final NotificationEntity entity = new NotificationEntity();

		assertThat(entity.getStatus()).isEqualTo(NotificationStatus.PENDING);
	}

	@Test
	void newNotificationEntity_shouldHaveDefaultRetryCounterZero() {
		final NotificationEntity entity = new NotificationEntity();

		assertThat(entity.getRetryCounter()).isZero();
	}

	@Test
	void setStatus_shouldUpdateStatus() {
		final NotificationEntity entity = new NotificationEntity();
		entity.setStatus(NotificationStatus.RETRYING);

		assertThat(entity.getStatus()).isEqualTo(NotificationStatus.RETRYING);
	}

	@Test
	void setRetryCounter_shouldUpdateRetryCounter() {
		final NotificationEntity entity = new NotificationEntity();
		entity.setRetryCounter(3);

		assertThat(entity.getRetryCounter()).isEqualTo(3);
	}

	@Test
	void setProgram_shouldUpdateProgram() {
		final NotificationEntity entity = new NotificationEntity();
		entity.setProgram("prg-token-123");

		assertThat(entity.getProgram()).isEqualTo("prg-token-123");
	}

	@Test
	void notificationEntity_shouldStoreAllFields() {
		final Date now = new Date();
		final NotificationEntity entity = new NotificationEntity();
		entity.setWebHookToken("wbh-token-1");
		entity.setObjectToken("usr-token-1");
		entity.setCreationDate(now);
		entity.setReceptionDate(now);
		entity.setNotificationType(NotificationType.USR);
		entity.setStatus(NotificationStatus.SUCCESS);
		entity.setRetryCounter(2);
		entity.setProgram("prg-token-1");

		assertThat(entity.getWebHookToken()).isEqualTo("wbh-token-1");
		assertThat(entity.getObjectToken()).isEqualTo("usr-token-1");
		assertThat(entity.getCreationDate()).isEqualTo(now);
		assertThat(entity.getReceptionDate()).isEqualTo(now);
		assertThat(entity.getNotificationType()).isEqualTo(NotificationType.USR);
		assertThat(entity.getStatus()).isEqualTo(NotificationStatus.SUCCESS);
		assertThat(entity.getRetryCounter()).isEqualTo(2);
		assertThat(entity.getProgram()).isEqualTo("prg-token-1");
	}

}
