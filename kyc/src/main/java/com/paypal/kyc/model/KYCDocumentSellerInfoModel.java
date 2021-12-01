package com.paypal.kyc.model;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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

/**
 * Creates an object that holds all required KYC data verification for Seller(Shop)
 */
@Slf4j
@Getter
public class KYCDocumentSellerInfoModel extends KYCDocumentInfoModel {

	private final boolean professional;

	private final KYCProofOfBusinessEnum proofOfBusiness;

	private final KYCProofOfAddressEnum proofOfAddress;

	public List<KYCDocumentModel> getIdentityDocuments() {
		if (KYCProofOfIdentityEnum.PASSPORT.equals(proofOfIdentity)) {
			//@formatter:off
			return Stream.ofNullable(getDocuments())
					.flatMap(Collection::stream)
					.filter(document -> document.getDocumentCategory().equals(KYCDocumentCategoryEnum.IDENTIFICATION))
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

	public List<KYCDocumentModel> getAddressDocuments() {
		//@formatter:off
		return Stream.ofNullable(getDocuments())
				.flatMap(Collection::stream)
				.filter(document -> document.getDocumentCategory().equals(KYCDocumentCategoryEnum.ADDRESS))
				.collect(Collectors.toList());
		//@formatter:on
	}

	public List<KYCDocumentModel> getProofOfBusinessDocuments() {
		return Stream.ofNullable(getDocuments()).flatMap(Collection::stream)
				.filter(document -> document.getDocumentCategory().equals(KYCDocumentCategoryEnum.BUSINESS))
				.collect(Collectors.toList());
	}

	public boolean isIdentityDocumentsFilled() {
		if (Objects.nonNull(this.getProofOfIdentity()) && Objects.nonNull(this.getDocuments())) {
			return this.getIdentityDocuments().size() == KYCProofOfIdentityEnum.getMiraklFields(this.proofOfIdentity)
					.size();

		}
		return false;
	}

	@Override
	public boolean existsDocumentInMirakl() {
		final List<String> documentsExistingInMirakl = miraklShopDocuments.stream().map(MiraklShopDocument::getTypeCode)
				.collect(Collectors.toList());
		final KYCProofOfIdentityEnum proofOfIdentity = this.getProofOfIdentity();
		if (!isProfessional()) {
			final List<String> proofOfIdentityMiraklFields = KYCProofOfIdentityEnum.getMiraklFields(proofOfIdentity);
			final List<String> proofOfAddressMiraklFields = KYCProofOfAddressEnum.getMiraklFields();
			return documentsExistingInMirakl.containsAll(proofOfIdentityMiraklFields)
					&& documentsExistingInMirakl.containsAll(proofOfAddressMiraklFields);
		}
		else {
			final List<String> proofOfBusinessMiraklFields = KYCProofOfBusinessEnum.getMiraklFields();
			return documentsExistingInMirakl.containsAll(proofOfBusinessMiraklFields);
		}
	}

	public boolean isAddressDocumentsFilled() {
		if (Objects.nonNull(this.proofOfAddress) && Objects.nonNull(this.getDocuments())) {
			return this.getAddressDocuments().size() == KYCProofOfAddressEnum.getMiraklFields().size();
		}
		return false;
	}

	public boolean isProofOfBusinessFilled() {
		if (Objects.nonNull(this.proofOfBusiness) && Objects.nonNull(this.getDocuments())) {
			return this.getProofOfBusinessDocuments().size() == KYCProofOfBusinessEnum.getMiraklFields().size();
		}

		return false;
	}

	public boolean hasSelectedDocumentControlFields() {
		if (isProfessional()) {
			return Objects.nonNull(getProofOfBusiness());
		}

		return Objects.nonNull(getProofOfAddress()) && Objects.nonNull(getProofOfIdentity());
	}

	@Override
	public boolean areDocumentsFilled() {
		if (isProfessional()) {
			return isProofOfBusinessFilled();
		}

		return isAddressDocumentsFilled() && isIdentityDocumentsFilled();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final KYCDocumentSellerInfoModel that = (KYCDocumentSellerInfoModel) o;

		return EqualsBuilder.reflectionEquals(this, that, "documents", "miraklShopDocuments")
				&& CollectionUtils.isEqualCollection(Optional.ofNullable(getDocuments()).orElse(List.of()),
						Optional.ofNullable(that.getDocuments()).orElse(List.of()))
				&& CollectionUtils.isEqualCollection(Optional.ofNullable(miraklShopDocuments).orElse(List.of()),
						Optional.ofNullable(that.miraklShopDocuments).orElse(List.of()));
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public KYCDocumentSellerInfoModel(final Builder builder) {
		super(builder);
		professional = builder.professional;
		proofOfBusiness = builder.proofOfBusiness;
		proofOfAddress = builder.proofOfAddress;
	}

	public Builder toBuilder() {
		//@formatter:off
		return KYCDocumentSellerInfoModel.builder()
				.userToken(userToken)
				.clientUserId(clientUserId)
				.professional(professional)
				.requiresKYC(requiresKYC)
				.countryIsoCode(countryIsoCode)
				.proofOfIdentity(proofOfIdentity)
				.proofOfAddress(proofOfAddress)
				.proofOfBusiness(proofOfBusiness)
				.miraklShopDocuments(miraklShopDocuments)
				.sentToHyperwallet(sentToHyperwallet)
				.hyperwalletProgram(hyperwalletProgram)
				.documents(getDocuments());
		//@formatter:on
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends KYCDocumentInfoModel.Builder<Builder> {

		private boolean professional;

		private KYCProofOfBusinessEnum proofOfBusiness;

		private KYCProofOfAddressEnum proofOfAddress;

		@Override
		public Builder getThis() {
			return this;
		}

		public Builder professional(final boolean professional) {
			this.professional = professional;
			return getThis();
		}

		@Override
		public Builder countryIsoCode(final String countryIsoCode) {
			this.countryIsoCode = countryIsoCode;
			return getThis();
		}

		public Builder proofOfAddress(final KYCProofOfAddressEnum proofOfAddress) {
			this.proofOfAddress = proofOfAddress;
			return getThis();
		}

		public Builder proofOfBusiness(final KYCProofOfBusinessEnum proofOfBusiness) {
			this.proofOfBusiness = proofOfBusiness;
			return getThis();
		}

		public Builder userToken(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklStringCustomFieldValue(fields, HYPERWALLET_USER_TOKEN_FIELD)
					.ifPresent(retrievedToken -> userToken = retrievedToken);
			return this;
		}

		public Builder requiresKYC(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklBooleanCustomFieldValue(fields, HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD)
					.ifPresent(requiresKYCValue -> requiresKYC = Boolean.parseBoolean(requiresKYCValue));

			return this;
		}

		@Override
		public Builder documents(final List<KYCDocumentModel> documents) {
			this.documents = Stream.ofNullable(documents).flatMap(Collection::stream).filter(Objects::nonNull)
					.map(document -> document.toBuilder().build()).collect(Collectors.toList());
			return this;
		}

		public Builder proofOfIdentity(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklSingleValueListCustomFieldValue(fields, HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_FIELD)
					.ifPresent(retrievedProofOfIdentity -> proofOfIdentity = EnumUtils
							.getEnum(KYCProofOfIdentityEnum.class, retrievedProofOfIdentity));

			return this;
		}

		public Builder proofOfAddress(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklSingleValueListCustomFieldValue(fields, HYPERWALLET_KYC_IND_PROOF_OF_ADDRESS_FIELD)
					.ifPresent(retrievedProofOfAddress -> proofOfAddress = EnumUtils
							.getEnum(KYCProofOfAddressEnum.class, retrievedProofOfAddress));

			return this;
		}

		public Builder countryIsoCode(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklStringCustomFieldValue(fields, HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_COUNTRY_ISOCODE)
					.ifPresent(retrievedCountryIsocode -> countryIsoCode = retrievedCountryIsocode);
			return this;
		}

		public Builder proofOfBusiness(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklSingleValueListCustomFieldValue(fields, HYPERWALLET_KYC_PROF_PROOF_OF_BUSINESS_FIELD)
					.ifPresent(retrievedProofOfBusiness -> proofOfBusiness = EnumUtils
							.getEnum(KYCProofOfBusinessEnum.class, retrievedProofOfBusiness));

			return this;
		}

		@Override
		public Builder miraklShopDocuments(final List<MiraklShopDocument> miraklShopDocuments) {
			this.miraklShopDocuments = Stream.ofNullable(miraklShopDocuments).flatMap(Collection::stream)
					.map(miraklShopDocument -> {
						final MiraklShopDocument miraklShopDocumentCopy = new MiraklShopDocument();
						miraklShopDocumentCopy.setId(miraklShopDocument.getId());
						miraklShopDocumentCopy.setTypeCode(miraklShopDocument.getTypeCode());
						miraklShopDocumentCopy.setDateDeleted(miraklShopDocument.getDateDeleted());
						miraklShopDocumentCopy.setDateUploaded(miraklShopDocument.getDateUploaded());
						miraklShopDocumentCopy.setFileName(miraklShopDocument.getFileName());
						miraklShopDocumentCopy.setShopId(miraklShopDocument.getShopId());

						return miraklShopDocumentCopy;
					}).collect(Collectors.toList());

			return this;
		}

		@Override
		public KYCDocumentSellerInfoModel build() {
			return new KYCDocumentSellerInfoModel(this);
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

	}

}
