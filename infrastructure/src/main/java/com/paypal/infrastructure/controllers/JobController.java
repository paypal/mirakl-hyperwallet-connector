package com.paypal.infrastructure.controllers;

import com.paypal.infrastructure.job.listener.JobExecutionInformationListener;
import com.paypal.infrastructure.model.entity.JobExecutionInformationEntity;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Rest controller for job entities
 */
@Slf4j
@RestController
@RequestMapping("/job")
public class JobController extends AbstractJobController {

	/**
	 * Returns a {@link List<JobExecutionInformationEntity>} of current jobs scheduled in
	 * the system
	 * @return the {@link List<JobExecutionInformationEntity>}
	 * @throws SchedulerException when quartz {@link org.quartz.Scheduler} fails
	 */
	@GetMapping
	public ResponseEntity<List<JobExecutionInformationEntity>> status() throws SchedulerException {
		//@formatter:off
        final List<JobExecutionInformationEntity> jobs = jobService.getJobs().stream()
				.map(jobDetail -> (JobExecutionInformationEntity)jobDetail.getJobDataMap().get(JobExecutionInformationListener.RUNNING_JOB_ENTITY))
				.filter(Objects::nonNull)
                .collect(Collectors.toList());
        //@formatter:on

		return ResponseEntity.accepted().body(jobs);
	}

}
