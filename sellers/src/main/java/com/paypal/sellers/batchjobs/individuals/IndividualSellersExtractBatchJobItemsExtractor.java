package com.paypal.sellers.batchjobs.individuals;

import com.paypal.infrastructure.batchjob.AbstractDeltaBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The extractor class for retrieving all the sellers within a delta time from mirakl
 */
@Component
public class IndividualSellersExtractBatchJobItemsExtractor
		extends AbstractDeltaBatchJobItemsExtractor<BatchJobContext, IndividualSellerExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	public IndividualSellersExtractBatchJobItemsExtractor(
			final MiraklSellersExtractService miraklSellersExtractService) {
		this.miraklSellersExtractService = miraklSellersExtractService;
	}

	/**
	 * Retrieves all the sellers modified since the {@code delta} time and returns them as
	 * a {@link IndividualSellerExtractJobItem}
	 * @param delta the cut-out {@link Date}
	 * @return a {@link Collection} of {@link IndividualSellerExtractJobItem}
	 */
	@Override
	protected Collection<IndividualSellerExtractJobItem> getItems(Date delta) {
		List<SellerModel> miraklIndividualSellers = miraklSellersExtractService.extractIndividuals(delta);
		return miraklIndividualSellers.stream().map(IndividualSellerExtractJobItem::new).collect(Collectors.toList());
	}

}
