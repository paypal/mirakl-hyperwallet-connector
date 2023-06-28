package com.paypal.kyc.incomingnotifications.services.converters;

import com.paypal.kyc.documentextractioncommons.model.KYCDocumentCategoryEnum;
import com.paypal.kyc.incomingnotifications.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HyperWalletObjectToKYCDocumentNotificationModelsConverterTest {

	private static final String DOCUMENTS = "documents";

	private static final String CREATED_ON_DATE = "2021-08-17T19:57:29";

	@InjectMocks
	private HyperWalletObjectToKYCDocumentNotificationModelsConverter testObj;

	@Test
	void convert_shouldReturnEmptyList_whenArgumentPassedItIsNotANotification() {
		final List<KYCDocumentNotificationModel> result = testObj.convert(new Object());

		assertThat(result).isEmpty();
	}

	@Test
	void convert_shouldReturnAListOfDocuments_forAllTypesOfBusinessDocuments() {
		final List<KYCDocumentNotificationModel> result = testObj
				.convert(createNotificationWithProofBusinessInvalidDocuments());

		assertThat(result).hasSize(3);
	}

	@Test
	void convert_shouldReturnAListOfDocuments_whenArgumentPassedHasDocuments() {
		final List<KYCDocumentNotificationModel> result = testObj.convert(createNotificationWithValidDocuments());

		assertThat(result).hasSize(2);
	}

	@Test
	void convert_shouldReturnOneValidDocument_whenArgumentPassedHasOneDocument() {
		final List<KYCDocumentNotificationModel> result = testObj.convert(createNotificationWithValidDocuments());

		assertThat(result).isNotEmpty();

		final KYCDocumentNotificationModel kycDocumentNotificationModel = result.get(0);
		assertThat(kycDocumentNotificationModel.getCreatedOn()).isEqualTo(CREATED_ON_DATE);
		assertThat(kycDocumentNotificationModel.getDocumentCategory()).isEqualTo(KYCDocumentCategoryEnum.ADDRESS);
		assertThat(kycDocumentNotificationModel.getDocumentStatus()).isEqualTo(KYCDocumentStatusEnum.VALID);
		assertThat(kycDocumentNotificationModel.getDocumentType()).isEqualTo(KYCDocumentTypeEnum.BANK_STATEMENT);
	}

	@Test
	void convert_shouldReturnInValidDocumentsWithReasons_whenArgumentPassedHasInvalidDocument() {
		final List<KYCDocumentNotificationModel> result = testObj.convert(createNotificationWithInvalidDocuments());

		assertThat(result).isNotEmpty();

		final KYCDocumentNotificationModel kycDocumentNotificationModel = result.get(0);
		assertThat(kycDocumentNotificationModel.getDocumentStatus()).isEqualTo(KYCDocumentStatusEnum.INVALID);

		final List<KYCDocumentRejectedReasonEnum> documentRejectedReasons = kycDocumentNotificationModel
				.getDocumentRejectedReasons();
		assertThat(documentRejectedReasons).hasSize(2).contains(
				KYCDocumentRejectedReasonEnum.DOCUMENT_CORRECTION_REQUIRED,
				KYCDocumentRejectedReasonEnum.DOCUMENT_NOT_COMPLETE);
	}

	private Map<String, Object> createNotificationWithValidDocuments() {
		final Map<String, Object> document1 = createDocument(KYCDocumentStatusEnum.VALID.name(),
				KYCDocumentTypeEnum.BANK_STATEMENT.name(), CREATED_ON_DATE, KYCDocumentCategoryEnum.ADDRESS.name(),
				null);
		final Map<String, Object> document2 = createDocument(KYCDocumentStatusEnum.VALID.name(),
				KYCDocumentTypeEnum.CREDIT_CARD_STATEMENT.name(), CREATED_ON_DATE,
				KYCDocumentCategoryEnum.BUSINESS.name(), null);
		return Map.of(DOCUMENTS, List.of(document1, document2));
	}

	private Map<String, Object> createNotificationWithInvalidDocuments() {
		final Map<String, Object> document1 = createDocument(KYCDocumentStatusEnum.INVALID.name(),
				KYCDocumentTypeEnum.BANK_STATEMENT.name(), CREATED_ON_DATE, KYCDocumentCategoryEnum.ADDRESS.name(),
				createRejectedReasons());
		final Map<String, Object> document2 = createDocument(KYCDocumentStatusEnum.INVALID.name(),
				KYCDocumentTypeEnum.CREDIT_CARD_STATEMENT.name(), CREATED_ON_DATE,
				KYCDocumentCategoryEnum.BUSINESS.name(), createRejectedReasons());
		return Map.of(DOCUMENTS, List.of(document1, document2));
	}

	private Map<String, Object> createNotificationWithProofBusinessInvalidDocuments() {
		final Map<String, Object> document1 = createDocument(KYCDocumentStatusEnum.INVALID.name(),
				KYCDocumentTypeEnum.INCORPORATION.name(), CREATED_ON_DATE, KYCDocumentCategoryEnum.BUSINESS.name(),
				createRejectedReasons());
		final Map<String, Object> document2 = createDocument(KYCDocumentStatusEnum.INVALID.name(),
				KYCDocumentTypeEnum.BUSINESS_REGISTRATION.name(), CREATED_ON_DATE,
				KYCDocumentCategoryEnum.BUSINESS.name(), createRejectedReasons());
		final Map<String, Object> document3 = createDocument(KYCDocumentStatusEnum.INVALID.name(),
				KYCDocumentTypeEnum.OPERATING_AGREEMENT.name(), CREATED_ON_DATE,
				KYCDocumentCategoryEnum.BUSINESS.name(), createRejectedReasons());
		return Map.of(DOCUMENTS, List.of(document1, document2, document3));
	}

	private Map<String, Object> createDocument(final String status, final String type, final String createdOn,
			final String category, final List<Map<String, String>> reasons) {
		final Map<String, Object> document = new HashMap<>(
				Map.of("status", status, "type", type, "createdOn", createdOn, "category", category));
		Optional.ofNullable(reasons).ifPresent(notNullReasons -> document.put("reasons", notNullReasons));
		return document;
	}

	private List<Map<String, String>> createRejectedReasons() {
		final Map<String, String> reason1 = Map.of("name", "DOCUMENT_CORRECTION_REQUIRED", "description",
				"Document requires correction.");
		final Map<String, String> reason2 = Map.of("name", "DOCUMENT_NOT_COMPLETE", "description",
				"Document is incomplet.");
		return List.of(reason1, reason2);
	}

}
