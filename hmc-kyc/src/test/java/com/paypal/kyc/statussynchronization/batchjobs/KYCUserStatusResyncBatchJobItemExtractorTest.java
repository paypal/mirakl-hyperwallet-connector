package com.paypal.kyc.statussynchronization.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.kyc.statussynchronization.model.KYCUserStatusInfoModel;
import com.paypal.kyc.statussynchronization.services.HyperwalletKycUserStatusExtractService;
import com.paypal.testsupport.TestDateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.paypal.testsupport.TestDateUtil.withinInterval;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KYCUserStatusResyncBatchJobItemExtractorTest {

	@InjectMocks
	private KYCUserStatusResyncBatchJobItemExtractor testObj;

	@Mock
	private HyperwalletKycUserStatusExtractService hyperwalletKycUserStatusExtractService;

	@Test
	void getItems_shouldDelegateToHyperwalletKycUserStatusExtractService() {
		// given
		final Date startDate = TestDateUtil.currentDateMinusDays(10);
		final List<KYCUserStatusInfoModel> kycUserStatuses = List.of(mock(KYCUserStatusInfoModel.class));
		when(hyperwalletKycUserStatusExtractService.extractKycUserStatuses(any(), any())).thenReturn(kycUserStatuses);

		// when
		final Collection<KYCUserStatusResyncBatchJobItem> result = testObj.getItems(mock(BatchJobContext.class),
				startDate);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.iterator().next().getItem()).isEqualTo(kycUserStatuses.get(0));
		verify(hyperwalletKycUserStatusExtractService, times(1)).extractKycUserStatuses(eq(startDate),
				argThat(x -> withinInterval(x, 10)));
	}

}
