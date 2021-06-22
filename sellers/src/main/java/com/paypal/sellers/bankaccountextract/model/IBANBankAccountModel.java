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
