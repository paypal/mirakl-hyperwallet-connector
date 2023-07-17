package com.paypal.notifications.failures.repositories;

import com.paypal.notifications.storage.repositories.entities.NotificationInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for {@link NotificationInfoEntity}
 */
@Repository
@Transactional
public interface FailedNotificationInformationRepository extends JpaRepository<NotificationInfoEntity, Long> {

	NotificationInfoEntity findByNotificationToken(String notificationToken);

	NotificationInfoEntity findByTypeAndTarget(String type, String target);

	Page<NotificationInfoEntity> findByTypeAndTarget(Pageable pageable, String type, String target);

}
