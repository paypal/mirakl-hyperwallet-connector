package com.paypal.kyc.model;

import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

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

	private final List<KYCDocumentModel> documents;

	protected final boolean sentToHyperwallet;

	protected final String hyperwalletProgram;

	protected KYCDocumentInfoModel(final Builder<?> builder) {
		userToken = builder.userToken;
		clientUserId = builder.clientUserId;
		countryIsoCode = builder.countryIsoCode;
		requiresKYC = builder.requiresKYC;
		proofOfIdentity = builder.proofOfIdentity;
		miraklShopDocuments = builder.miraklShopDocuments;
		sentToHyperwallet = builder.sentToHyperwallet;
		documents = builder.documents;
		hyperwalletProgram = builder.hyperwalletProgram;
	}

	public boolean existsDocumentInMirakl() {
		return false;
	}

	public boolean areDocumentsFilled() {
		return false;
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

		protected String userToken;

		protected String clientUserId;

		protected boolean requiresKYC;

		protected String countryIsoCode;

		protected KYCProofOfIdentityEnum proofOfIdentity;

		protected List<MiraklShopDocument> miraklShopDocuments;

		protected List<KYCDocumentModel> documents;

		protected boolean sentToHyperwallet;

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

		public T sentToHyperwallet(final boolean sentToHyperwallet) {
			this.sentToHyperwallet = sentToHyperwallet;
			return getThis();
		}

		public T hyperwalletProgram(final String hyperwalletProgram) {
			this.hyperwalletProgram = hyperwalletProgram;
			return getThis();
		}

		public KYCDocumentInfoModel build() {
			return new KYCDocumentInfoModel(this);
		}

	}

}
