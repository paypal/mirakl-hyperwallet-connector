package com.paypal.jobsystem.quartzadapter.job;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjob.model.BatchJobItemValidationResult;
import com.paypal.jobsystem.batchjob.model.BatchJobType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.aop.framework.ProxyFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class QuartzBatchJobBuilderTest {

	@Mock
	BatchJob batchJobMock;

	@Test
	void shouldBuildQuartzBatchJobStoringTheBatchJobClassName() {
		final JobDetail result = QuartzBatchJobBuilder.newJob(new TestBatchJob()).build();

		assertThat(result.getJobClass()).isEqualTo(QuartzBatchJobBean.class);
		assertThat(result.getJobDataMap()).containsEntry(QuartzBatchJobBean.KEY_BATCH_JOB_BEAN,
				TestBatchJob.class.getName());
	}

	/**
	 * Regression test for the reported failure: with a JDBC {@code JobStore}, Quartz
	 * serializes the job data map into the database, so everything in it has to be
	 * serializable. Storing the batch job instance dragged the whole Spring service graph
	 * in with it and blew up on startup.
	 */
	@Test
	void shouldBuildAJobDataMapThatCanBeSerialized() {
		final JobDetail result = QuartzBatchJobBuilder.newJob(new TestBatchJob()).build();

		assertThatCode(() -> serialize(result.getJobDataMap())).doesNotThrowAnyException();
	}

	/**
	 * Guards the test above: it only proves anything because the batch job itself is not
	 * serializable, which is exactly what made storing the instance fail.
	 */
	@Test
	void storingTheBatchJobInstanceWouldNotBeSerializable() {
		final JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(QuartzBatchJobBean.KEY_BATCH_JOB_BEAN, new TestBatchJob());

		assertThatThrownBy(() -> serialize(jobDataMap)).isInstanceOf(NotSerializableException.class);
	}

	@Test
	void shouldStoreTheTargetClassNameWhenTheBatchJobIsProxied() {
		final ProxyFactory proxyFactory = new ProxyFactory(new TestBatchJob());
		proxyFactory.setProxyTargetClass(true);
		final BatchJob<?, ?> proxiedBatchJob = (BatchJob<?, ?>) proxyFactory.getProxy();

		final JobDetail result = QuartzBatchJobBuilder.newJob(proxiedBatchJob).build();

		// The generated proxy class name could not be loaded again, so the name recorded
		// has to be the one the bean can actually be looked up by.
		assertThat(proxiedBatchJob.getClass().getName()).isNotEqualTo(TestBatchJob.class.getName());
		assertThat(result.getJobDataMap()).containsEntry(QuartzBatchJobBean.KEY_BATCH_JOB_BEAN,
				TestBatchJob.class.getName());
	}

	private static void serialize(final Object value) throws IOException {
		try (ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream())) {
			out.writeObject(value);
		}
	}

	/**
	 * A batch job shaped like the real ones: a Spring-managed, non-serializable object
	 * holding collaborators.
	 */
	static class TestBatchJob implements BatchJob<BatchJobContext, BatchJobItem<?>> {

		@SuppressWarnings("unused")
		private final Object collaborator = new Object();

		@Override
		public Collection<BatchJobItem<?>> getItems(final BatchJobContext ctx) {
			return List.of();
		}

		@Override
		public void prepareForItemProcessing(final BatchJobContext ctx,
				final Collection<BatchJobItem<?>> itemsToBeProcessed) {
			// no-op
		}

		@Override
		public BatchJobItem<?> enrichItem(final BatchJobContext ctx, final BatchJobItem<?> jobItem) {
			return jobItem;
		}

		@Override
		public BatchJobItemValidationResult validateItem(final BatchJobContext ctx, final BatchJobItem<?> jobItem) {
			return null;
		}

		@Override
		public void processItem(final BatchJobContext ctx, final BatchJobItem<?> jobItem) {
			// no-op
		}

		@Override
		public BatchJobType getType() {
			return null;
		}

	}

}
