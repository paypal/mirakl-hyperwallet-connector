package com.paypal.kyc.documentextractioncommons.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Creates an object of KYCDocumentModel
 */

@Slf4j
@Getter
@Builder
public class KYCDocumentModel implements Serializable {

	private final String documentFieldName;

	private final File file;

	public KYCDocumentModelBuilder toBuilder() {
		//@formatter:off
		return KYCDocumentModel.builder()
				.documentFieldName(documentFieldName)
				.file(file);
		//@formatter:on
	}

	public KYCDocumentSideEnum getDocumentSide() {
		return KYCDocumentSideEnum.getDocumentSideByFieldName(documentFieldName);
	}

	public KYCDocumentCategoryEnum getDocumentCategory() {
		return KYCDocumentCategoryEnum.getDocumentCategoryForField(documentFieldName);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final KYCDocumentModel that = (KYCDocumentModel) o;
		return EqualsBuilder.reflectionEquals(this, that, List.of());
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
