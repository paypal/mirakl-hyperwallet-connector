package com.paypal.sellers.bankaccountextract.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Creates an object of type {@link ABABankAccountModel}
 */
@Slf4j
@Getter
public class ABABankAccountModel extends BankAccountModel {

	private final String branchId;

	private final String bankAccountPurpose;

	public ABABankAccountModel(final Builder builder) {
		super(builder);
		branchId = builder.branchId;
		bankAccountPurpose = builder.bankAccountPurpose;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends BankAccountModel.Builder<Builder> {

		private String branchId;

		private String bankAccountPurpose;

		@Override
		public Builder getThis() {
			return this;
		}

		public Builder branchId(final String branchId) {
			this.branchId = branchId;
			return getThis();
		}

		public Builder bankAccountPurpose(final String bankAccountPurpose) {
			this.bankAccountPurpose = bankAccountPurpose;
			return getThis();
		}

		@Override
		public ABABankAccountModel build() {
			return new ABABankAccountModel(this);
		}

	}

}
