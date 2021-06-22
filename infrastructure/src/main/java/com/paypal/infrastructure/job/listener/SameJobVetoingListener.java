package com.paypal.infrastructure.job.listener;

import org.quartz.*;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.function.Predicate;

/**
 * Listener that avoids running un parallel jobs of same type
 */
@Service
public class SameJobVetoingListener extends TriggerListenerSupport {

	@Resource
	private Scheduler scheduler;

	@Override
	public String getName() {
		return "sameJobVetoingListener";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean vetoJobExecution(final Trigger trigger, final JobExecutionContext context) {
		try {
			final List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
			// @formatter:off
			final boolean isVetoed = currentlyExecutingJobs.stream()
					.map(JobExecutionContext::getJobInstance)
					.anyMatch(isSameType(context));
			// @formatter:on
			if (isVetoed) {
				getLog().warn("Vetoing job with key [{}] because job of same type {} was already running",
						context.getJobDetail().getKey(), context.getJobInstance().getClass());
				return true;
			}

		}
		catch (final SchedulerException e) {
			getLog().error(e.getMessage(), e);
		}
		return false;
	}

	private static Predicate<Job> isSameType(final JobExecutionContext jec) {
		return job -> jec.getJobInstance().getClass().equals(job.getClass());
	}

}
