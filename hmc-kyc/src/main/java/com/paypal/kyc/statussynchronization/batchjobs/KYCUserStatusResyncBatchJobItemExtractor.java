package com.paypal.kyc.statussynchronization.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.support.AbstractFixedWindowDeltaBatchJobItemsExtractor;
import com.paypal.kyc.statussynchronization.services.HyperwalletKycUserStatusExtractService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class KYCUserStatusResyncBatchJobItemExtractor
		extends AbstractFixedWindowDeltaBatchJobItemsExtractor<BatchJobContext, KYCUserStatusResyncBatchJobItem> {

	private final HyperwalletKycUserStatusExtractService hyperwalletKycUserStatusExtractService;

	public KYCUserStatusResyncBatchJobItemExtractor(
			final HyperwalletKycUserStatusExtractService hyperwalletKycUserStatusExtractService) {
		this.hyperwalletKycUserStatusExtractService = hyperwalletKycUserStatusExtractService;
	}

	/**
	 * Retrieves all Hyperwallet users since the {@code delta} time and returns them as a
	 * {@link Collection} of {@link KYCUserStatusResyncBatchJobItem}
	 * @param delta the initial date for the creation date of the users {@link Date}
	 * @return a {@link Collection} of {@link KYCUserStatusResyncBatchJobItem}
	 */
	@Override
	protected Collection<KYCUserStatusResyncBatchJobItem> getItems(final BatchJobContext ctx, final Date delta) {
		//@formatter:off
		return hyperwalletKycUserStatusExtractService.extractKycUserStatuses(delta, new Date())
				.stream()
				.map(KYCUserStatusResyncBatchJobItem::new)
				.collect(Collectors.toList());
		//@formatter:on
	}

}
