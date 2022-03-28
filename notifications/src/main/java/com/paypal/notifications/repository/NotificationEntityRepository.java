package com.paypal.notifications.repository;

import com.paypal.notifications.model.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Repository for retrieving {@link NotificationEntity} from repository.
 */
@Repository
@Transactional
public interface NotificationEntityRepository extends JpaRepository<NotificationEntity, Long> {

	/**
	 * Retrieves all the {@link NotificationEntity} whose reception date are between the
	 * given dates.
	 * @param from from {@link Date}.
	 * @param to to {@link Date}.
	 * @return a {@link List} of {@link NotificationEntity} whose date are between the
	 * given dates.
	 */
	@Query("Select n from NotificationEntity n where n.receptionDate >= :from and n.receptionDate <= :to")
	List<NotificationEntity> findNotificationsBetween(@Param("from") Date from, @Param("to") Date to);

	/**
	 * Deletes all the {@link NotificationEntity} whose reception date are between the
	 * given dates.
	 * @param from from {@link Date}.
	 * @param to to {@link Date}.
	 */
	@Modifying
	@Query("Delete from NotificationEntity n where n.receptionDate >= :from and n.receptionDate <= :to")
	void deleteNotificationsBetween(@Param("from") Date from, @Param("to") Date to);

	/**
	 * Retrieves all the {@link NotificationEntity} whose webHookToken is equals to the
	 * given one.
	 * @param webHookToken the webHookToken to search for.
	 * @return a {@link List} of {@link NotificationEntity} whose webHookToken is equals
	 * to the given one.
	 */
	@Query("Select n from NotificationEntity n where n.webHookToken = :webHookToken")
	List<NotificationEntity> findNotificationsByWebHookToken(@Param("webHookToken") final String webHookToken);

	/**
	 * Retrieves all the {@link NotificationEntity} whose objectToken is equals to the
	 * given one and creationDate is later than the given one.
	 * @param objectToken the objectToken to search for.
	 * @param creationDate the creationDate to search for.
	 * @return a {@link List} of {@link NotificationEntity} whose objectToken is equals to
	 * the given one and creationDate is later than the given one.
	 */
	@Query("Select n from NotificationEntity n where n.objectToken = :objectToken and  n.creationDate > :creationDate")
	List<NotificationEntity> findNotificationsByObjectTokenAndAndCreationDateAfter(
			@Param("objectToken") final String objectToken, @Param("creationDate") final Date creationDate);

}
