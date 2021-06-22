package com.paypal.sellers.bankaccountextract.model;

import lombok.Getter;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Creates an object of type {@link UKBankAccountModel}
 */
@Getter
public class UKBankAccountModel extends BankAccountModel {

	private final String bankAccountId;

	public UKBankAccountModel(final UKBankAccountModel.Builder builder) {
		super(builder);
		bankAccountId = builder.bankAccountId;
	}

	public static UKBankAccountModel.Builder builder() {
		return new UKBankAccountModel.Builder();
	}

	public static class Builder extends BankAccountModel.Builder<UKBankAccountModel.Builder> {

		private String bankAccountId;

		@Override
		public UKBankAccountModel.Builder getThis() {
			return this;
		}

		public UKBankAccountModel.Builder bankAccountId(final String bankAccountId) {
			this.bankAccountId = bankAccountId;
			return getThis();
		}

		@Override
		public UKBankAccountModel build() {
			return new UKBankAccountModel(this);
		}

	}

	@Override
	public boolean equals(final Object o) {
		if (super.equals(o)) {
			final UKBankAccountModel that = (UKBankAccountModel) o;
			return this.getBankAccountId().equals(that.getBankAccountId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
