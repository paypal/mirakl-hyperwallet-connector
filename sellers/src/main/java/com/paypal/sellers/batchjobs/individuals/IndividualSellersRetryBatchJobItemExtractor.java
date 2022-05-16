package com.paypal.sellers.batchjobs.individuals;

import com.paypal.infrastructure.batchjob.AbstractFailedItemsBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndividualSellersRetryBatchJobItemExtractor
		extends AbstractFailedItemsBatchJobItemsExtractor<BatchJobContext, IndividualSellersExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	public IndividualSellersRetryBatchJobItemExtractor(final MiraklSellersExtractService miraklSellersExtractService,
			final BatchJobFailedItemService batchJobFailedItemService) {
		super(IndividualSellersExtractJobItem.ITEM_TYPE, batchJobFailedItemService);
		this.miraklSellersExtractService = miraklSellersExtractService;
	}

	/**
	 * Retrieves a {@link Collection} of {@link IndividualSellersExtractJobItem} by the
	 * given ids.
	 * @param ids the {@link IndividualSellersExtractJobItem} ids what to search for.
	 * @return a {@link Collection} of {@link IndividualSellersExtractJobItem}
	 */
	@Override
	protected Collection<IndividualSellersExtractJobItem> getItems(List<String> ids) {
		return miraklSellersExtractService.extractSellers(ids).stream().map(IndividualSellersExtractJobItem::new)
				.collect(Collectors.toList());
	}

}
