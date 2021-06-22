package com.paypal.sellers.bankaccountextract.converter.impl.sellermodel;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextract.model.ABABankAccountModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Class to convert from {@link ABABankAccountModel} to {@link HyperwalletBankAccount}
 */
@Service
public class SellerModelToHyperWalletABABankAccount extends AbstractSellerModelToHyperwalletBankAccount {

	@Override
	public HyperwalletBankAccount execute(final SellerModel source) {
		final var hyperwalletBankAccount = callSuperConvert(source);
		final var bankAccountDetails = (ABABankAccountModel) source.getBankAccountDetails();

		hyperwalletBankAccount.setBranchId(bankAccountDetails.getBranchId());
		hyperwalletBankAccount.setBankAccountPurpose(bankAccountDetails.getBankAccountPurpose());
		hyperwalletBankAccount.setPostalCode(bankAccountDetails.getPostalCode());
		hyperwalletBankAccount.setStateProvince(bankAccountDetails.getStateProvince());

		return hyperwalletBankAccount;
	}

	protected HyperwalletBankAccount callSuperConvert(final SellerModel source) {
		return super.execute(source);
	}

	/**
	 * Returns true if bankAccountDetails of {@link SellerModel} is of type
	 * {@link ABABankAccountModel}
	 * @param source the source object
	 * @return true if bankAccountDetails of {@link SellerModel} is of type
	 * {@link ABABankAccountModel}, false otherwise
	 */
	@Override
	public boolean isApplicable(final SellerModel source) {
		return Objects.nonNull(source.getBankAccountDetails())
				&& source.getBankAccountDetails() instanceof ABABankAccountModel;
	}

}
