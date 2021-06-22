package com.paypal.sellers.bankaccountextract.converter.impl.sellermodel;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextract.model.CanadianBankAccountModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Class to convert from {@link CanadianBankAccountModel} to
 * {@link HyperwalletBankAccount}
 */
@Service
public class SellerModelToHyperWalletCanadianBankAccount extends AbstractSellerModelToHyperwalletBankAccount {

	@Override
	public HyperwalletBankAccount execute(final SellerModel source) {
		final var hyperwalletBankAccount = callSuperConvert(source);
		final var bankAccountDetails = (CanadianBankAccountModel) source.getBankAccountDetails();

		hyperwalletBankAccount.setBankId(bankAccountDetails.getBankId());
		hyperwalletBankAccount.setBranchId(bankAccountDetails.getBranchId());

		return hyperwalletBankAccount;
	}

	protected HyperwalletBankAccount callSuperConvert(final SellerModel source) {
		return super.execute(source);
	}

	/**
	 * Returns true if bankAccountDetails of {@link SellerModel} is of type
	 * {@link CanadianBankAccountModel}
	 * @param source the source object
	 * @return true if bankAccountDetails of {@link SellerModel} is of type
	 * {@link CanadianBankAccountModel}, false otherwise
	 */
	@Override
	public boolean isApplicable(final SellerModel source) {
		return Objects.nonNull(source.getBankAccountDetails())
				&& source.getBankAccountDetails() instanceof CanadianBankAccountModel;
	}

}
