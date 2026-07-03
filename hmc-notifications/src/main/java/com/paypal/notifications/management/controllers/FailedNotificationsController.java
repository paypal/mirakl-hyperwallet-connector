package com.paypal.notifications.management.controllers;

import com.paypal.notifications.management.controllers.converters.FailedNotificationInfoConverter;
import com.paypal.notifications.management.controllers.dtos.FailedNotificationInfo;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationStatus;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import com.paypal.notifications.storage.services.NotificationStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * REST controller for inspecting and managing failed notifications.
 * <p>
 * Implements the {@code /management/failed-notifications/} endpoints defined in
 * {@code management-api.yaml}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/management/failed-notifications")
public class FailedNotificationsController {

	private final NotificationStorageService notificationStorageService;

	private final FailedNotificationInfoConverter converter;

	private final PagedResourcesAssembler<FailedNotificationInfo> pagedResourcesAssembler;

	/**
	 * Returns a paged list of failed and retrying notifications, optionally filtered by
	 * type and target (object token).
	 * <p>
	 * {@code RETRYING} notifications are included alongside {@code FAILED} ones so that
	 * consumers can inspect in-progress failure recovery and determine whether a
	 * notification is "fully failed" based on its {@code retryCounter}.
	 * @param type optional notification-type filter (matches the {@link NotificationType}
	 * enum name, e.g. {@code USR}).
	 * @param target optional object-token filter.
	 * @param pageable pagination and sort information resolved from {@code page},
	 * {@code size} and {@code sort} query parameters.
	 * @return a paged collection of {@link FailedNotificationInfo} DTOs.
	 */
	@GetMapping("/")
	@ResponseStatus(OK)
	public PagedModel<EntityModel<FailedNotificationInfo>> find(@RequestParam(required = false) final String type,
			@RequestParam(required = false) final String target, final Pageable pageable) {

		final NotificationType notificationType = parseType(type);
		final Page<NotificationEntity> page = notificationStorageService.getFailedNotifications(notificationType,
				target, pageable);

		return pagedResourcesAssembler.toModel(converter.from(page));
	}

	/**
	 * Retrieves a single failed notification by its webhook token.
	 * @param notificationToken the webhook token of the notification.
	 * @return {@code 200 OK} with the notification DTO, or {@code 404 Not Found} if no
	 * notification with the given token exists.
	 */
	@GetMapping("/{notificationToken}")
	public ResponseEntity<FailedNotificationInfo> get(@PathVariable final String notificationToken) {
		return notificationStorageService.getNotificationByWebHookToken(notificationToken)
			.map(converter::from)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Adds a new failed notification entry.
	 * @param info the notification data to persist.
	 * @return {@code 201 Created}.
	 */
	@PostMapping("/")
	@ResponseStatus(CREATED)
	public void add(@RequestBody final FailedNotificationInfo info) {
		final NotificationEntity entity = toEntity(info);
		entity.setStatus(NotificationStatus.FAILED);
		entity.setReceptionDate(new Date());
		notificationStorageService.saveNotification(entity);
	}

	/**
	 * Updates the mutable fields of an existing failed notification.
	 * @param notificationToken the webhook token identifying the notification to update.
	 * @param info the updated notification data.
	 * @return {@code 200 OK} with the updated notification DTO, or {@code 404 Not Found}
	 * if no notification with the given token exists.
	 */
	@PutMapping("/{notificationToken}")
	public ResponseEntity<FailedNotificationInfo> update(@PathVariable final String notificationToken,
			@RequestBody final FailedNotificationInfo info) {
		return notificationStorageService.getNotificationByWebHookToken(notificationToken).map(entity -> {
			applyUpdate(entity, info);
			final NotificationEntity saved = notificationStorageService.saveNotification(entity);
			return ResponseEntity.ok(converter.from(saved));
		}).orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Replaces the entire list of failed/retrying notifications with the provided data.
	 * All existing {@code FAILED} and {@code RETRYING} notifications are deleted first,
	 * then the new list is persisted.
	 * @param infos the replacement list of notification data.
	 */
	@PutMapping("/")
	@ResponseStatus(OK)
	public void replace(@RequestBody final List<FailedNotificationInfo> infos) {
		notificationStorageService.getFailedNotifications(null, null, Pageable.unpaged())
			.getContent()
			.forEach(e -> notificationStorageService.deleteNotificationByWebHookToken(e.getWebHookToken()));

		infos.forEach(info -> {
			final NotificationEntity entity = toEntity(info);
			entity.setStatus(NotificationStatus.FAILED);
			entity.setReceptionDate(new Date());
			notificationStorageService.saveNotification(entity);
		});
	}

	/**
	 * Deletes the failed notification identified by the given token.
	 * @param notificationToken the webhook token of the notification to delete.
	 * @return {@code 200 OK} if deleted, or {@code 404 Not Found} if not found.
	 */
	@DeleteMapping("/{notificationToken}")
	public ResponseEntity<Void> delete(@PathVariable final String notificationToken) {
		return notificationStorageService.getNotificationByWebHookToken(notificationToken).map(entity -> {
			notificationStorageService.deleteNotificationByWebHookToken(notificationToken);
			return ResponseEntity.ok().<Void>build();
		}).orElse(ResponseEntity.notFound().build());
	}

	// ── helpers ───────────────────────────────────────────────────────────────────

	private NotificationType parseType(final String type) {
		if (type == null || type.isBlank()) {
			return null;
		}
		try {
			return NotificationType.valueOf(type.toUpperCase());
		}
		catch (final IllegalArgumentException e) {
			return NotificationType.UNK;
		}
	}

	private NotificationEntity toEntity(final FailedNotificationInfo info) {
		final NotificationEntity entity = new NotificationEntity();
		entity.setWebHookToken(info.getNotificationToken());
		entity.setObjectToken(info.getTarget());
		entity.setProgram(info.getProgram());
		entity.setRetryCounter(info.getRetryCounter());
		entity.setCreationDate(info.getCreationDate());
		if (info.getType() != null) {
			entity.setNotificationType(parseType(info.getType()));
		}
		return entity;
	}

	private void applyUpdate(final NotificationEntity entity, final FailedNotificationInfo info) {
		if (info.getTarget() != null) {
			entity.setObjectToken(info.getTarget());
		}
		if (info.getProgram() != null) {
			entity.setProgram(info.getProgram());
		}
		if (info.getType() != null) {
			entity.setNotificationType(parseType(info.getType()));
		}
		entity.setRetryCounter(info.getRetryCounter());
	}

}
