package com.paypal.sellers.batchjobs.bstk;

import com.paypal.infrastructure.batchjob.AbstractDeltaBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobTrackingService;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.BusinessStakeholderExtractService;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The extractor class for retrieving all the business stakeholders within a delta time
 * from mirakl
 */
@Service
public class BusinessStakeholdersExtractBatchJobItemsExtractor
		extends AbstractDeltaBatchJobItemsExtractor<BatchJobContext, BusinessStakeholderExtractJobItem> {

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
	protected Collection<BusinessStakeholderExtractJobItem> getItems(BatchJobContext ctx, final Date delta) {
		final List<SellerModel> miraklProfessionalSellers = miraklSellersExtractService.extractProfessionals(delta);
		final List<BusinessStakeHolderModel> businessStakeHolderModels = businessStakeholderExtractService
				.extractBusinessStakeHolders(miraklProfessionalSellers);
		return businessStakeHolderModels.stream().map(BusinessStakeholderExtractJobItem::new)
				.collect(Collectors.toList());
	}

}
