package com.paypal.sellers.bankaccountextraction.model;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.infrastructure.support.countries.CountriesUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.money.Monetary;
import javax.money.UnknownCurrencyException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static com.paypal.sellers.sellerextractioncommons.model.SellerModelConstants.*;

/**
 * Creates an object of type {@link BankAccountModel}
 */
@Slf4j
@Getter
public class BankAccountModel {

	protected final String transferMethodCountry;

	protected final String transferMethodCurrency;

	protected final TransferType transferType;

	protected final BankAccountType type;

	protected final String bankAccountNumber;

	protected final String businessName;

	protected final String firstName;

	protected final String lastName;

	protected final String country;

	protected final String addressLine1;

	protected final String addressLine2;

	protected final String city;

	protected final String stateProvince;

	protected final String postalCode;

	protected final String token;

	protected final String hyperwalletProgram;

	protected BankAccountModel(final Builder<?> builder) {
		transferMethodCountry = builder.transferMethodCountry;
		transferMethodCurrency = builder.transferMethodCurrency;
		transferType = builder.transferType;
		type = builder.type;
		bankAccountNumber = builder.bankAccountNumber;
		businessName = builder.businessName;
		addressLine1 = builder.addressLine1;
		addressLine2 = builder.addressLine2;
		firstName = builder.firstName;
		lastName = builder.lastName;
		city = builder.city;
		stateProvince = builder.stateProvince;
		country = builder.country;
		postalCode = builder.postalCode;
		token = builder.token;
		hyperwalletProgram = builder.hyperwalletProgram;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BankAccountModel)) {
			return false;
		}
		final BankAccountModel that = (BankAccountModel) o;
		//@formatter:off
		return Objects.equals(getTransferMethodCountry(), that.getTransferMethodCountry())
				&& Objects.equals(getTransferMethodCurrency(), that.getTransferMethodCurrency())
				&& getTransferType() == that.getTransferType() && getType() == that.getType()
				&& Objects.equals(getBankAccountNumber(), that.getBankAccountNumber())
				&& Objects.equals(getBusinessName(), that.getBusinessName())
				&& Objects.equals(getFirstName(), that.getFirstName())
				&& Objects.equals(getLastName(), that.getLastName())
				&& Objects.equals(getCountry(), that.getCountry())
				&& Objects.equals(getAddressLine1(), that.getAddressLine1())
				&& Objects.equals(getAddressLine2(), that.getAddressLine2())
				&& Objects.equals(getCity(), that.getCity())
				&& Objects.equals(getStateProvince(), that.getStateProvince())
				&& Objects.equals(getPostalCode(), that.getPostalCode())
				&& Objects.equals(getToken(), that.getToken())
				&& Objects.equals(getHyperwalletProgram(), that.getHyperwalletProgram());
		//@formatter:on
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTransferMethodCountry(), getTransferMethodCurrency(), getTransferType(), getType(),
				getBankAccountNumber(), getBusinessName(), getFirstName(), getLastName(), getCountry(),
				getAddressLine1(), getAddressLine2(), getCity(), getStateProvince(), getPostalCode(), getToken());
	}

	@SuppressWarnings("java:S3740")
	public Builder toBuilder() {
		//@formatter:off
		return BankAccountModel.builder()
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
				.hyperwalletProgram(hyperwalletProgram);
		//@formatter:on
	}

	@SuppressWarnings("java:S3740")
	public static Builder builder() {
		return new Builder() {
			@Override
			public Builder getThis() {
				return this;
			}
		};
	}

	public abstract static class Builder<T extends Builder<T>> {

		protected String transferMethodCountry;

		protected String transferMethodCurrency;

		protected TransferType transferType;

		protected BankAccountType type;

		protected String bankAccountNumber;

		protected String businessName;

		protected String firstName;

		protected String lastName;

		protected String country;

		protected String addressLine1;

		protected String addressLine2;

		protected String city;

		protected String stateProvince;

		protected String postalCode;

		protected String token;

		protected String hyperwalletProgram;

		public abstract T getThis();

		public T transferMethodCountry(final String transferMethodCountry) {
			final Locale countryLocale = CountriesUtil.getLocaleByIsocode(transferMethodCountry)
					.orElseThrow(() -> new IllegalStateException(
							"Country with isocode: [%s] not valid".formatted(transferMethodCountry)));

			this.transferMethodCountry = countryLocale.getCountry();
			return getThis();
		}

		protected T buildTransferMethodCountry(final String transferMethodCountry) {
			this.transferMethodCountry = transferMethodCountry;
			return getThis();
		}

		public T transferMethodCurrency(final String transferMethodCurrency) {
			try {
				Optional.of(Monetary.getCurrency(transferMethodCurrency))
						.ifPresent(currency -> this.transferMethodCurrency = currency.getCurrencyCode());
			}
			catch (final UnknownCurrencyException ex) {
				throw new IllegalStateException(
						"Transfer method currency with code: [%s] not valid".formatted(transferMethodCurrency), ex);
			}
			return getThis();
		}

		protected T buildTransferMethodCurrency(final String transferMethodCurrency) {
			this.transferMethodCurrency = transferMethodCurrency;
			return getThis();
		}

		public T transferType(final TransferType type) {
			transferType = type;
			return getThis();
		}

		public T type(final BankAccountType type) {
			this.type = type;
			return getThis();
		}

		public T bankAccountNumber(final String bankAccountNumber) {
			this.bankAccountNumber = bankAccountNumber;
			return getThis();
		}

		public T businessName(final String businessName) {
			this.businessName = businessName;
			return getThis();
		}

		public T firstName(final String firstName) {
			this.firstName = firstName;
			return getThis();
		}

		public T lastName(final String lastName) {
			this.lastName = lastName;
			return getThis();
		}

		public T country(final String country) {
			final Locale countryLocale = CountriesUtil.getLocaleByIsocode(country).orElseThrow(
					() -> new IllegalStateException("Country with isocode: [%s] not valid".formatted(country)));

			this.country = countryLocale.getCountry();
			return getThis();
		}

		protected T buildCountry(final String country) {
			this.country = country;
			return getThis();
		}

		public T addressLine1(final String addressLine1) {
			this.addressLine1 = addressLine1;
			return getThis();
		}

		public T addressLine2(final String addressLine2) {
			this.addressLine2 = addressLine2;
			return getThis();
		}

		public T city(final String city) {
			this.city = city;
			return getThis();
		}

		public T stateProvince(final List<MiraklAdditionalFieldValue> fieldValues) {
			stateProvince = getMiraklStringCustomFieldValue(fieldValues, HYPERWALLET_BANK_ACCOUNT_STATE).orElse(null);
			return getThis();
		}

		public T stateProvince(final String stateProvince) {
			this.stateProvince = stateProvince;
			return getThis();
		}

		public T postalCode(final String postalCode) {
			this.postalCode = postalCode;
			return getThis();
		}

		public T token(final List<MiraklAdditionalFieldValue> fieldValues) {
			token = getMiraklStringCustomFieldValue(fieldValues, HYPERWALLET_BANK_ACCOUNT_TOKEN).orElse(null);

			return getThis();
		}

		public T token(final String token) {
			this.token = token;
			return getThis();
		}

		public T hyperwalletProgram(final String hyperwalletProgram) {
			this.hyperwalletProgram = hyperwalletProgram;
			return getThis();
		}

		public T hyperwalletProgram(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklSingleValueListCustomFieldValue(fields, HYPERWALLET_PROGRAM)
					.ifPresent(hyperwalletProgramValue -> this.hyperwalletProgram = hyperwalletProgramValue);

			return getThis();
		}

		public BankAccountModel build() {
			return new BankAccountModel(this);
		}

		private Optional<String> getMiraklStringCustomFieldValue(final List<MiraklAdditionalFieldValue> fields,
				final String customFieldCode) {
			//@formatter:off
			return fields.stream().filter(field -> field.getCode().equals(customFieldCode))
					.filter(MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue.class::isInstance)
					.map(MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue.class::cast)
					.findAny()
					.map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue);
			//@formatter:on
		}

		private Optional<String> getMiraklSingleValueListCustomFieldValue(final List<MiraklAdditionalFieldValue> fields,
				final String customFieldCode) {
			//@formatter:off
			return fields.stream()
					.filter(field -> field.getCode().equals(customFieldCode))
					.filter(MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue.class::isInstance)
					.map(MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue.class::cast).findAny()
					.map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue);
			//@formatter:on
		}

	}

}
