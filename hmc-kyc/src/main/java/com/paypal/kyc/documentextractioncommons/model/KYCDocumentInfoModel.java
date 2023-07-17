package com.paypal.kyc.documentextractioncommons.model;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import static com.paypal.kyc.documentextractioncommons.model.KYCConstants.HW_PROGRAM;

/**
 * Model where all KYC Models must extend (except KYCDocumentModel)
 */
@Getter
public class KYCDocumentInfoModel implements Serializable {

	protected final String userToken;

	protected final String clientUserId;

	protected final boolean requiresKYC;

	protected final String countryIsoCode;

	protected final KYCProofOfIdentityEnum proofOfIdentity;

	protected final transient List<MiraklShopDocument> miraklShopDocuments;

	protected final String hyperwalletProgram;

	private final List<KYCDocumentModel> documents;

	protected KYCDocumentInfoModel(final Builder<?> builder) {
		userToken = builder.userToken;
		clientUserId = builder.clientUserId;
		countryIsoCode = builder.countryIsoCode;
		requiresKYC = builder.requiresKYC;
		proofOfIdentity = builder.proofOfIdentity;
		miraklShopDocuments = builder.miraklShopDocuments;
		documents = builder.documents;
		hyperwalletProgram = builder.hyperwalletProgram;
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

	public boolean existsDocumentInMirakl() {
		return false;
	}

	public boolean areDocumentsFilled() {
		return false;
	}

	public String getDocumentTracingIdentifier() {
		return StringUtils.EMPTY;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof KYCDocumentInfoModel)) {
			return false;
		}
		final KYCDocumentInfoModel that = (KYCDocumentInfoModel) o;

		return EqualsBuilder.reflectionEquals(this, that, "miraklShopDocuments", "documents")
				&& CollectionUtils.isEqualCollection(Optional.ofNullable(getMiraklShopDocuments()).orElse(List.of()),
						Optional.ofNullable(that.getMiraklShopDocuments()).orElse(List.of()))
				&& CollectionUtils.isEqualCollection(Optional.ofNullable(getDocuments()).orElse(List.of()),
						Optional.ofNullable(that.getDocuments()).orElse(List.of()));
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public abstract static class Builder<T extends Builder<T>> {

		protected String userToken;

		protected String clientUserId;

		protected boolean requiresKYC;

		protected String countryIsoCode;

		protected KYCProofOfIdentityEnum proofOfIdentity;

		protected List<MiraklShopDocument> miraklShopDocuments;

		protected List<KYCDocumentModel> documents;

		protected String hyperwalletProgram;

		public abstract T getThis();

		public T userToken(final String userToken) {
			this.userToken = userToken;
			return getThis();
		}

		public T clientUserId(final String clientUserId) {
			this.clientUserId = clientUserId;
			return getThis();
		}

		public T requiresKYC(final boolean requiresKYC) {
			this.requiresKYC = requiresKYC;
			return getThis();
		}

		public T proofOfIdentity(final KYCProofOfIdentityEnum proofOfIdentity) {
			this.proofOfIdentity = proofOfIdentity;
			return getThis();
		}

		public T miraklShopDocuments(final List<MiraklShopDocument> miraklShopDocuments) {
			this.miraklShopDocuments = miraklShopDocuments;
			return getThis();
		}

		public T documents(final List<KYCDocumentModel> documents) {
			this.documents = documents;
			return getThis();
		}

		public T countryIsoCode(final String countryIsoCode) {
			this.countryIsoCode = countryIsoCode;
			return getThis();
		}

		public T hyperwalletProgram(final String hyperwalletProgram) {
			this.hyperwalletProgram = hyperwalletProgram;
			return getThis();
		}

		public T hyperwalletProgram(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklSingleValueListCustomFieldValue(fields, HW_PROGRAM)
					.ifPresent(retrievedHyperwalletProgram -> this.hyperwalletProgram = retrievedHyperwalletProgram);

			return getThis();
		}

		public KYCDocumentInfoModel build() {
			return new KYCDocumentInfoModel(this);
		}

		protected Optional<String> getMiraklSingleValueListCustomFieldValue(
				final List<MiraklAdditionalFieldValue> fields, final String customFieldCode) {
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
