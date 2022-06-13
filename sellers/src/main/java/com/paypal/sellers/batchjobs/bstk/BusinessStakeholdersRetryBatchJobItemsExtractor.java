package com.paypal.sellers.batchjobs.bstk;

import com.paypal.infrastructure.batchjob.AbstractCachingFailedItemsBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheService;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.BusinessStakeholderExtractService;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extract business stakeholders for retry from the failed items cache.
 */
@Service
public class BusinessStakeholdersRetryBatchJobItemsExtractor
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<BatchJobContext, BusinessStakeholderExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	private final BusinessStakeholderExtractService businessStakeholderExtractService;

	protected BusinessStakeholdersRetryBatchJobItemsExtractor(BatchJobFailedItemService batchJobFailedItemService,
			BatchJobFailedItemCacheService batchJobFailedItemCacheService,
			MiraklSellersExtractService miraklSellersExtractService,
			BusinessStakeholderExtractService businessStakeholderExtractService) {
		super(BusinessStakeholderExtractJobItem.class, BusinessStakeholderExtractJobItem.ITEM_TYPE,
				batchJobFailedItemService, batchJobFailedItemCacheService);
		this.miraklSellersExtractService = miraklSellersExtractService;
		this.businessStakeholderExtractService = businessStakeholderExtractService;
	}

	@Override
	protected Collection<BusinessStakeholderExtractJobItem> getItems(final List<String> ids) {

		final List<SellerModel> miraklProfessionalSellers = miraklSellersExtractService.extractProfessionals(ids);
		final List<BusinessStakeHolderModel> businessStakeHolderModels = businessStakeholderExtractService
				.extractBusinessStakeHolders(miraklProfessionalSellers);

		return businessStakeHolderModels.stream().map(BusinessStakeholderExtractJobItem::new)
				.collect(Collectors.toList());
	}

}
