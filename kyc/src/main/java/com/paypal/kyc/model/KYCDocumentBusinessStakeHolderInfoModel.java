package com.paypal.kyc.model;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.paypal.kyc.model.KYCConstants.*;
import static com.paypal.kyc.model.KYCConstants.HwDocuments.PROOF_OF_AUTHORIZATION;

/**
 * Defines model for business stakeholder kyc verification
 */
@Slf4j
@Getter
public class KYCDocumentBusinessStakeHolderInfoModel extends KYCDocumentInfoModel {

	private final String token;

	private final int businessStakeholderMiraklNumber;

	private final boolean requiresLetterOfAuthorization;

	private final boolean contact;

	private final boolean notifiedDocumentsReadyForReview;

	public KYCDocumentBusinessStakeHolderInfoModel(final Builder builder) {
		super(builder);
		token = builder.token;
		businessStakeholderMiraklNumber = builder.businessStakeholderMiraklNumber;
		requiresLetterOfAuthorization = builder.requiresLetterOfAuthorization;
		contact = builder.contact;
		notifiedDocumentsReadyForReview = builder.notifiedDocumentsReadyForReview;
	}

	public static Builder builder() {
		return new Builder();
	}

	public List<KYCDocumentModel> getIdentityDocuments() {
		if (KYCProofOfIdentityEnum.PASSPORT.equals(proofOfIdentity)) {
			//@formatter:off
			return Stream.ofNullable(getDocuments())
					.flatMap(Collection::stream)
					.filter(document -> KYCDocumentCategoryEnum.IDENTIFICATION.equals(document.getDocumentCategory()))
					.filter(document -> document.getDocumentSide().equals(KYCDocumentSideEnum.FRONT))
					.collect(Collectors.toList());
			//@formatter:on
		}
		//@formatter:off
		return Stream.ofNullable(getDocuments())
				.flatMap(Collection::stream)
				.filter(document -> document.getDocumentCategory().equals(KYCDocumentCategoryEnum.IDENTIFICATION))
				.collect(Collectors.toList());
		//@formatter:on
	}

	public List<KYCDocumentModel> getLetterOfAuthorizationDocument() {
		if (isContact() && isRequiresLetterOfAuthorization()) {
			return Stream
					.ofNullable(getDocuments()).flatMap(Collection::stream).filter(kycDocumentModel -> kycDocumentModel
							.getDocumentCategory().equals(KYCDocumentCategoryEnum.AUTHORIZATION))
					.collect(Collectors.toList());
		}

		return List.of();
	}

	public boolean isIdentityDocumentsFilled() {
		if (Objects.nonNull(this.getProofOfIdentity()) && Objects.nonNull(this.getDocuments())) {
			return this.getIdentityDocuments().size() == KYCProofOfIdentityEnum.getMiraklFields(this.proofOfIdentity)
					.size();

		}
		return false;
	}

	public boolean existsLetterOfAuthorizationDocumentInMirakl() {
		final List<String> documentsExistingInMirakl = Stream.ofNullable(miraklShopDocuments)
				.flatMap(Collection::stream).map(MiraklShopDocument::getTypeCode).collect(Collectors.toList());
		if (isRequiresLetterOfAuthorization()) {
			return documentsExistingInMirakl.contains(PROOF_OF_AUTHORIZATION);
		}

		return false;
	}

	@Override
	public boolean existsDocumentInMirakl() {
		final List<String> documentsExistingInMirakl = Stream.ofNullable(miraklShopDocuments)
				.flatMap(Collection::stream).map(MiraklShopDocument::getTypeCode).collect(Collectors.toList());
		final KYCProofOfIdentityEnum proofOfIdentity = this.getProofOfIdentity();
		final List<String> proofOfIdentityMiraklFields = KYCProofOfIdentityEnum.getMiraklFields(proofOfIdentity,
				this.businessStakeholderMiraklNumber);

		return documentsExistingInMirakl.containsAll(proofOfIdentityMiraklFields);
	}

	public boolean isEmpty() {
		return Objects.isNull(token) && Objects.isNull(proofOfIdentity) && Objects.isNull(getDocuments());
	}

	public boolean hasSelectedDocumentsControlFieldsInBusinessStakeholder() {
		if (requiresKYC) {
			return Objects.nonNull(getProofOfIdentity());
		}

		return true;
	}

	public Builder toBuilder() {
		//@formatter:off
		return KYCDocumentBusinessStakeHolderInfoModel.builder()
				.token(token)
				.requiresKYC(requiresKYC)
				.requiresLetterOfAuthorization(requiresLetterOfAuthorization)
				.countryIsoCode(countryIsoCode)
				.businessStakeholderMiraklNumber(businessStakeholderMiraklNumber)
				.userToken(userToken)
				.clientUserId(clientUserId)
				.proofOfIdentity(proofOfIdentity)
				.miraklShopDocuments(miraklShopDocuments)
				.contact(contact)
				.sentToHyperwallet(sentToHyperwallet)
				.notifiedDocumentsReadyForReview(notifiedDocumentsReadyForReview)
				.hyperwalletProgram(hyperwalletProgram)
				.documents(getDocuments());
		//@formatter:on
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof KYCDocumentBusinessStakeHolderInfoModel)) {
			return false;
		}
		final KYCDocumentBusinessStakeHolderInfoModel that = (KYCDocumentBusinessStakeHolderInfoModel) o;

		return EqualsBuilder.reflectionEquals(this, that, "miraklShopDocuments", "documents")
				&& CollectionUtils.isEqualCollection(Optional.ofNullable(getMiraklShopDocuments()).orElse(List.of()),
						Optional.ofNullable(that.getMiraklShopDocuments()).orElse(List.of()))
				&& CollectionUtils.isEqualCollection(Optional.ofNullable(getDocuments()).orElse(List.of()),
						Optional.ofNullable(that.getDocuments()).orElse(List.of()));

	}

	@Override
	public boolean areDocumentsFilled() {
		boolean proofOfIdentityFilled = true;
		if (requiresKYC) {
			proofOfIdentityFilled = isIdentityDocumentsFilled();
		}
		boolean letterOfAuthorizationRequired = true;

		if (isRequiresLetterOfAuthorization()) {
			letterOfAuthorizationRequired = CollectionUtils.isNotEmpty(getLetterOfAuthorizationDocument());
		}

		return proofOfIdentityFilled && letterOfAuthorizationRequired;

	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public static class Builder extends KYCDocumentInfoModel.Builder<Builder> {

		private static final String BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE = "Business Stake Holder number {} incorrect. System only allows Business stake holder attributes from 1 to 5";

		private boolean notifiedDocumentsReadyForReview;

		private String token;

		private int businessStakeholderMiraklNumber;

		private boolean requiresLetterOfAuthorization;

		private boolean contact;

		@Override
		public Builder getThis() {
			return this;
		}

		public Builder businessStakeholderMiraklNumber(final Integer businessStakeHolderNumber) {
			this.businessStakeholderMiraklNumber = businessStakeHolderNumber;
			return this;
		}

		public Builder userToken(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklStringCustomFieldValue(fields, HYPERWALLET_USER_TOKEN_FIELD)
					.ifPresent(retrievedToken -> userToken = retrievedToken);
			return this;
		}

		public Builder token(final List<MiraklAdditionalFieldValue> fieldValues,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				token = getMiraklStringCustomFieldValue(fieldValues, getCustomFieldCode(
						HYPERWALLET_PREFIX + STAKEHOLDER_PREFIX + STAKEHOLDER_TOKEN_PREFIX, businessStakeHolderNumber))
								.orElse(null);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public Builder requiresKYC(final List<MiraklAdditionalFieldValue> fields,
				final Integer businessStakeHolderNumber) {

			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				getMiraklBooleanCustomFieldValue(fields,
						getCustomFieldCode(HYPERWALLET_PREFIX + STAKEHOLDER_PREFIX + REQUIRED_PROOF_IDENTITY,
								businessStakeHolderNumber))
										.ifPresent(termsConsent -> requiresKYC = Boolean.valueOf(termsConsent));
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public Builder proofOfIdentity(final List<MiraklAdditionalFieldValue> fields,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				getMiraklSingleValueListCustomFieldValue(fields,
						getCustomFieldCode(HYPERWALLET_PREFIX + STAKEHOLDER_PREFIX + STAKEHOLDER_PROOF_IDENTITY,
								businessStakeHolderNumber))
										.ifPresent(retrievedProofOfIdentity -> proofOfIdentity = EnumUtils
												.getEnum(KYCProofOfIdentityEnum.class, retrievedProofOfIdentity));
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public Builder countryIsoCode(final List<MiraklAdditionalFieldValue> fields,
				final Integer businessStakeHolderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeHolderNumber)) {
				getMiraklStringCustomFieldValue(fields,
						getCustomFieldCode(HYPERWALLET_PREFIX + STAKEHOLDER_PREFIX + PROOF_IDENTITY_PREFIX
								+ STAKEHOLDER_COUNTRY_PREFIX, businessStakeHolderNumber))
										.ifPresent(retrievedCountryIsoCode -> countryIsoCode = retrievedCountryIsoCode);
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeHolderNumber);
			}
			return this;
		}

		public Builder requiresLetterOfAuthorization(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklBooleanCustomFieldValue(fields, HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD)
					.ifPresent(requiresLetterOfAuthorizationValue -> requiresLetterOfAuthorization = Boolean
							.parseBoolean(requiresLetterOfAuthorizationValue));

			return this;
		}

		public Builder contact(final List<MiraklAdditionalFieldValue> fields, final int businessStakeholderNumber) {
			if (validateBusinessStakeHolderNumber(businessStakeholderNumber)) {
				getMiraklBooleanCustomFieldValue(fields,
						getCustomFieldCode(HYPERWALLET_PREFIX + STAKEHOLDER_PREFIX + CONTACT,
								businessStakeholderNumber))
										.ifPresent(isContact -> contact = Boolean.parseBoolean(isContact));
			}
			else {
				log.warn(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE, businessStakeholderNumber);
			}
			return this;
		}

		@Override
		public Builder documents(final List<KYCDocumentModel> documents) {
			this.documents = Stream.ofNullable(documents).flatMap(Collection::stream).filter(Objects::nonNull)
					.map(document -> document.toBuilder().build()).collect(Collectors.toList());
			return this;
		}

		public Builder token(final String token) {
			this.token = token;
			return getThis();
		}

		public Builder notifiedDocumentsReadyForReview(final boolean notifiedDocumentsReadyForReview) {
			this.notifiedDocumentsReadyForReview = notifiedDocumentsReadyForReview;
			return this;
		}

		@Override
		public KYCDocumentBusinessStakeHolderInfoModel build() {
			return new KYCDocumentBusinessStakeHolderInfoModel(this);
		}

		private boolean validateBusinessStakeHolderNumber(final Integer businessStakeHolderNumber) {
			return Objects.nonNull(businessStakeHolderNumber) && businessStakeHolderNumber > 0
					&& businessStakeHolderNumber < 6;
		}

		private String getCustomFieldCode(final String customField, final Integer businessStakeHolderNumber) {
			return customField.concat(String.valueOf(businessStakeHolderNumber));
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

		public Builder requiresLetterOfAuthorization(final boolean requiresLetterOfAuthorization) {
			this.requiresLetterOfAuthorization = requiresLetterOfAuthorization;

			return this;
		}

		public Builder contact(final boolean contact) {
			this.contact = contact;
			return this;
		}

	}

}
