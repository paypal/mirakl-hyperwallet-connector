package com.paypal.notifications.management.controllers;

import com.paypal.notifications.failures.repositories.FailedNotificationInformationRepository;
import com.paypal.notifications.management.controllers.converters.FailedNotificationsDtoConverter;
import com.paypal.notifications.management.controllers.dto.FailedNotificationInfoDTO;
import com.paypal.notifications.storage.repositories.entities.NotificationInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Rest controller that adds utilities for notifications used in automated tests
 */
@Slf4j
@RestController
@RequestMapping("/management/failed-notifications")
public class FailedNotificationsManagementController {

	public static final String MSG_NOTIFICATION_NOT_FOUND = "Notification not found";

	private final FailedNotificationInformationRepository failedNotificationInformationRepository;

	private final FailedNotificationsDtoConverter failedNotificationsDtoConverter;

	private final PagedResourcesAssembler<FailedNotificationInfoDTO> pagedResourcesAssembler;

	public FailedNotificationsManagementController(
			final FailedNotificationInformationRepository failedNotificationInformationRepository,
			final FailedNotificationsDtoConverter failedNotificationsDtoConverter,
			final PagedResourcesAssembler<FailedNotificationInfoDTO> pagedResourcesAssembler) {
		this.failedNotificationInformationRepository = failedNotificationInformationRepository;
		this.failedNotificationsDtoConverter = failedNotificationsDtoConverter;
		this.pagedResourcesAssembler = pagedResourcesAssembler;
	}

	@GetMapping("/")
	public PagedModel<EntityModel<FailedNotificationInfoDTO>> findAll(final Pageable pageable) {
		final Page<NotificationInfoEntity> notifications = failedNotificationInformationRepository.findAll(pageable);

		return pagedResourcesAssembler.toModel(failedNotificationsDtoConverter.from(notifications));
	}

	@GetMapping(value = "/", params = { "type", "target" })
	public PagedModel<EntityModel<FailedNotificationInfoDTO>> findAll(final Pageable pageable,
			@RequestParam final String type, @RequestParam final String target) {
		final Page<NotificationInfoEntity> notifications = failedNotificationInformationRepository
				.findByTypeAndTarget(pageable, type, target);

		return pagedResourcesAssembler.toModel(failedNotificationsDtoConverter.from(notifications));
	}

	@PostMapping("/")
	@ResponseStatus(CREATED)
	public void add(@RequestBody final FailedNotificationInfoDTO notification) {
		failedNotificationInformationRepository.save(failedNotificationsDtoConverter.from(notification));
	}

	@PutMapping("/")
	public void replaceAll(@RequestBody final List<FailedNotificationInfoDTO> notifications) {
		failedNotificationInformationRepository.deleteAll();
		failedNotificationInformationRepository.saveAll(
				notifications.stream().map(failedNotificationsDtoConverter::from).collect(Collectors.toList()));
	}

	@GetMapping("/{notificationToken}")
	public EntityModel<FailedNotificationInfoDTO> get(@PathVariable final String notificationToken) {
		final NotificationInfoEntity notification = failedNotificationInformationRepository
				.findByNotificationToken(notificationToken);
		if (notification == null) {
			throw new ResponseStatusException(NOT_FOUND, MSG_NOTIFICATION_NOT_FOUND);
		}

		return EntityModel.of(failedNotificationsDtoConverter.from(notification));
	}

	@PutMapping("/{notificationToken}")
	public void update(@PathVariable final String notificationToken,
			@RequestBody final FailedNotificationInfoDTO notification) {
		notification.setNotificationToken(notificationToken);
		failedNotificationInformationRepository.save(failedNotificationsDtoConverter.from(notification));
	}

	@DeleteMapping("/{notificationToken}")
	public void delete(@PathVariable final String notificationToken) {
		final NotificationInfoEntity notification = failedNotificationInformationRepository
				.findByNotificationToken(notificationToken);
		if (notification != null) {
			failedNotificationInformationRepository.delete(notification);
		}
	}

}
