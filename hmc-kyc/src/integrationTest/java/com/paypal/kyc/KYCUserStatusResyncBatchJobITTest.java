package com.paypal.kyc;

import com.paypal.infrastructure.changestaging.service.StagedChangesPoller;
import com.paypal.infrastructure.changestaging.service.StagedChangesProcessor;
import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.infrastructure.support.date.TimeMachine;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import com.paypal.kyc.statussynchronization.batchjobs.KYCUserStatusResyncBatchJobItemProcessor;
import com.paypal.kyc.statussynchronization.jobs.KYCUserStatusResyncJob;
import com.paypal.testsupport.AbstractMockEnabledIntegrationTest;
import com.paypal.testsupport.TestJobExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.model.JsonPathBody;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;

@Transactional
class KYCUserStatusResyncBatchJobITTest extends AbstractMockEnabledIntegrationTest {

	public static final int NUM_MOCKED_HYPERWALLETUSERS = 5;

	@Value("${server.url}")
	private String mockServerUrl;

	@Autowired
	private KYCUserStatusResyncBatchJobItemProcessor kycUserStatusResyncBatchJobItemProcessor;

	@Autowired
	private BatchJobFailedItemService batchJobFailedItemService;

	@Autowired
	private TestJobExecutor testJobExecutor;

	@Autowired
	private StagedChangesPoller stagedChangesPoller;

	@Autowired
	private StagedChangesProcessor stagedChangesProcessor;

	@Value("${hmc.jobs.settings.resync-maxdays}")
	private Integer resyncMaxDays;

	@BeforeEach
	void setUpFixtures() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());
	}

	@AfterEach
	void tearDown() {
		TimeMachine.useSystemDefaultZoneClock();
	}

	@Test
	void shouldSynchronizeTheKYCStatus_WhenTheJobIsExecutedWithStagedChanges() {
		// given
		final Date to = DateUtil.convertToDate(TimeMachine.now(), UTC);
		final Date from = DateUtil.convertToDate(TimeMachine.now().minusDays(resyncMaxDays), UTC);
		mockServerExpectationsLoader.loadExpectationsFromFolder("mocks/testsets/kycuserstatusresyncbatchjob",
				"hyperwallet", Map.of("https://uat-api.paylution.com", mockServerUrl));
		mockServerExpectationsLoader.loadExpectationsFromFolder("mocks/testsets/kycuserstatusresyncbatchjob", "mirakl",
				Map.of());
		final int operationBatchSize = 3;
		setOperationBatchSize(operationBatchSize);

		// when
		testJobExecutor.executeJobAndWaitForCompletion(KYCUserStatusResyncJob.class, Map.of("delta", from));

		for (int i = 0; i < NUM_MOCKED_HYPERWALLETUSERS; i += operationBatchSize) {
			stagedChangesPoller.performStagedChange();
		}

		// then
		mockServerClient.verify(request().withPath("/api/shops").withMethod("PUT"), VerificationTimes.exactly(2));

		verifyMiraklKYCStatusRequest("2001", "APPROVED");
		verifyMiraklKYCStatusRequest("2002", "REFUSED");
		verifyMiraklKYCStatusRequest("2003", "PENDING_APPROVAL");
		verifyMiraklKYCStatusRequest("2005", "APPROVED");

		assertThat(batchJobFailedItemService.getFailedItems("KYCUserStatusInfo")).isEmpty();
	}

	private void verifyMiraklKYCStatusRequest(final String shopId, final String status) {
		mockServerClient.verify(
				request().withPath("/api/shops").withMethod("PUT")
						.withBody(JsonPathBody.jsonPath(
								"$.shops[?(@.shop_id==\"%s\" && @.kyc.status == \"%s\")]".formatted(shopId, status))),
				VerificationTimes.exactly(1));
	}

	private void setChangeStaging(final boolean useStaging) {
		ReflectionTestUtils.setField(kycUserStatusResyncBatchJobItemProcessor, "useStaging", useStaging);
	}

	private void setOperationBatchSize(final int operationBatchSize) {
		ReflectionTestUtils.setField(stagedChangesProcessor, "operationBatchSize", operationBatchSize);
	}

}
