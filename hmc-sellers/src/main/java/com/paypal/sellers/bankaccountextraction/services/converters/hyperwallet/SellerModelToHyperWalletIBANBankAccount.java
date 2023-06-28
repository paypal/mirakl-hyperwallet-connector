package com.paypal.sellers.bankaccountextraction.services.converters.hyperwallet;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextraction.model.IBANBankAccountModel;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Class to convert from {@link IBANBankAccountModel} to {@link HyperwalletBankAccount}
 */
@Service
public class SellerModelToHyperWalletIBANBankAccount extends AbstractSellerModelToHyperwalletBankAccount {

	@Override
	public HyperwalletBankAccount execute(final SellerModel source) {
		final var hyperwalletBankAccount = callSuperConvert(source);
		final var bankAccountDetails = (IBANBankAccountModel) source.getBankAccountDetails();
		hyperwalletBankAccount.setBankId(bankAccountDetails.getBankBic());

		return hyperwalletBankAccount;
	}

	protected HyperwalletBankAccount callSuperConvert(final SellerModel source) {
		return super.execute(source);
	}

	/**
	 * Returns true if bankAccountDetails of {@link SellerModel} is of type
	 * {@link IBANBankAccountModel}
	 * @param source the source object
	 * @return true if bankAccountDetails of {@link SellerModel} is of type
	 * {@link IBANBankAccountModel}, false otherwise
	 */
	@Override
	public boolean isApplicable(final SellerModel source) {
		return Objects.nonNull(source.getBankAccountDetails())
				&& source.getBankAccountDetails() instanceof IBANBankAccountModel;
	}

}
