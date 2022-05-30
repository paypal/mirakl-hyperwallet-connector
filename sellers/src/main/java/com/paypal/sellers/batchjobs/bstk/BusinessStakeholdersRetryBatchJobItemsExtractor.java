package com.paypal.sellers.batchjobs.bstk;

import com.paypal.infrastructure.batchjob.AbstractOnlyCachedFailedItemsBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheService;
import org.springframework.stereotype.Service;

/**
 * Extract business stakeholders for retry from the failed items cache.
 */
@Service
public class BusinessStakeholdersRetryBatchJobItemsExtractor extends
		AbstractOnlyCachedFailedItemsBatchJobItemsExtractor<BatchJobContext, BusinessStakeholderExtractJobItem> {

	protected BusinessStakeholdersRetryBatchJobItemsExtractor(BatchJobFailedItemService batchJobFailedItemService,
			BatchJobFailedItemCacheService batchJobFailedItemCacheService) {
		super(BusinessStakeholderExtractJobItem.class, BusinessStakeholderExtractJobItem.ITEM_TYPE,
				batchJobFailedItemService, batchJobFailedItemCacheService);
	}

}
