package com.paypal.infrastructure.job.listener;

import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobBean;
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
	public boolean vetoJobExecution(final Trigger trigger, final JobExecutionContext jecToBeExecuted) {
		try {
			final List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
			// @formatter:off
			final boolean isVetoed = currentlyExecutingJobs.stream()
					.anyMatch(isSameType(jecToBeExecuted));
			// @formatter:on
			if (isVetoed) {
				getLog().warn("Vetoing job with key [{}] because job of same type {} was already running",
						jecToBeExecuted.getJobDetail().getKey(), jecToBeExecuted.getJobInstance().getClass());
				return true;
			}

		}
		catch (final SchedulerException e) {
			getLog().error(e.getMessage(), e);
		}
		return false;
	}

	private static Predicate<JobExecutionContext> isSameType(final JobExecutionContext jecToBeExecuted) {
		return context -> getJobClass(context).equals(getJobClass(jecToBeExecuted));
	}

	private static Class<?> getJobClass(JobExecutionContext jec) {
		if (jec.getJobInstance() instanceof QuartzBatchJobBean) {
			return QuartzBatchJobBean.getBatchJobClass(jec);
		}
		else {
			return jec.getJobInstance().getClass();
		}
	}

}
