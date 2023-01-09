package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.batchjob.entities.BatchJobTrackInfoEntity;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

import static java.time.ZoneOffset.UTC;

/**
 * Base class for delta items extraction.
 *
 * @param <C> the job context type.
 * @param <T> the job item type.
 */
public abstract class AbstractDeltaBatchJobItemsExtractor<C extends BatchJobContext, T extends BatchJobItem<?>>
		implements BatchJobItemsExtractor<C, T> {

	private static final String DELTA = "delta";

	private final BatchJobTrackingService batchJobTrackingService;

	@Value("${jobs.extraction.maxdays}")
	protected Integer extractionMaxDays;

	protected AbstractDeltaBatchJobItemsExtractor(BatchJobTrackingService batchJobTrackingService) {
		this.batchJobTrackingService = batchJobTrackingService;
	}

	protected Date getDelta(final C context) {
		final Date deltaParameter = findDeltaInJobParameters(context);

		return deltaParameter != null ? deltaParameter : getLastSuccessfulExtractionDate(context);
	}

	private Date findDeltaInJobParameters(final C context) {
		return (Date) context.getJobExecutionContext().getJobDetail().getJobDataMap().get(DELTA);
	}

	private Date getLastSuccessfulExtractionDate(final C context) {
		LocalDateTime searchJobsFrom = TimeMachine.now().minusDays(extractionMaxDays);
		BatchJobTrackInfoEntity batchJobTrackInfoEntity = batchJobTrackingService
				.findLastJobExecutionWithNonEmptyExtraction(context.getJobName(), searchJobsFrom).orElse(null);

		return batchJobTrackInfoEntity != null ? DateUtil.convertToDate(batchJobTrackInfoEntity.getStartTime(), UTC)
				: DateUtil.convertToDate(searchJobsFrom, UTC);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<T> getItems(C ctx) {
		return getItems(ctx, getDelta(ctx));
	}

	protected abstract Collection<T> getItems(C ctx, Date delta);

}
