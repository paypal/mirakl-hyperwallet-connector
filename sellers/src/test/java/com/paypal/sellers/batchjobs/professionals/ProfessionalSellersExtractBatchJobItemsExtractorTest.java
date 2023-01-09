package com.paypal.sellers.batchjobs.professionals;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfessionalSellersExtractBatchJobItemsExtractorTest {

	private static final Date DELTA = new Date();

	@InjectMocks
	private ProfessionalSellersExtractBatchJobItemsExtractor testObj;

	@Mock
	private MiraklSellersExtractService miraklSellersExtractServiceMock;

	@Mock
	private SellerModel sellerModelMock1, sellerModelMock2;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void getItems_ShouldReturnProfessionalSellersExtractJobItems() {
		when(miraklSellersExtractServiceMock.extractProfessionals(DELTA))
				.thenReturn(List.of(sellerModelMock1, sellerModelMock2));

		final Collection<ProfessionalSellerExtractJobItem> result = testObj.getItems(batchJobContextMock, DELTA);

		assertThat(result.stream().map(ProfessionalSellerExtractJobItem::getItem).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(sellerModelMock1, sellerModelMock2);
	}

}
