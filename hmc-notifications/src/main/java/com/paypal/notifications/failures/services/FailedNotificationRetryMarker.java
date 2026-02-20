package com.paypal.notifications.failures.services;

import com.paypal.notifications.failures.repositories.FailedNotificationInformationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class FailedNotificationRetryMarker {

    private final FailedNotificationInformationRepository repo;

    public FailedNotificationRetryMarker(FailedNotificationInformationRepository repo) {
        this.repo = repo;
    }

    public void incrementFailures(String token) {
        repo.incrementRetryCounter(token);
    }

    public void markSucceededAndRemove(String token) {
        repo.deleteByNotificationToken(token);
    }

    public void expireAndRemove(String token) {
        repo.deleteByNotificationToken(token);
    }
}
