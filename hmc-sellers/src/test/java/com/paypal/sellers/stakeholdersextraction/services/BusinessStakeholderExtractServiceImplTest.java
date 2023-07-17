package com.paypal.sellers.stakeholdersextraction.services;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.sellers.stakeholdersextraction.services.BusinessStakeholderExtractServiceImpl;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderConstants;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BusinessStakeholderExtractServiceImplTest {

	private static final String TOKEN_1 = "token1";

	private static final String TOKEN_2 = "token2";

	private static final String CLIENT_ID_1 = "clientId1";

	private static final String CLIENT_ID_2 = "clientId2";

	@InjectMocks
	private BusinessStakeholderExtractServiceImpl testObj;

	@Test
	void extractBusinessStakeHolders_shouldReturnAllBusinessStakeHoldersWithDifferentSellers() {

		final BusinessStakeHolderModel stkOne = BusinessStakeHolderModel.builder().userToken(TOKEN_1)
				.clientUserId("0001").token("STK1_TOKEN")
				.firstName(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						BusinessStakeHolderConstants.FIRST_NAME, "john")), 1)
				.build();
		final BusinessStakeHolderModel stkTwo = BusinessStakeHolderModel.builder().userToken(TOKEN_2)
				.firstName(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						BusinessStakeHolderConstants.FIRST_NAME, "susan")), 1)
				.clientUserId("0002").token("STK2_TOKEN").build();

		final SellerModel sellerModelOne = SellerModel.builder().token(TOKEN_1)
				.businessStakeHolderDetails(List.of(stkOne)).clientUserId(CLIENT_ID_1).build();
		final SellerModel sellerModelTwo = SellerModel.builder().token(TOKEN_2)
				.businessStakeHolderDetails(List.of(stkTwo)).clientUserId(CLIENT_ID_2).build();

		final List<BusinessStakeHolderModel> result = testObj
				.extractBusinessStakeHolders(List.of(sellerModelOne, sellerModelTwo));

		assertThat(result).containsExactly(stkOne, stkTwo);
	}

}
