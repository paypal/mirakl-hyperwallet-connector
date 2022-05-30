package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.sellers.sellersextract.model.SellerModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountExtractJobItemTest {

	private static final String CLIENT_USER_ID = "clientUserId";

	private static final String BANK_ACCOUNT = "BankAccount";

	@Test
	void getItemId_ShouldReturnTheClientUserId() {

		final SellerModel sellerModel = SellerModel.builder().clientUserId(CLIENT_USER_ID).build();
		final BankAccountExtractJobItem testObj = new BankAccountExtractJobItem(sellerModel);

		assertThat(testObj.getItemId()).isEqualTo(CLIENT_USER_ID);
	}

	@Test
	void getItemType_ShouldReturnBankAccount() {

		final SellerModel sellerModel = SellerModel.builder().clientUserId(CLIENT_USER_ID).build();
		final BankAccountExtractJobItem testObj = new BankAccountExtractJobItem(sellerModel);

		assertThat(testObj.getItemType()).isEqualTo(BANK_ACCOUNT);
	}

}
