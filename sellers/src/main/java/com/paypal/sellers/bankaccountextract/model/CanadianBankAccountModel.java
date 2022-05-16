package com.paypal.sellers.bankaccountextract.model;

import lombok.Getter;

/**
 * Creates an object of type {@link CanadianBankAccountModel}
 */
@Getter
public class CanadianBankAccountModel extends BankAccountModel {

	private final String bankId;

	private final String branchId;

	public CanadianBankAccountModel(final Builder builder) {
		super(builder);
		bankId = builder.bankId;
		branchId = builder.branchId;
	}

	public static CanadianBankAccountModel.Builder builder() {
		return new CanadianBankAccountModel.Builder();
	}

	@Override
	public Builder toBuilder() {
		//@formatter:off
		return CanadianBankAccountModel.builder()
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
				.bankId(bankId)
				.branchId(branchId);
		//@formatter:on
	}

	public static class Builder extends BankAccountModel.Builder<CanadianBankAccountModel.Builder> {

		private String bankId;

		private String branchId;

		@Override
		public CanadianBankAccountModel.Builder getThis() {
			return this;
		}

		public CanadianBankAccountModel.Builder branchId(final String branchId) {
			this.branchId = branchId;
			return getThis();
		}

		public CanadianBankAccountModel.Builder bankId(final String bankId) {
			this.bankId = bankId;
			return getThis();
		}

		@Override
		public CanadianBankAccountModel build() {
			return new CanadianBankAccountModel(this);
		}

	}

}
