package com.paypal.jobsystem.batchjobsupport.support;

import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.infrastructure.support.date.TimeMachine;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobTrackInfoEntity;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.ZoneOffset.UTC;

/**
 * Base class for delta items extraction.
 *
 * @param <C> the job context type.
 * @param <T> the job item type.
 */
public abstract class AbstractDynamicWindowDeltaBatchJobItemsExtractor<C extends BatchJobContext, T extends BatchJobItem<?>>
		extends AbstractBatchJobItemExtractor<C, T> {

	private final BatchJobTrackingService batchJobTrackingService;

	@Value("${hmc.jobs.settings.extraction-maxdays}")
	protected Integer extractionMaxDays;

	protected AbstractDynamicWindowDeltaBatchJobItemsExtractor(final BatchJobTrackingService batchJobTrackingService) {
		this.batchJobTrackingService = batchJobTrackingService;
	}

	@Override
	protected Date getCalculatedDelta(final BatchJobContext context) {
		final LocalDateTime searchJobsFrom = TimeMachine.now().minusDays(extractionMaxDays);
		final BatchJobTrackInfoEntity batchJobTrackInfoEntity = batchJobTrackingService
				.findLastJobExecutionWithNonEmptyExtraction(context.getJobName(), searchJobsFrom).orElse(null);

		return batchJobTrackInfoEntity != null ? DateUtil.convertToDate(batchJobTrackInfoEntity.getStartTime(), UTC)
				: DateUtil.convertToDate(searchJobsFrom, UTC);
	}

}
