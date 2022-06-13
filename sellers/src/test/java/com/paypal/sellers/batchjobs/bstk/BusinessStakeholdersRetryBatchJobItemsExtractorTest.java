package com.paypal.sellers.batchjobs.bstk;

import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.BusinessStakeholderExtractService;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessStakeholdersRetryBatchJobItemsExtractorTest {

	public static final String PROFESSIONAL_SELLER_1 = "1";

	public static final String PROFESSIONAL_SELLER_2 = "2";

	@InjectMocks
	private BusinessStakeholdersRetryBatchJobItemsExtractor testObj;

	@Mock
	private MiraklSellersExtractService miraklSellersExtractServiceMock;

	@Mock
	private BusinessStakeholderExtractService businessStakeholderExtractServiceMock;

	@Mock
	private SellerModel sellerModelMock1, sellerModelMock2;

	@Mock
	private BusinessStakeHolderModel businessStakeHolderModelMock1, businessStakeHolderModelMock2;

	@Test
	void getItemType_shouldReturnBusinessStakeholderType() {
		String result = testObj.getItemType();

		assertThat(result).isEqualTo(BusinessStakeholderExtractJobItem.ITEM_TYPE);
	}

	@Test
	void getItems_ShouldReturnBusinessStakeholderExtractJobItems() {

		when(miraklSellersExtractServiceMock
				.extractProfessionals(List.of(PROFESSIONAL_SELLER_1, PROFESSIONAL_SELLER_2)))
						.thenReturn(List.of(sellerModelMock1, sellerModelMock2));

		when(businessStakeholderExtractServiceMock
				.extractBusinessStakeHolders(List.of(sellerModelMock1, sellerModelMock2)))
						.thenReturn(List.of(businessStakeHolderModelMock1, businessStakeHolderModelMock2));

		final Collection<BusinessStakeholderExtractJobItem> result = testObj
				.getItems(List.of(PROFESSIONAL_SELLER_1, PROFESSIONAL_SELLER_2));

		assertThat(result.stream().map(BusinessStakeholderExtractJobItem::getItem).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(businessStakeHolderModelMock1, businessStakeHolderModelMock2);
	}

}
