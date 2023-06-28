package com.paypal.sellers.stakeholdersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import com.paypal.jobsystem.batchjobsupport.support.AbstractDynamicWindowDeltaBatchJobItemsExtractor;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.services.MiraklSellersExtractService;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import com.paypal.sellers.stakeholdersextraction.services.BusinessStakeholderExtractService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The extractor class for retrieving all the business stakeholders within a delta time
 * from mirakl
 */
@Component
public class BusinessStakeholdersExtractBatchJobItemsExtractor
		extends AbstractDynamicWindowDeltaBatchJobItemsExtractor<BatchJobContext, BusinessStakeholderExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	private final BusinessStakeholderExtractService businessStakeholderExtractService;

	public BusinessStakeholdersExtractBatchJobItemsExtractor(
			final MiraklSellersExtractService miraklSellersExtractService,
			final BusinessStakeholderExtractService businessStakeholderExtractService,
			final BatchJobTrackingService batchJobTrackingService) {
		super(batchJobTrackingService);
		this.miraklSellersExtractService = miraklSellersExtractService;
		this.businessStakeholderExtractService = businessStakeholderExtractService;
	}

	/**
	 * Retrieves all the stakeholders modified since the {@code delta} time and returns
	 * them as a {@link BusinessStakeholderExtractJobItem}
	 * @param delta the cut-out {@link Date}
	 * @return a {@link Collection} of {@link BusinessStakeholderExtractJobItem}
	 */
	@Override
	protected Collection<BusinessStakeholderExtractJobItem> getItems(final BatchJobContext ctx, final Date delta) {
		final List<SellerModel> miraklProfessionalSellers = miraklSellersExtractService.extractProfessionals(delta);
		final List<BusinessStakeHolderModel> businessStakeHolderModels = businessStakeholderExtractService
				.extractBusinessStakeHolders(miraklProfessionalSellers);
		return businessStakeHolderModels.stream().map(BusinessStakeholderExtractJobItem::new)
				.collect(Collectors.toList());
	}

}
