package com.paypal.sellers.individualsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.sellers.individualsellersextraction.batchjobs.IndividualSellersExtractBatchJobItemsExtractor;
import com.paypal.sellers.individualsellersextraction.batchjobs.IndividualSellersExtractJobItem;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.services.MiraklSellersExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndividualSellersExtractBatchJobItemsExtractorTest {

	private static final Date DELTA = new Date();

	@InjectMocks
	private IndividualSellersExtractBatchJobItemsExtractor testObj;

	@Mock
	private MiraklSellersExtractService miraklSellersExtractServiceMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void getItems_ShouldRetrieveAllSellersAndMapThemIntoIndividualSellersExtractJobItems() {

		final SellerModel sellerModel1 = SellerModel.builder().build();
		final SellerModel sellerModel2 = SellerModel.builder().build();

		when(miraklSellersExtractServiceMock.extractIndividuals(DELTA)).thenReturn(List.of(sellerModel1, sellerModel2));

		final Collection<IndividualSellersExtractJobItem> individualSellersExtractJobItems = testObj
				.getItems(batchJobContextMock, DELTA);

		assertThat(individualSellersExtractJobItems.stream().map(IndividualSellersExtractJobItem::getItem))
				.containsExactly(sellerModel1, sellerModel2);
	}

}
