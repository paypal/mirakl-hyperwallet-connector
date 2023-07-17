package com.paypal.sellers.professionalsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import com.paypal.jobsystem.batchjobfailures.services.cache.BatchJobFailedItemCacheService;
import com.paypal.jobsystem.batchjobfailures.support.AbstractCachingFailedItemsBatchJobItemsExtractor;
import com.paypal.sellers.sellerextractioncommons.services.MiraklSellersExtractService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extract professional sellers for retry from the failed items cache.
 */
@Component
public class ProfessionalSellersRetryBatchJobItemsExtractor
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<BatchJobContext, ProfessionalSellerExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	protected ProfessionalSellersRetryBatchJobItemsExtractor(final BatchJobFailedItemService batchJobFailedItemService,
			final BatchJobFailedItemCacheService batchJobFailedItemCacheService,
			final MiraklSellersExtractService miraklSellersExtractService) {
		super(ProfessionalSellerExtractJobItem.class, ProfessionalSellerExtractJobItem.ITEM_TYPE,
				batchJobFailedItemService, batchJobFailedItemCacheService);
		this.miraklSellersExtractService = miraklSellersExtractService;
	}

	@Override
	protected Collection<ProfessionalSellerExtractJobItem> getItems(final List<String> ids) {
		return miraklSellersExtractService.extractProfessionals(ids).stream().map(ProfessionalSellerExtractJobItem::new)
				.collect(Collectors.toList());
	}

}
