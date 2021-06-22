package com.paypal.infrastructure.job;

import com.paypal.infrastructure.model.entity.JobExecutionInformationEntity;
import com.paypal.infrastructure.repository.JobExecutionInformationRepository;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
 * Abstract class that holds {@link JobExecutionInformationRepository} job
 */
public abstract class AbstractDeltaInfoJob implements Job {

	private static final String DELTA = "delta";

	@Resource
	private JobExecutionInformationRepository jobExecutionInformationRepository;

	/**
	 * Creates the delta time for mirakl data retrieval query
	 * @param delta the cut-out {@link Date} to retrieve shops
	 * @return the {@link JobDataMap}
	 */
	public static JobDataMap createJobDataMap(final Date delta) {
		final JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(DELTA, delta);
		return jobDataMap;
	}

	/**
	 * Retrieves the delta from time from {@link JobDataMap} linked to
	 * {@link JobExecutionContext}
	 * @param context the {@link JobExecutionContext}
	 * @return the {@link Date}
	 */
	public Date getDelta(final JobExecutionContext context) {
		final Date delta = (Date) context.getJobDetail().getJobDataMap().get(DELTA);

		return delta == null ? Optional
				.ofNullable(jobExecutionInformationRepository.findTopByTypeAndEndTimeIsNotNullOrderByIdDesc(
						context.getJobDetail().getJobClass().getSimpleName()))
				.map(JobExecutionInformationEntity::getStartTime).orElse(null) : delta;
	}

}
