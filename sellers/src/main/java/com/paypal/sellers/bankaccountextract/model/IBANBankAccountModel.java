package com.paypal.sellers.bankaccountextract.model;

import lombok.Getter;

/**
 * Creates an object of type {@link IBANBankAccountModel}
 */
@Getter
public class IBANBankAccountModel extends BankAccountModel {

	private final String bankBic;

	public IBANBankAccountModel(final Builder builder) {
		super(builder);
		bankBic = builder.bankBic;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public Builder toBuilder() {
		//@formatter:off
		return IBANBankAccountModel.builder()
				.buildTransferMethodCountry(transferMethodCountry)
				.buildTransferMethodCurrency(transferMethodCurrency)
				.transferType(transferType)
				.type(type)
				.bankAccountNumber(bankAccountNumber)
				.businessName(businessName)
				.firstName(firstName)
				.lastName(lastName)
				.buildCountry(country)
				.addressLine1(addressLine1)
				.addressLine2(addressLine2)
				.city(city)
				.stateProvince(stateProvince)
				.postalCode(postalCode)
				.token(token)
				.hyperwalletProgram(hyperwalletProgram)
				.bankBic(bankBic);
		//@formatter:on
	}

	public static class Builder extends BankAccountModel.Builder<Builder> {

		private String bankBic;

		@Override
		public Builder getThis() {
			return this;
		}

		public Builder bankBic(final String bankBIC) {
			bankBic = bankBIC;
			return getThis();
		}

		@Override
		public IBANBankAccountModel build() {
			return new IBANBankAccountModel(this);
		}

	}

}
