package com.paypal.sellers.stakeholdersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.sellers.stakeholdersextraction.batchjobs.BusinessStakeholderExtractJobItem;
import com.paypal.sellers.stakeholdersextraction.batchjobs.BusinessStakeholdersExtractBatchJobItemsExtractor;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.stakeholdersextraction.services.BusinessStakeholderExtractService;
import com.paypal.sellers.sellerextractioncommons.services.MiraklSellersExtractService;
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
class BusinessStakeholdersExtractBatchJobItemsExtractorTest {

	private static final Date DELTA = new Date();

	@InjectMocks
	private BusinessStakeholdersExtractBatchJobItemsExtractor testObj;

	@Mock
	private MiraklSellersExtractService miraklSellersExtractServiceMock;

	@Mock
	private BusinessStakeholderExtractService businessStakeholderExtractServiceMock;

	@Mock
	private SellerModel sellerModelMock1, sellerModelMock2;

	@Mock
	private BusinessStakeHolderModel businessStakeHolderModelMock1, businessStakeHolderModelMock2;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void getItems_ShouldReturnBusinessStakeholderExtractJobItems() {
		when(miraklSellersExtractServiceMock.extractProfessionals(DELTA))
				.thenReturn(List.of(sellerModelMock1, sellerModelMock2));
		when(businessStakeholderExtractServiceMock
				.extractBusinessStakeHolders(List.of(sellerModelMock1, sellerModelMock2)))
						.thenReturn(List.of(businessStakeHolderModelMock1, businessStakeHolderModelMock2));

		final Collection<BusinessStakeholderExtractJobItem> result = testObj.getItems(batchJobContextMock, DELTA);

		assertThat(result.stream().map(BusinessStakeholderExtractJobItem::getItem).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(businessStakeHolderModelMock1, businessStakeHolderModelMock2);
	}

}
