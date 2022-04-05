package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.model.entity.JobExecutionInformationEntity;
import com.paypal.infrastructure.repository.JobExecutionInformationRepository;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

/**
 * Base class for delta items extraction.
 *
 * @param <C> the job context type.
 * @param <T> the job item type.
 */
public abstract class AbstractDeltaBatchJobItemsExtractor<C extends BatchJobContext, T extends BatchJobItem<?>>
		implements BatchJobItemsExtractor<C, T> {

	private static final String DELTA = "delta";

	@Resource
	private JobExecutionInformationRepository jobExecutionInformationRepository;

	public void setJobExecutionInformationRepository(
			final JobExecutionInformationRepository jobExecutionInformationRepository) {
		this.jobExecutionInformationRepository = jobExecutionInformationRepository;
	}

	protected Date getDelta(final C context) {
		final Date delta = (Date) context.getJobExecutionContext().getJobDetail().getJobDataMap().get(DELTA);

		return delta == null ? Optional
				.ofNullable(jobExecutionInformationRepository.findTopByTypeAndEndTimeIsNotNullOrderByIdDesc(
						context.getJobExecutionContext().getJobDetail().getJobClass().getSimpleName()))
				.map(JobExecutionInformationEntity::getStartTime).orElse(null) : delta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<T> getItems(C ctx) {
		return getItems(getDelta(ctx));
	}

	protected abstract Collection<T> getItems(Date delta);

}
