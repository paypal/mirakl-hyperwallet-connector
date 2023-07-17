package com.paypal.sellers.bankaccountextraction.services.converters.hyperwallet;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextraction.model.UKBankAccountModel;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Class to convert from {@link UKBankAccountModel} to {@link HyperwalletBankAccount}
 */
@Service
public class SellerModelToHyperWalletUKBankAccount extends AbstractSellerModelToHyperwalletBankAccount {

	@Override
	public HyperwalletBankAccount execute(final SellerModel source) {
		final var hyperwalletBankAccount = callSuperConvert(source);
		final var bankAccountDetails = (UKBankAccountModel) source.getBankAccountDetails();
		hyperwalletBankAccount.setBankId(bankAccountDetails.getBankAccountId());

		return hyperwalletBankAccount;
	}

	protected HyperwalletBankAccount callSuperConvert(final SellerModel source) {
		return super.execute(source);
	}

	/**
	 * Returns true if bankAccountDetails of {@link SellerModel} is of type
	 * {@link UKBankAccountModel}
	 * @param source the source object
	 * @return true if bankAccountDetails of {@link SellerModel} is of type
	 * {@link UKBankAccountModel}, false otherwise
	 */
	@Override
	public boolean isApplicable(final SellerModel source) {
		return Objects.nonNull(source.getBankAccountDetails())
				&& source.getBankAccountDetails() instanceof UKBankAccountModel;
	}

}
