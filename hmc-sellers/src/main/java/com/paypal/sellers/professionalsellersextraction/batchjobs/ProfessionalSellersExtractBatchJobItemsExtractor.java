package com.paypal.sellers.professionalsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import com.paypal.jobsystem.batchjobsupport.support.AbstractDynamicWindowDeltaBatchJobItemsExtractor;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.services.MiraklSellersExtractService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The extractor class for retrieving all the sellers within a delta time from mirakl
 */
@Component
public class ProfessionalSellersExtractBatchJobItemsExtractor
		extends AbstractDynamicWindowDeltaBatchJobItemsExtractor<BatchJobContext, ProfessionalSellerExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	public ProfessionalSellersExtractBatchJobItemsExtractor(
			final MiraklSellersExtractService miraklSellersExtractService,
			final BatchJobTrackingService batchJobTrackingService) {
		super(batchJobTrackingService);
		this.miraklSellersExtractService = miraklSellersExtractService;
	}

	/**
	 * Retrieves all the sellers modified since the {@code delta} time and returns them as
	 * a {@link ProfessionalSellerExtractJobItem}
	 * @param delta the cut-out {@link Date}
	 * @return a {@link Collection} of {@link ProfessionalSellerExtractJobItem}
	 */
	@Override
	protected Collection<ProfessionalSellerExtractJobItem> getItems(final BatchJobContext ctx, final Date delta) {
		final List<SellerModel> miraklProfessionalSellers = this.miraklSellersExtractService
				.extractProfessionals(delta);
		return miraklProfessionalSellers.stream().map(ProfessionalSellerExtractJobItem::new)
				.collect(Collectors.toList());
	}

}
