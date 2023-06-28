package com.paypal.jobsystem.batchjobsupport.support;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.testsupport.TestDateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AbstractFixedWindowDeltaBatchJobItemsExtractorTest {

	public static final long RESYNC_MAX_DAYS = 90;

	@InjectMocks
	private MyFixedWindowDeltaBatchJobItemsExtractor testObj;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void getCalculatedDelta_shouldReturnTodayMinusResyncMaxDays_whenThereIsNoError() {
		testObj.resyncMaxDays = RESYNC_MAX_DAYS;
		final Date result = testObj.getCalculatedDelta(batchJobContextMock);

		final Date minus90MinusSeconds = TestDateUtil.currentDateMinusDaysPlusSeconds(RESYNC_MAX_DAYS, -100);
		final Date minus90 = TestDateUtil.currentDateMinusDaysPlusSeconds(RESYNC_MAX_DAYS, 10);
		assertThat(result).isBetween(minus90MinusSeconds, minus90);
	}

	private static class MyFixedWindowDeltaBatchJobItemsExtractor
			extends AbstractFixedWindowDeltaBatchJobItemsExtractor<BatchJobContext, BatchJobItem<Object>> {

		@Override
		protected Collection<BatchJobItem<Object>> getItems(final BatchJobContext ctx, final Date delta) {
			return null;
		}

	}

}
