package com.paypal.sellers.sellerextractioncommons.model;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import com.paypal.infrastructure.support.countries.CountriesUtil;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.paypal.sellers.sellerextractioncommons.model.SellerModelConstants.*;

@Slf4j
@Getter
@Builder
public class SellerModel {

	protected final String timeZone;

	private final String clientUserId;

	private final String firstName;

	private final String lastName;

	private final Date dateOfBirth;

	private final String countryOfBirth;

	private final String countryOfNationality;

	private final String gender;

	private final String phoneNumber;

	private final String mobilePhone;

	private final String email;

	private final String governmentId;

	private final SellerGovernmentIdType governmentIdType;

	private final String passportId;

	private final String driversLicenseId;

	private final String addressLine1;

	private final String addressLine2;

	private final String city;

	private final String stateProvince;

	private final String country;

	private final String postalCode;

	private final String language;

	private final String programToken;

	private final SellerBusinessType businessType;

	private final String businessName;

	private final String token;

	private final BankAccountModel bankAccountDetails;

	private final SellerProfileType profileType;

	private final String companyName;

	private final String companyRegistrationNumber;

	private final String companyRegistrationCountry;

	private final String businessRegistrationStateProvince;

	private final String vatNumber;

	private final boolean hwTermsConsent;

	private final List<BusinessStakeHolderModel> businessStakeHolderDetails;

	private final String hyperwalletProgram;

	public SellerModelBuilder toBuilder() {
		//@formatter:off
		return SellerModel.builder()
				.clientUserId(clientUserId)
				.firstName(firstName)
				.lastName(lastName)
				.timeZone(timeZone)
				.dateOfBirth(dateOfBirth)
				.countryOfBirth(countryOfBirth)
				.countryOfNationality(countryOfNationality)
				.gender(gender)
				.phoneNumber(phoneNumber)
				.mobilePhone(mobilePhone)
				.email(email)
				.governmentId(governmentId)
				.governmentIdType(governmentIdType)
				.passportId(passportId)
				.driversLicenseId(driversLicenseId)
				.addressLine1(addressLine1)
				.addressLine2(addressLine2)
				.city(city)
				.stateProvince(stateProvince)
				.internalBusinessRegistrationStateProvince(businessRegistrationStateProvince)
				.internalCountry(country)
				.internalCompanyRegistrationCountry(companyRegistrationCountry)
				.postalCode(postalCode)
				.language(language)
				.programToken(programToken)
				.businessType(businessType)
				.businessName(businessName)
				.token(token)
				.bankAccountDetails(bankAccountDetails)
				.profileType(profileType)
				.companyName(companyName)
				.companyRegistrationNumber(companyRegistrationNumber)
				.vatNumber(vatNumber)
				.businessStakeHolderDetails(businessStakeHolderDetails)
				.hyperwalletProgram(hyperwalletProgram);
		//@formatter:on
	}

	public boolean hasAcceptedTermsAndConditions() {
		return isHwTermsConsent() || Objects.nonNull(getToken());
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SellerModel)) {
			return false;
		}
		final SellerModel that = (SellerModel) o;

		return EqualsBuilder.reflectionEquals(this, that, "businessStakeHolderDetails") && CollectionUtils
				.isEqualCollection(Optional.ofNullable(getBusinessStakeHolderDetails()).orElse(List.of()),
						Optional.ofNullable(that.businessStakeHolderDetails).orElse(List.of()));
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public static class SellerModelBuilder {

		private SellerModelBuilder internalCountry(final String country) {
			this.country = country;
			return this;
		}

		private SellerModelBuilder internalCompanyRegistrationCountry(final String companyRegistrationCountry) {
			this.companyRegistrationCountry = companyRegistrationCountry;
			return this;
		}

		private SellerModelBuilder internalBusinessRegistrationStateProvince(
				final String businessRegistrationStateProvince) {
			this.businessRegistrationStateProvince = businessRegistrationStateProvince;
			return this;
		}

		public SellerModelBuilder country(final String country) {
			this.country = transform3CharIsocodeTo2CharIsocode(country);
			return this;
		}

		public SellerModelBuilder companyRegistrationCountry(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklStringCustomFieldValue(fields, HYPERWALLET_BUSINESS_REGISTRATION_COUNTRY)
					.ifPresent(countryIsocode -> companyRegistrationCountry = countryIsocode);

			return this;
		}

		public SellerModelBuilder businessRegistrationStateProvince(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklStringCustomFieldValue(fields, HYPERWALLET_BUSINESS_REGISTRATION_STATE_PROVINCE).ifPresent(
					businessRegistrationStateProvinceValue -> this.businessRegistrationStateProvince = businessRegistrationStateProvinceValue);

			return this;
		}

		public SellerModelBuilder token(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklStringCustomFieldValue(fields, HYPERWALLET_USER_TOKEN)
					.ifPresent(retrievedToken -> token = retrievedToken);
			return this;
		}

		public SellerModelBuilder dateOfBirth(final List<MiraklAdditionalFieldValue> fields) {
			//@formatter:off
			fields.stream().filter(field -> field.getCode().equals(DATE_OF_BIRTH))
					.filter(MiraklAdditionalFieldValue.MiraklDateAdditionalFieldValue.class::isInstance)
					.map(MiraklAdditionalFieldValue.MiraklDateAdditionalFieldValue.class::cast).findAny()
					.map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue)
					.ifPresent(dateAsStringISO8601 -> {
						final ZonedDateTime zonedDateTime = Instant.parse(dateAsStringISO8601).atZone(ZoneId.of(timeZone));
						final long offsetMillis = TimeUnit.SECONDS.toMillis(ZoneOffset.from(zonedDateTime).getTotalSeconds());
						final long isoMillis = zonedDateTime.toInstant().toEpochMilli();
						dateOfBirth = new Date(isoMillis + offsetMillis);
					});
			//@formatter:on

			return this;
		}

		public SellerModelBuilder hwTermsConsent(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklBooleanCustomFieldValue(fields, HYPERWALLET_TERMS_CONSENT)
					.ifPresent(termsConsent -> hwTermsConsent = Boolean.parseBoolean(termsConsent));
			return this;
		}

		public SellerModelBuilder countryOfBirth(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklStringCustomFieldValue(fields, COUNTRY_OF_BIRTH)
					.ifPresent(retrievedCountryOfBirth -> countryOfBirth = retrievedCountryOfBirth);
			return this;
		}

		public SellerModelBuilder countryOfNationality(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklStringCustomFieldValue(fields, COUNTRY_OF_NATIONALITY)
					.ifPresent(retrievedCountryOfNationality -> countryOfNationality = retrievedCountryOfNationality);
			return this;
		}

		public SellerModelBuilder governmentId(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklStringCustomFieldValue(fields, GOVERNMENT_ID)
					.ifPresent(retrievedGovernmentId -> governmentId = retrievedGovernmentId);
			return this;
		}

		public SellerModelBuilder governmentIdType(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklSingleValueListCustomFieldValue(fields, GOVERNMENT_ID_TYPE)
					.ifPresent(retrievedGovernmentIdType -> governmentIdType = EnumUtils
							.getEnum(SellerGovernmentIdType.class, retrievedGovernmentIdType));
			return this;
		}

		public SellerModelBuilder passportId(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklStringCustomFieldValue(fields, PASSPORT_ID)
					.ifPresent(retrievedPassportId -> passportId = retrievedPassportId);
			return this;
		}

		public SellerModelBuilder driversLicenseId(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklStringCustomFieldValue(fields, DRIVERS_LICENSE_ID)
					.ifPresent(retrievedDriversLicenseId -> driversLicenseId = retrievedDriversLicenseId);
			return this;
		}

		public SellerModelBuilder businessType(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklSingleValueListCustomFieldValue(fields, BUSINESS_TYPE)
					.ifPresent(retrievedBusinessType -> businessType = EnumUtils.getEnum(SellerBusinessType.class,
							retrievedBusinessType));
			return this;
		}

		public SellerModelBuilder businessStakeHolderDetails(
				final List<BusinessStakeHolderModel> businessStakeHolderModels) {
			businessStakeHolderDetails = Stream.ofNullable(businessStakeHolderModels).flatMap(Collection::stream)
					.filter(Objects::nonNull)
					.map(businessStakeHolderModel -> businessStakeHolderModel.toBuilder().build())
					.collect(Collectors.toList());

			return this;

		}

		public SellerModelBuilder hyperwalletProgram(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklSingleValueListCustomFieldValue(fields, HYPERWALLET_PROGRAM)
					.ifPresent(hyperwalletProgramValue -> hyperwalletProgram = hyperwalletProgramValue);

			return this;
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

		private Optional<String> getMiraklStringCustomFieldValue(final List<MiraklAdditionalFieldValue> fields,
				final String customFieldCode) {
			return fields.stream().filter(field -> field.getCode().equals(customFieldCode))
					.filter(MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue.class::isInstance)
					.map(MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue.class::cast).findAny()
					.map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue);
		}

		private Optional<String> getMiraklBooleanCustomFieldValue(final List<MiraklAdditionalFieldValue> fields,
				final String customFieldCode) {
			return fields.stream().filter(field -> field.getCode().equals(customFieldCode))
					.filter(MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue.class::isInstance)
					.map(MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue.class::cast).findAny()
					.map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue);
		}

		private String transform3CharIsocodeTo2CharIsocode(final String country) {
			final Locale countryLocale = CountriesUtil.getLocaleByIsocode(country).orElseThrow(
					() -> new IllegalStateException("Country with isocode: [%s] not valid".formatted(country)));

			return countryLocale.getCountry();
		}

		private SellerModelBuilder dateOfBirth(final Date dateOfBirth) {
			this.dateOfBirth = dateOfBirth;

			return this;
		}

		private SellerModelBuilder countryOfBirth(final String countryOfBirth) {
			this.countryOfBirth = countryOfBirth;

			return this;
		}

		private SellerModelBuilder countryOfNationality(final String countryOfNationality) {
			this.countryOfNationality = countryOfNationality;

			return this;
		}

		private SellerModelBuilder governmentId(final String governmentId) {
			this.governmentId = governmentId;

			return this;
		}

		private SellerModelBuilder governmentIdType(final SellerGovernmentIdType governmentIdType) {
			this.governmentIdType = governmentIdType;

			return this;
		}

		private SellerModelBuilder passportId(final String passportId) {
			this.passportId = passportId;

			return this;
		}

		private SellerModelBuilder driversLicenseId(final String driversLicenseId) {
			this.driversLicenseId = driversLicenseId;

			return this;
		}

		private SellerModelBuilder businessType(final SellerBusinessType businessType) {
			this.businessType = businessType;

			return this;
		}

		public SellerModelBuilder token(final String token) {
			this.token = token;

			return this;
		}

		public SellerModelBuilder hyperwalletProgram(final String hyperwalletProgram) {
			this.hyperwalletProgram = hyperwalletProgram;
			return this;
		}

		public SellerModelBuilder bankAccountDetails(final BankAccountModel bankAccountDetails) {
			this.bankAccountDetails = bankAccountDetails;

			return this;
		}

	}

}
