package com.paypal.sellers.stakeholdersextraction.batchjobs;

import com.paypal.sellers.stakeholdersextraction.batchjobs.BusinessStakeholderExtractJobItem;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessStakeholderExtractJobItemTest {

	private static final String CLIENT_USER_ID = "clientUserId";

	private static final int STK_ID = 100;

	private static final String BUSINESS_STAKEHOLDER = "BusinessStakeholder";

	@Test
	void getItemId_ShouldReturnClientUserIdAndStkId() {

		final BusinessStakeHolderModel businessStakeHolderModel = BusinessStakeHolderModel.builder()
				.clientUserId(CLIENT_USER_ID).stkId(STK_ID).build();

		final BusinessStakeholderExtractJobItem testObj = new BusinessStakeholderExtractJobItem(
				businessStakeHolderModel);

		assertThat(testObj.getItemId()).isEqualTo(CLIENT_USER_ID + "-" + STK_ID);
	}

	@Test
	void getItemType_ShouldReturnBusinessStakeholder() {

		final BusinessStakeHolderModel businessStakeHolderModel = BusinessStakeHolderModel.builder()
				.clientUserId(CLIENT_USER_ID).stkId(STK_ID).build();

		final BusinessStakeholderExtractJobItem testObj = new BusinessStakeholderExtractJobItem(
				businessStakeHolderModel);

		assertThat(testObj.getItemType()).isEqualTo(BUSINESS_STAKEHOLDER);
	}

}
