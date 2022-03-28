package com.paypal.notifications.controllers;

import com.paypal.notifications.exceptions.DateIntervalException;
import com.paypal.notifications.model.entity.NotificationEntity;
import com.paypal.notifications.service.hmc.NotificationEntityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

/**
 * Rest controller for {@link NotificationEntity}.
 */
@RestController
@RequestMapping(value = "/webhooks")
public class NotificationsController {

	@Resource
	private NotificationEntityService notificationEntityService;

	/**
	 * Retrieves all the {@link NotificationEntity} whose date are between the given
	 * dates.
	 * <p>
	 * A {@link DateIntervalException} will be thrown if {@code from} date is after thant
	 * {@code to} date.
	 * @param from from {@link Date}.
	 * @param to to {@link Date}.
	 * @return a {@link List} of {@link NotificationEntity} whose reception date are
	 * between the given from and to dates.
	 */
	@GetMapping("/notifications")
	@ResponseStatus(OK)
	public List<NotificationEntity> getAllNotifications(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date to) {

		checkDateIntervals(from, to);

		return notificationEntityService.getNotificationsBetween(from, to);
	}

	/**
	 * Deletes all the {@link NotificationEntity} whose date are between the given dates.
	 * <p>
	 * A {@link DateIntervalException} will be thrown if {@code from} date is after thant
	 * {@code to} date.
	 * @param from from {@link Date}.
	 * @param to to {@link Date}.
	 */
	@DeleteMapping("/notifications")
	@ResponseStatus(OK)
	public void deleteNotificationsBetween(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date to) {

		checkDateIntervals(from, to);

		notificationEntityService.deleteNotificationsBetween(from, to);
	}

	/**
	 * Checks if {@code from} date is earlier than {@code to} date. If not a
	 * {@link DateIntervalException} is throw.
	 * @param from from {@link Date}.
	 * @param to to {@link Date}.
	 */
	private void checkDateIntervals(final Date from, final Date to) {

		if (from.toInstant().isAfter(to.toInstant())) {

			throw new DateIntervalException(from, to);
		}
	}

}
