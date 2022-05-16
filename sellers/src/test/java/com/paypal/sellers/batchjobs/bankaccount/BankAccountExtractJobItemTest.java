package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static com.paypal.sellers.batchjobs.bankaccount.BankAccountExtractJobItem.BATCH_JOB_ITEM_EMPTY_ID;
import static org.assertj.core.api.Assertions.assertThat;

class BankAccountExtractJobItemTest {

	private static final String CLIENT_USER_ID = "clientUserId";

	private static final String BANK_ACCOUNT = "BankAccount";

	private static final String BANK_ACCOUNT_NUMBER = "12345678";

	@Test
	void getItemId_ShouldReturnTheClientUserId() {

		final SellerModel sellerModel = SellerModel.builder().clientUserId(CLIENT_USER_ID)
				.bankAccountDetails(BankAccountModel.builder().bankAccountNumber(BANK_ACCOUNT_NUMBER).build()).build();
		final BankAccountExtractJobItem testObj = new BankAccountExtractJobItem(sellerModel);

		assertThat(testObj.getItemId()).isEqualTo(CLIENT_USER_ID + "-*****" + "5678");
	}

	@Test
	void getItemId_ShouldReturnTheClientUserIdWithEmptySuffix_WhenBankAccountDetailsIsNull() {

		final SellerModel sellerModel = SellerModel.builder().clientUserId(CLIENT_USER_ID).bankAccountDetails(null)
				.build();
		final BankAccountExtractJobItem testObj = new BankAccountExtractJobItem(sellerModel);

		assertThat(testObj.getItemId()).isEqualTo(CLIENT_USER_ID + "-" + BATCH_JOB_ITEM_EMPTY_ID);
	}

	@Test
	void getItemId_ShouldReturnTheClientUserIdWithEmptySuffix_WhenBankAccountDetailsIsEmpty() {

		final SellerModel sellerModel = SellerModel.builder().clientUserId(CLIENT_USER_ID)
				.bankAccountDetails(BankAccountModel.builder().bankAccountNumber(StringUtils.EMPTY).build()).build();
		final BankAccountExtractJobItem testObj = new BankAccountExtractJobItem(sellerModel);

		assertThat(testObj.getItemId()).isEqualTo(CLIENT_USER_ID + "-" + BATCH_JOB_ITEM_EMPTY_ID);
	}

	@Test
	void getItemId_ShouldReturnTheClientUserIdWithEmptySuffix_WhenBankAccountDetailsIsBlank() {

		final SellerModel sellerModel = SellerModel.builder().clientUserId(CLIENT_USER_ID)
				.bankAccountDetails(BankAccountModel.builder().bankAccountNumber("  ").build()).build();
		final BankAccountExtractJobItem testObj = new BankAccountExtractJobItem(sellerModel);

		assertThat(testObj.getItemId()).isEqualTo(CLIENT_USER_ID + "-" + BATCH_JOB_ITEM_EMPTY_ID);
	}

	@Test
	void getItemType_ShouldReturnBankAccount() {

		final SellerModel sellerModel = SellerModel.builder().clientUserId(CLIENT_USER_ID).build();
		final BankAccountExtractJobItem testObj = new BankAccountExtractJobItem(sellerModel);

		assertThat(testObj.getItemType()).isEqualTo(BANK_ACCOUNT);
	}

}
