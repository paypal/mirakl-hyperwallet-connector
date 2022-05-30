package com.paypal.sellers.batchjobs.professionals;

import com.paypal.infrastructure.batchjob.AbstractCachingFailedItemsBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheService;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extract professional sellers for retry from the failed items cache.
 */
@Service
public class ProfessionalSellersRetryBatchJobItemsExtractor
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<BatchJobContext, ProfessionalSellerExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	protected ProfessionalSellersRetryBatchJobItemsExtractor(BatchJobFailedItemService batchJobFailedItemService,
			BatchJobFailedItemCacheService batchJobFailedItemCacheService,
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
