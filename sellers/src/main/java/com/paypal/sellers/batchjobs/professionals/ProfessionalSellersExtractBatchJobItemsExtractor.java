package com.paypal.sellers.batchjobs.professionals;

import com.paypal.infrastructure.batchjob.AbstractDeltaBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobTrackingService;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The extractor class for retrieving all the sellers within a delta time from mirakl
 */
@Service
public class ProfessionalSellersExtractBatchJobItemsExtractor
		extends AbstractDeltaBatchJobItemsExtractor<BatchJobContext, ProfessionalSellerExtractJobItem> {

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
	protected Collection<ProfessionalSellerExtractJobItem> getItems(BatchJobContext ctx, final Date delta) {
		final List<SellerModel> miraklProfessionalSellers = this.miraklSellersExtractService
				.extractProfessionals(delta);
		return miraklProfessionalSellers.stream().map(ProfessionalSellerExtractJobItem::new)
				.collect(Collectors.toList());
	}

}
