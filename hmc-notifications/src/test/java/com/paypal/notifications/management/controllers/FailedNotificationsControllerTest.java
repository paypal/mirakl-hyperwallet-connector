package com.paypal.notifications.management.controllers;

import com.paypal.notifications.management.controllers.converters.FailedNotificationInfoConverter;
import com.paypal.notifications.management.controllers.dtos.FailedNotificationInfo;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationStatus;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import com.paypal.notifications.storage.services.NotificationStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FailedNotificationsControllerTest {

	private static final String WEBHOOK_TOKEN = "wbh-test-token-001";

	private static final String OBJECT_TOKEN = "usr-test-obj-001";

	private static final String PROGRAM_TOKEN = "prg-test-prg-001";

	@InjectMocks
	private FailedNotificationsController testObj;

	@Mock
	private NotificationStorageService notificationStorageServiceMock;

	@Mock
	private FailedNotificationInfoConverter converterMock;

	@Mock
	private PagedResourcesAssembler<FailedNotificationInfo> pagedResourcesAssemblerMock;

	// ── find ─────────────────────────────────────────────────────────────────────

	@Test
	void find_withNoFilters_shouldQueryWithNullTypeAndNullTarget() {
		final Page<NotificationEntity> entityPage = new PageImpl<>(List.of());
		final Page<FailedNotificationInfo> dtoPage = new PageImpl<>(List.of());
		final PagedModel<EntityModel<FailedNotificationInfo>> pagedModel = PagedModel.empty();

		given(notificationStorageServiceMock.getFailedNotifications(null, null, Pageable.unpaged()))
				.willReturn(entityPage);
		given(converterMock.from(entityPage)).willReturn(dtoPage);
		given(pagedResourcesAssemblerMock.toModel(dtoPage)).willReturn(pagedModel);

		final PagedModel<EntityModel<FailedNotificationInfo>> result = testObj.find(null, null, Pageable.unpaged());

		assertThat(result).isSameAs(pagedModel);
		verify(notificationStorageServiceMock).getFailedNotifications(null, null, Pageable.unpaged());
	}

	@Test
	void find_withTypeFilter_shouldConvertTypeToEnumAndPassToService() {
		final Page<NotificationEntity> entityPage = new PageImpl<>(List.of());
		final Page<FailedNotificationInfo> dtoPage = new PageImpl<>(List.of());
		final PagedModel<EntityModel<FailedNotificationInfo>> pagedModel = PagedModel.empty();
		final Pageable pageable = PageRequest.of(0, 10);

		given(notificationStorageServiceMock.getFailedNotifications(NotificationType.USR, null, pageable))
				.willReturn(entityPage);
		given(converterMock.from(entityPage)).willReturn(dtoPage);
		given(pagedResourcesAssemblerMock.toModel(dtoPage)).willReturn(pagedModel);

		testObj.find("USR", null, pageable);

		verify(notificationStorageServiceMock).getFailedNotifications(NotificationType.USR, null, pageable);
	}

	@Test
	void find_withTargetFilter_shouldPassTargetToService() {
		final Page<NotificationEntity> entityPage = new PageImpl<>(List.of());
		final Page<FailedNotificationInfo> dtoPage = new PageImpl<>(List.of());
		final PagedModel<EntityModel<FailedNotificationInfo>> pagedModel = PagedModel.empty();
		final Pageable pageable = PageRequest.of(0, 10);

		given(notificationStorageServiceMock.getFailedNotifications(null, OBJECT_TOKEN, pageable))
				.willReturn(entityPage);
		given(converterMock.from(entityPage)).willReturn(dtoPage);
		given(pagedResourcesAssemblerMock.toModel(dtoPage)).willReturn(pagedModel);

		testObj.find(null, OBJECT_TOKEN, pageable);

		verify(notificationStorageServiceMock).getFailedNotifications(null, OBJECT_TOKEN, pageable);
	}

	@Test
	void find_withUnknownTypeString_shouldFallBackToUNK() {
		final Page<NotificationEntity> entityPage = new PageImpl<>(List.of());
		final Page<FailedNotificationInfo> dtoPage = new PageImpl<>(List.of());
		final PagedModel<EntityModel<FailedNotificationInfo>> pagedModel = PagedModel.empty();
		final Pageable pageable = Pageable.unpaged();

		given(notificationStorageServiceMock.getFailedNotifications(NotificationType.UNK, null, pageable))
				.willReturn(entityPage);
		given(converterMock.from(entityPage)).willReturn(dtoPage);
		given(pagedResourcesAssemblerMock.toModel(dtoPage)).willReturn(pagedModel);

		testObj.find("TOTALLY_UNKNOWN", null, pageable);

		verify(notificationStorageServiceMock).getFailedNotifications(NotificationType.UNK, null, pageable);
	}

	// ── get ──────────────────────────────────────────────────────────────────────

	@Test
	void get_whenTokenExists_shouldReturn200WithDto() {
		final NotificationEntity entity = buildEntity(WEBHOOK_TOKEN);
		final FailedNotificationInfo dto = buildDto(WEBHOOK_TOKEN);

		given(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN))
				.willReturn(Optional.of(entity));
		given(converterMock.from(entity)).willReturn(dto);

		final ResponseEntity<FailedNotificationInfo> response = testObj.get(WEBHOOK_TOKEN);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(dto);
	}

	@Test
	void get_whenTokenNotFound_shouldReturn404() {
		given(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN)).willReturn(Optional.empty());

		final ResponseEntity<FailedNotificationInfo> response = testObj.get(WEBHOOK_TOKEN);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	// ── add ──────────────────────────────────────────────────────────────────────

	@Test
	void add_shouldPersistEntityWithFailedStatusAndCurrentReceptionDate() {
		final FailedNotificationInfo info = buildDto(WEBHOOK_TOKEN);
		info.setType("USR");
		info.setTarget(OBJECT_TOKEN);
		info.setProgram(PROGRAM_TOKEN);
		info.setRetryCounter(2);

		given(notificationStorageServiceMock.saveNotification(any(NotificationEntity.class)))
				.willAnswer(inv -> inv.getArgument(0));

		testObj.add(info);

		verify(notificationStorageServiceMock).saveNotification(any(NotificationEntity.class));
	}

	// ── update ───────────────────────────────────────────────────────────────────

	@Test
	void update_whenTokenExists_shouldApplyChangesAndReturn200() {
		final NotificationEntity entity = buildEntity(WEBHOOK_TOKEN);
		final FailedNotificationInfo info = buildDto(WEBHOOK_TOKEN);
		info.setProgram("prg-updated");
		info.setRetryCounter(5);

		given(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN))
				.willReturn(Optional.of(entity));
		given(notificationStorageServiceMock.saveNotification(entity)).willReturn(entity);
		given(converterMock.from(entity)).willReturn(info);

		final ResponseEntity<FailedNotificationInfo> response = testObj.update(WEBHOOK_TOKEN, info);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(entity.getProgram()).isEqualTo("prg-updated");
		assertThat(entity.getRetryCounter()).isEqualTo(5);
	}

	@Test
	void update_whenTokenNotFound_shouldReturn404() {
		given(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN)).willReturn(Optional.empty());

		final ResponseEntity<FailedNotificationInfo> response = testObj.update(WEBHOOK_TOKEN, buildDto(WEBHOOK_TOKEN));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		verify(notificationStorageServiceMock, never()).saveNotification(any());
	}

	// ── replace ──────────────────────────────────────────────────────────────────

	@Test
	void replace_shouldDeleteAllExistingFailedAndPersistNewOnes() {
		final NotificationEntity existing = buildEntity("wbh-old-001");
		final Page<NotificationEntity> existingPage = new PageImpl<>(List.of(existing));
		final FailedNotificationInfo newInfo = buildDto("wbh-new-001");

		given(notificationStorageServiceMock.getFailedNotifications(null, null, Pageable.unpaged()))
				.willReturn(existingPage);
		willDoNothing().given(notificationStorageServiceMock).deleteNotificationByWebHookToken("wbh-old-001");
		given(notificationStorageServiceMock.saveNotification(any(NotificationEntity.class)))
				.willAnswer(inv -> inv.getArgument(0));

		testObj.replace(List.of(newInfo));

		verify(notificationStorageServiceMock).deleteNotificationByWebHookToken("wbh-old-001");
		verify(notificationStorageServiceMock).saveNotification(any(NotificationEntity.class));
	}

	@Test
	void replace_withEmptyList_shouldDeleteAllAndPersistNothing() {
		final NotificationEntity existing = buildEntity("wbh-old-001");
		final Page<NotificationEntity> existingPage = new PageImpl<>(List.of(existing));

		given(notificationStorageServiceMock.getFailedNotifications(null, null, Pageable.unpaged()))
				.willReturn(existingPage);
		willDoNothing().given(notificationStorageServiceMock).deleteNotificationByWebHookToken("wbh-old-001");

		testObj.replace(List.of());

		verify(notificationStorageServiceMock).deleteNotificationByWebHookToken("wbh-old-001");
		verify(notificationStorageServiceMock, never()).saveNotification(any());
	}

	// ── delete ───────────────────────────────────────────────────────────────────

	@Test
	void delete_whenTokenExists_shouldDeleteAndReturn200() {
		final NotificationEntity entity = buildEntity(WEBHOOK_TOKEN);
		given(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN))
				.willReturn(Optional.of(entity));
		willDoNothing().given(notificationStorageServiceMock).deleteNotificationByWebHookToken(WEBHOOK_TOKEN);

		final ResponseEntity<Void> response = testObj.delete(WEBHOOK_TOKEN);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(notificationStorageServiceMock).deleteNotificationByWebHookToken(WEBHOOK_TOKEN);
	}

	@Test
	void delete_whenTokenNotFound_shouldReturn404() {
		given(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN)).willReturn(Optional.empty());

		final ResponseEntity<Void> response = testObj.delete(WEBHOOK_TOKEN);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		verify(notificationStorageServiceMock, never()).deleteNotificationByWebHookToken(any());
	}

	// ── find — RETRYING included ──────────────────────────────────────────────────

	@Test
	void find_shouldIncludeRetryingNotificationsAlongsideFailed() {
		// The service is expected to return both FAILED and RETRYING; the controller
		// must pass them through without any status-based filtering of its own.
		final NotificationEntity failedEntity = buildEntity(WEBHOOK_TOKEN);
		final NotificationEntity retryingEntity = buildEntity("wbh-retrying-001");
		retryingEntity.setStatus(NotificationStatus.RETRYING);
		retryingEntity.setRetryCounter(2);

		final Page<NotificationEntity> entityPage = new PageImpl<>(List.of(failedEntity, retryingEntity));
		final FailedNotificationInfo failedDto = buildDto(WEBHOOK_TOKEN);
		final FailedNotificationInfo retryingDto = buildDto("wbh-retrying-001");
		final Page<FailedNotificationInfo> dtoPage = new PageImpl<>(List.of(failedDto, retryingDto));
		final PagedModel<EntityModel<FailedNotificationInfo>> pagedModel = PagedModel.empty();

		given(notificationStorageServiceMock.getFailedNotifications(null, null, Pageable.unpaged()))
				.willReturn(entityPage);
		given(converterMock.from(entityPage)).willReturn(dtoPage);
		given(pagedResourcesAssemblerMock.toModel(dtoPage)).willReturn(pagedModel);

		final PagedModel<EntityModel<FailedNotificationInfo>> result = testObj.find(null, null, Pageable.unpaged());

		assertThat(result).isSameAs(pagedModel);
	}

	// ── replace — RETRYING included ───────────────────────────────────────────────

	@Test
	void replace_shouldAlsoDeleteRetryingNotificationsBeforePersistingNewList() {
		final NotificationEntity failedEntity = buildEntity("wbh-failed-001");
		final NotificationEntity retryingEntity = buildEntity("wbh-retrying-001");
		retryingEntity.setStatus(NotificationStatus.RETRYING);

		final Page<NotificationEntity> existingPage = new PageImpl<>(List.of(failedEntity, retryingEntity));

		given(notificationStorageServiceMock.getFailedNotifications(null, null, Pageable.unpaged()))
				.willReturn(existingPage);
		willDoNothing().given(notificationStorageServiceMock).deleteNotificationByWebHookToken(any());

		testObj.replace(List.of());

		verify(notificationStorageServiceMock).deleteNotificationByWebHookToken("wbh-failed-001");
		verify(notificationStorageServiceMock).deleteNotificationByWebHookToken("wbh-retrying-001");
	}

	// ── delete — works on RETRYING ────────────────────────────────────────────────

	@Test
	void delete_whenTokenBelongsToRetryingNotification_shouldDeleteAndReturn200() {
		final NotificationEntity retryingEntity = buildEntity(WEBHOOK_TOKEN);
		retryingEntity.setStatus(NotificationStatus.RETRYING);
		retryingEntity.setRetryCounter(1);

		given(notificationStorageServiceMock.getNotificationByWebHookToken(WEBHOOK_TOKEN))
				.willReturn(Optional.of(retryingEntity));
		willDoNothing().given(notificationStorageServiceMock).deleteNotificationByWebHookToken(WEBHOOK_TOKEN);

		final ResponseEntity<Void> response = testObj.delete(WEBHOOK_TOKEN);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(notificationStorageServiceMock).deleteNotificationByWebHookToken(WEBHOOK_TOKEN);
	}

	// ── helpers ──────────────────────────────────────────────────────────────────

	private NotificationEntity buildEntity(final String token) {
		final NotificationEntity entity = new NotificationEntity();
		entity.setWebHookToken(token);
		entity.setObjectToken(OBJECT_TOKEN);
		entity.setProgram(PROGRAM_TOKEN);
		entity.setNotificationType(NotificationType.USR);
		entity.setStatus(NotificationStatus.FAILED);
		entity.setCreationDate(new Date());
		entity.setReceptionDate(new Date());
		return entity;
	}

	private FailedNotificationInfo buildDto(final String token) {
		final FailedNotificationInfo dto = new FailedNotificationInfo();
		dto.setNotificationToken(token);
		dto.setTarget(OBJECT_TOKEN);
		dto.setProgram(PROGRAM_TOKEN);
		dto.setType("USR");
		dto.setCreationDate(new Date());
		return dto;
	}

}
