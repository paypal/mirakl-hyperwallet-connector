package com.paypal.sellers.sellersextract.model;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.paypal.sellers.sellersextract.model.BusinessStakeHolderConstants.*;
import static com.paypal.sellers.sellersextract.model.SellerModelConstants.HYPERWALLET_PROGRAM;
import static com.paypal.sellers.sellersextract.model.SellerModelConstants.HYPERWALLET_USER_TOKEN;

/**
 * Creates an object of type {@link BusinessStakeHolderModel}
 */
@Slf4j
@Getter
@Builder
public class BusinessStakeHolderModel {

	private final String timeZone;

	private final int stkId;

	private final boolean justCreated;

	private final String userToken;

	private final String clientUserId;

	private final String token;

	private final Boolean businessContact;

	private final Boolean director;

	private final Boolean ubo;

	private final Boolean smo;

	private final String firstName;

	private final String middleName;

	private final String lastName;

	private final Date dateOfBirth;

	private final String countryOfBirth;

	private final String countryOfNationality;

	private final SellerGender gender;

	private final String phoneNumber;

	private final String mobileNumber;

	private final String email;

	private final String governmentId;

	private final SellerGovernmentIdType governmentIdType;

	private final String driversLicenseId;

	private final String addressLine1;

	private final String addressLine2;

	private final String city;

	private final String stateProvince;

	private final String country;

	private final String postalCode;

	private final String hyperwalletProgram;

	public BusinessStakeHolderModelBuilder toBuilder() {
		//@formatter:off
        return BusinessStakeHolderModel.builder()
                .stkId(stkId)
                .userToken(userToken)
                .clientUserId(clientUserId)
                .token(token)
                .businessContact(businessContact)
                .director(director)
                .ubo(ubo)
                .smo(smo)
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
								.timeZone(timeZone)
                .dateOfBirth(dateOfBirth)
                .countryOfBirth(countryOfBirth)
                .countryOfNationality(countryOfNationality)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .mobileNumber(mobileNumber)
                .email(email)
                .governmentId(governmentId)
                .governmentIdType(governmentIdType)
                .driversLicenseId(driversLicenseId)
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .city(city)
                .stateProvince(stateProvince)
                .country(country)
                .postalCode(postalCode)
                .hyperwalletProgram(hyperwalletProgram);
        //@formatter:on
	}

	public boolean isEmpty() {
		return Objects.isNull(token) && Objects.isNull(businessContact) && Objects.isNull(director)
				&& Objects.isNull(ubo) && Objects.isNull(smo) && Objects.isNull(firstName) && Objects.isNull(middleName)
				&& Objects.isNull(lastName) && Objects.isNull(dateOfBirth) && Objects.isNull(countryOfBirth)
				&& Objects.isNull(countryOfNationality) && Objects.isNull(gender) && Objects.isNull(phoneNumber)
				&& Objects.isNull(mobileNumber) && Objects.isNull(email) && Objects.isNull(governmentId)
				&& Objects.isNull(governmentIdType) && Objects.isNull(driversLicenseId) && Objects.isNull(addressLine1)
				&& Objects.isNull(addressLine2) && Objects.isNull(city) && Objects.isNull(stateProvince)
				&& Objects.isNull(country) && Objects.isNull(postalCode);
	}

	public static class BusinessStakeHolderModelBuilder {

		private static final String BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE = "Business Stake Holder number {} incorrect. System only allows Business stake holder attributes from 1 to 5";

		public BusinessStakeHolderModelBuilder token(final String token) {
			this.token = token;

			return this;
		}

		public BusinessStakeHolderModelBuilder token(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				token = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(TOKEN, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder businessContact(final List<MiraklAdditionalFieldValue> fields,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				getMiraklBooleanCustomFieldValue(fields, getCustomFieldCode(BUSINESS, businessStakeHolderNumber))
						.ifPresent(retrievedBusinessContact -> businessContact = Boolean
								.valueOf(retrievedBusinessContact));
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder userToken(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklStringCustomFieldValue(fields, HYPERWALLET_USER_TOKEN)
					.ifPresent(retrievedToken -> userToken = retrievedToken);
			return this;
		}

		private BusinessStakeHolderModelBuilder businessContact(final Boolean businessContact) {
			this.businessContact = businessContact;
			return this;
		}

		public BusinessStakeHolderModelBuilder director(final List<MiraklAdditionalFieldValue> fields,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				getMiraklBooleanCustomFieldValue(fields, getCustomFieldCode(DIRECTOR, businessStakeHolderNumber))
						.ifPresent(retrievedDirector -> director = Boolean.valueOf(retrievedDirector));
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		private BusinessStakeHolderModelBuilder director(final Boolean director) {

			this.director = director;
			return this;
		}

		public BusinessStakeHolderModelBuilder ubo(final List<MiraklAdditionalFieldValue> fields,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				getMiraklBooleanCustomFieldValue(fields, getCustomFieldCode(UBO, businessStakeHolderNumber))
						.ifPresent(retrievedUbo -> ubo = Boolean.valueOf(retrievedUbo));
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder smo(final List<MiraklAdditionalFieldValue> fields,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				getMiraklBooleanCustomFieldValue(fields, getCustomFieldCode(SMO, businessStakeHolderNumber))
						.ifPresent(retrievedSmo -> smo = Boolean.valueOf(retrievedSmo));
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder firstName(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				firstName = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(FIRST_NAME, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder middleName(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				middleName = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(MIDDLE_NAME, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder lastName(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				lastName = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(LAST_NAME, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder dateOfBirth(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				dateOfBirth = getMiraklDateCustomFieldValue(fieldValues,
						getCustomFieldCode(DOB, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder countryOfBirth(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				countryOfBirth = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(COUNTRY_OF_BIRTH, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder countryOfNationality(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				countryOfNationality = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(NATIONALITY, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder gender(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				getMiraklSingleValueListCustomFieldValue(fieldValues,
						getCustomFieldCode(GENDER, businessStakeHolderNumber))
								.ifPresent(retrievedGovernmentIdType -> gender = EnumUtils.getEnum(SellerGender.class,
										retrievedGovernmentIdType));
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder phoneNumber(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				phoneNumber = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(PHONE_NUMBER, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder mobileNumber(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				mobileNumber = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(MOBILE_NUMBER, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder email(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				email = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(EMAIL, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder governmentId(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				governmentId = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(GOVERNMENT_ID_NUM, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder governmentIdType(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				getMiraklSingleValueListCustomFieldValue(fieldValues,
						getCustomFieldCode(GOVERNMENT_ID_TYPE, businessStakeHolderNumber))
								.ifPresent(retrievedGovernmentIdType -> governmentIdType = EnumUtils
										.getEnum(SellerGovernmentIdType.class, retrievedGovernmentIdType));
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder driversLicenseId(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				driversLicenseId = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(DRIVERS_LICENSE_NUM, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder addressLine1(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				addressLine1 = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(ADDRESS_LINE_1, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder addressLine2(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				addressLine2 = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(ADDRESS_LINE_2, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder city(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				city = getMiraklStringCustomFieldValue(fieldValues, getCustomFieldCode(CITY, businessStakeHolderNumber))
						.orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder country(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				country = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(COUNTRY, businessStakeHolderNumber)).orElse(null);
				return this;
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder stateProvince(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				stateProvince = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(STATE, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder postalCode(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				postalCode = getMiraklStringCustomFieldValue(fieldValues,
						getCustomFieldCode(POST_CODE, businessStakeHolderNumber)).orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public BusinessStakeHolderModelBuilder hyperwalletProgram(final List<MiraklAdditionalFieldValue> fieldValues) {
			getMiraklSingleValueListCustomFieldValue(fieldValues, HYPERWALLET_PROGRAM)
					.ifPresent(hyperwalletProgramValue -> this.hyperwalletProgram = hyperwalletProgramValue);

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
			//@formatter:off
            return fields.stream().filter(field -> field.getCode().equals(customFieldCode))
                    .filter(MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue.class::isInstance)
                    .map(MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue.class::cast)
                    .findAny()
                    .map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue);
            //@formatter:on
		}

		private Optional<String> getMiraklBooleanCustomFieldValue(final List<MiraklAdditionalFieldValue> fields,
				final String customFieldCode) {
			return fields.stream().filter(field -> field.getCode().equals(customFieldCode))
					.filter(MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue.class::isInstance)
					.map(MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue.class::cast).findAny()
					.map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue);
		}

		private Optional<Date> getMiraklDateCustomFieldValue(final List<MiraklAdditionalFieldValue> fieldValues,
				final String customFieldCode) {
			return fieldValues.stream().filter(field -> field.getCode().equals(customFieldCode))
					.filter(MiraklAdditionalFieldValue.MiraklDateAdditionalFieldValue.class::isInstance)
					.map(MiraklAdditionalFieldValue.MiraklDateAdditionalFieldValue.class::cast).findAny()
					.map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue)
					.map((dateAsStringISO8601 -> {
						try {
							final ZonedDateTime zonedDateTime = Instant.parse(dateAsStringISO8601)
									.atZone(ZoneId.of(timeZone));
							long offsetMillis = TimeUnit.SECONDS
									.toMillis(ZoneOffset.from(zonedDateTime).getTotalSeconds());
							long isoMillis = zonedDateTime.toInstant().toEpochMilli();
							return new Date(isoMillis + offsetMillis);
						}
						catch (final DateTimeParseException dtpex) {
							log.error("Date value with [{}] is not in the correct ISO8601 format", dateAsStringISO8601);
							return null;
						}
					}));
		}

		@NotNull
		private String getCustomFieldCode(final String customField, final Integer businessStakeHolderNumber) {
			return customField.concat("-").concat(String.valueOf(businessStakeHolderNumber));
		}

		private boolean validateBusinessStakeHolderNumber(final Integer businessStakeHolderNumber) {
			return Objects.nonNull(businessStakeHolderNumber) && businessStakeHolderNumber > 0
					&& businessStakeHolderNumber < 6;
		}

		private BusinessStakeHolderModelBuilder postalCode(final String postalCode) {
			this.postalCode = postalCode;

			return this;
		}

		private BusinessStakeHolderModelBuilder country(final String country) {
			this.country = country;

			return this;
		}

		private BusinessStakeHolderModelBuilder stateProvince(final String stateProvince) {
			this.stateProvince = stateProvince;

			return this;
		}

		private BusinessStakeHolderModelBuilder city(final String city) {
			this.city = city;

			return this;
		}

		private BusinessStakeHolderModelBuilder addressLine2(final String addressLine2) {
			this.addressLine2 = addressLine2;

			return this;
		}

		private BusinessStakeHolderModelBuilder addressLine1(final String addressLine1) {
			this.addressLine1 = addressLine1;

			return this;
		}

		private BusinessStakeHolderModelBuilder driversLicenseId(final String driversLicenseId) {
			this.driversLicenseId = driversLicenseId;

			return this;
		}

		private BusinessStakeHolderModelBuilder governmentIdType(final SellerGovernmentIdType governmentIdType) {
			this.governmentIdType = governmentIdType;

			return this;
		}

		private BusinessStakeHolderModelBuilder governmentId(final String governmentId) {
			this.governmentId = governmentId;

			return this;
		}

		private BusinessStakeHolderModelBuilder email(final String email) {
			this.email = email;

			return this;
		}

		private BusinessStakeHolderModelBuilder mobileNumber(final String mobileNumber) {
			this.mobileNumber = mobileNumber;

			return this;
		}

		private BusinessStakeHolderModelBuilder phoneNumber(final String phoneNumber) {
			this.phoneNumber = phoneNumber;

			return this;
		}

		private BusinessStakeHolderModelBuilder gender(final SellerGender gender) {
			this.gender = gender;

			return this;
		}

		private BusinessStakeHolderModelBuilder countryOfNationality(final String countryOfNationality) {
			this.countryOfNationality = countryOfNationality;

			return this;
		}

		private BusinessStakeHolderModelBuilder countryOfBirth(final String countryOfBirth) {
			this.countryOfBirth = countryOfBirth;

			return this;
		}

		private BusinessStakeHolderModelBuilder dateOfBirth(final Date dateOfBirth) {
			this.dateOfBirth = dateOfBirth;

			return this;
		}

		private BusinessStakeHolderModelBuilder lastName(final String lastName) {
			this.lastName = lastName;

			return this;
		}

		private BusinessStakeHolderModelBuilder middleName(final String middleName) {
			this.middleName = middleName;

			return this;
		}

		private BusinessStakeHolderModelBuilder firstName(final String firstName) {
			this.firstName = firstName;

			return this;
		}

		private BusinessStakeHolderModelBuilder smo(final Boolean smo) {
			this.smo = smo;

			return this;
		}

		private BusinessStakeHolderModelBuilder ubo(final Boolean ubo) {
			this.ubo = ubo;

			return this;
		}

		public BusinessStakeHolderModelBuilder userToken(final String userToken) {
			this.userToken = userToken;

			return this;
		}

		public BusinessStakeHolderModelBuilder hyperwalletProgram(final String hyperwalletProgram) {
			this.hyperwalletProgram = hyperwalletProgram;

			return this;
		}

	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BusinessStakeHolderModel)) {
			return false;
		}
		//@formatter:off
        final BusinessStakeHolderModel that = (BusinessStakeHolderModel) o;
        return EqualsBuilder.reflectionEquals(this, that);
        //@formatter:on
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
