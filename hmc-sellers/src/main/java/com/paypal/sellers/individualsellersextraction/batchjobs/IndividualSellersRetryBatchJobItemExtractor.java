package com.paypal.sellers.individualsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import com.paypal.jobsystem.batchjobfailures.services.cache.BatchJobFailedItemCacheService;
import com.paypal.jobsystem.batchjobfailures.support.AbstractCachingFailedItemsBatchJobItemsExtractor;
import com.paypal.sellers.sellerextractioncommons.services.MiraklSellersExtractService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IndividualSellersRetryBatchJobItemExtractor
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<BatchJobContext, IndividualSellersExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	public IndividualSellersRetryBatchJobItemExtractor(final MiraklSellersExtractService miraklSellersExtractService,
			final BatchJobFailedItemService batchJobFailedItemService,
			final BatchJobFailedItemCacheService batchJobFailedItemCacheService) {
		super(IndividualSellersExtractJobItem.class, IndividualSellersExtractJobItem.ITEM_TYPE,
				batchJobFailedItemService, batchJobFailedItemCacheService);
		this.miraklSellersExtractService = miraklSellersExtractService;
	}

	@Override
	protected Collection<IndividualSellersExtractJobItem> getItems(final List<String> ids) {
		return miraklSellersExtractService.extractSellers(ids).stream().map(IndividualSellersExtractJobItem::new)
				.collect(Collectors.toList());
	}

}
