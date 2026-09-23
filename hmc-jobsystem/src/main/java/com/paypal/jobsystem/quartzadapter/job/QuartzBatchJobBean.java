package com.paypal.jobsystem.quartzadapter.job;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.ClassUtils;

/**
 * Quartz job that delegates to the {@link BatchJob} bean identified by the
 * {@value #KEY_BATCH_JOB_BEAN} entry of its job data map.
 * <p>
 * The job data map holds the fully qualified class name of the batch job, not the bean
 * itself, so that it stays serializable and the job can be persisted by a JDBC
 * {@code JobStore}. The bean is resolved from the application context on execution.
 */
public class QuartzBatchJobBean extends QuartzJobBean {

	public static final String KEY_BATCH_JOB_BEAN = "batchJob";

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory;

	private String batchJob;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeInternal(final JobExecutionContext context) throws JobExecutionException {
		quartzBatchJobAdapterFactory.getQuartzJob(resolveBatchJob()).execute(context);
	}

	public void setBatchJob(final String batchJob) {
		this.batchJob = batchJob;
	}

	/**
	 * Returns the fully qualified class name of the batch job of the given execution.
	 * <p>
	 * Returns the name rather than the {@link Class} so that callers which only need to
	 * identify or display the job do not have to load a class that may no longer exist: a
	 * persisted job outlives the code that defined it.
	 * @param context the job execution context.
	 * @return the fully qualified class name of the batch job.
	 */
	public static String getBatchJobClassName(final JobExecutionContext context) {
		return (String) context.getJobDetail().getJobDataMap().get(KEY_BATCH_JOB_BEAN);
	}

	/**
	 * Resolves the batch job bean from the application context.
	 * <p>
	 * Resolution happens here rather than in {@link #setBatchJob(String)} because Spring
	 * applies the job data map to the job instance before autowiring it, so
	 * {@link #beanFactory} is not yet available when the setter runs.
	 * @return the batch job bean.
	 * @throws JobExecutionException if the class cannot be loaded or no such bean exists.
	 */
	@SuppressWarnings("unchecked")
	private BatchJob<? extends BatchJobContext, ? extends BatchJobItem<?>> resolveBatchJob()
			throws JobExecutionException {
		try {
			final Class<?> batchJobClass = ClassUtils.forName(batchJob, getClass().getClassLoader());
			return (BatchJob<? extends BatchJobContext, ? extends BatchJobItem<?>>) beanFactory.getBean(batchJobClass);
		}
		catch (final ClassNotFoundException | LinkageError | BeansException e) {
			throw new JobExecutionException("Unable to resolve batch job bean [" + batchJob
					+ "]. The job is persisted but its class is no " + "longer available in the application context.",
					e);
		}
	}

}
