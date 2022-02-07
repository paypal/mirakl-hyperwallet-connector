package com.paypal.infrastructure.repository;

import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for {@link NotificationInfoEntity}
 */
@Repository
@Transactional
public interface FailedNotificationInformationRepository extends CrudRepository<NotificationInfoEntity, Long> {

	NotificationInfoEntity findByNotificationToken(String notificationToken);

	NotificationInfoEntity findByTypeAndTarget(String type, String target);

}
