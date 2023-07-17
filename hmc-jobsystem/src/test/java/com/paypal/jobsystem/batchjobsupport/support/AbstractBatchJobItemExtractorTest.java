package com.paypal.jobsystem.batchjobsupport.support;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.testsupport.TestDateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractBatchJobItemExtractorTest {

	private static final String DELTA_KEY = "delta";

	private static final Date DELTA = new Date();

	private static final Date CALCULATED_DELTA = TestDateUtil.from("2023-05-01");

	@Spy
	private MyAbstractBatchJobItemExtractor testObj;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private BatchJobContext batchJobContextMock;

	@Test
	void getDelta_shouldReturnJobDetailDelta_whenDeltaExistsInJobParameters() {
		// given
		when(batchJobContextMock.getJobExecutionContext().getJobDetail().getJobDataMap().get(DELTA_KEY))
				.thenReturn(DELTA);

		// when
		final Date result = testObj.getDelta(batchJobContextMock);

		// then
		assertThat(result).isEqualTo(DELTA);
	}

	@Test
	void getDelta_shouldReturnCalculatedDate_whenDeltaDoesntExistsInJobParameters() {
		// given
		when(batchJobContextMock.getJobExecutionContext().getJobDetail().getJobDataMap().get(DELTA_KEY))
				.thenReturn(null);
		doReturn(CALCULATED_DELTA).when(testObj).getCalculatedDelta(batchJobContextMock);

		// when
		final Date result = testObj.getDelta(batchJobContextMock);

		// then
		assertThat(result).isEqualTo(CALCULATED_DELTA);
	}

	@Test
	void getItems_shouldInvokeChildClassGetItems() {
		// given
		final Collection<BatchJobItem<Object>> items = mock(Collection.class);
		doReturn(items).when(testObj).getItems(batchJobContextMock, DELTA);
		doReturn(DELTA).when(testObj).getDelta(batchJobContextMock);

		// when
		final Collection<BatchJobItem<Object>> result = testObj.getItems(batchJobContextMock);

		// then
		assertThat(result).isEqualTo(items);
		verify(testObj).getItems(batchJobContextMock, DELTA);
	}

	static class MyAbstractBatchJobItemExtractor
			extends AbstractBatchJobItemExtractor<BatchJobContext, BatchJobItem<Object>> {

		@Override
		protected Collection<BatchJobItem<Object>> getItems(final BatchJobContext ctx, final Date delta) {
			return null;
		}

		@Override
		protected Date getCalculatedDelta(final BatchJobContext context) {
			return null;
		}

	}

}
