package com.paypal.notifications.incoming.jobs;

import com.paypal.jobsystem.quartzintegration.support.AbstractDeltaInfoJob;
import com.paypal.notifications.incoming.services.NotificationProcessingQueueService;
import com.paypal.notifications.incoming.services.NotificationProcessingService;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quartz job that reads a batch of pending/retrying notifications from the processing
 * queue and delegates each one to {@link NotificationProcessingService} for execution.
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class NotificationProcessJob extends AbstractDeltaInfoJob {

	@Value("${hmc.webhooks.processing-queue.batch-size:100}")
	private int batchSize;

	private final NotificationProcessingQueueService notificationProcessingQueueService;

	private final NotificationProcessingService notificationProcessingService;

	@Override
	public void execute(final JobExecutionContext context) {
		log.info("NotificationProcessJob started — fetching up to [{}] notifications", batchSize);
		final List<NotificationEntity> batch = notificationProcessingQueueService.fetchNextBatch(batchSize);
		log.info("Processing [{}] notifications", batch.size());

		final Map<String, NotificationProcessingService.NotificationProcessingStatus> statusUpdates = new HashMap<>();

		for (final NotificationEntity entity : batch) {
			final NotificationProcessingService.NotificationProcessingStatus result = notificationProcessingService
				.processNotification(entity);
			statusUpdates.put(entity.getWebHookToken(), result);
		}

		if (!statusUpdates.isEmpty()) {
			notificationProcessingQueueService.updateStatus(statusUpdates);
		}
	}

}
