package com.paypal.sellers.stakeholdersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import com.paypal.jobsystem.batchjobfailures.services.cache.BatchJobFailedItemCacheService;
import com.paypal.jobsystem.batchjobfailures.support.AbstractCachingFailedItemsBatchJobItemsExtractor;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.services.MiraklSellersExtractService;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import com.paypal.sellers.stakeholdersextraction.services.BusinessStakeholderExtractService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extract business stakeholders for retry from the failed items cache.
 */
@Component
public class BusinessStakeholdersRetryBatchJobItemsExtractor
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<BatchJobContext, BusinessStakeholderExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	private final BusinessStakeholderExtractService businessStakeholderExtractService;

	protected BusinessStakeholdersRetryBatchJobItemsExtractor(final BatchJobFailedItemService batchJobFailedItemService,
			final BatchJobFailedItemCacheService batchJobFailedItemCacheService,
			final MiraklSellersExtractService miraklSellersExtractService,
			final BusinessStakeholderExtractService businessStakeholderExtractService) {
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
