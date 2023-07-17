package com.paypal.jobsystem.quartzintegration.support;

import com.paypal.jobsystem.quartzintegration.repositories.entities.JobExecutionInformationEntity;
import jakarta.annotation.Resource;
import com.paypal.jobsystem.quartzintegration.repositories.JobExecutionInformationRepository;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.Map;
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
		return createJobDataMap(delta, Map.of());
	}

	/**
	 * Creates the delta time for mirakl data retrieval query
	 * @param delta the cut-out {@link Date} to retrieve shops
	 * @param params additional params to be passed to the job
	 * @return the {@link JobDataMap}
	 */
	public static JobDataMap createJobDataMap(final Date delta, @NonNull final Map<String, Object> params) {
		final JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(DELTA, delta);
		jobDataMap.putAll(params);
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
