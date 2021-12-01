package com.paypal.kyc.model;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue.*;
import static com.paypal.kyc.model.KYCConstants.HYPERWALLET_USER_TOKEN_FIELD;
import static com.paypal.kyc.model.KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK;
import static com.paypal.kyc.model.KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class KYCDocumentBusinessStakeHolderInfoModelTest {

	private static final Integer VALID_BUSINESS_STAKE_HOLDER_NUMBER = 1;

	private static final Integer INVALID_BUSINESS_STAKE_HOLDER_NUMBER = 10;

	private static final String TOKEN = "hw-stakeholder-token-1";

	private static final String PROOF_OF_IDENTITY_TYPE = "hw-stakeholder-proof-identity-type-1";

	private static final String REQUIRES_PROOF_IDENTITY = "hw-stakeholder-req-proof-identity-1";

	private static final String REQUIRES_PROOF_IDENTITY_COUNTRY_ISOCODE = "hw-stakeholder-proof-identity-ctry-1";

	private static final String PROOF_OF_AUTHORIZATION = "hw-bsh-letter-authorization";

	private static final String SHOP_ID_2000 = "2000";

	private static final String SHOP_ID_2001 = "2001";

	private static final String USER_TOKEN = "userToken";

	private static final String BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE = "Business Stake Holder number 10 incorrect. System only allows Business stake holder attributes from 1 to 5";

	private static final String BUSINESS_STAKEHOLDER_TOKEN = "businessStakeholderToken";

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.WARN)
			.recordForType(KYCDocumentBusinessStakeHolderInfoModel.class);

	@Mock
	private KYCDocumentModel kycDocumentModelMock;

	@Test
	void token_whenMiraklTokenBusinessStakeHolderFieldValueHasAValue_shouldSetToken() {
		final MiraklStringAdditionalFieldValue tokenBusinessStakeHolderField = new MiraklStringAdditionalFieldValue();
		tokenBusinessStakeHolderField.setCode(TOKEN);
		tokenBusinessStakeHolderField.setValue("token");
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel result = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.token(List.of(tokenBusinessStakeHolderField), VALID_BUSINESS_STAKE_HOLDER_NUMBER)
				.build();
		//@formatter:on

		assertThat(result.getToken()).isEqualTo("token");
	}

	@Test
	void proofOfIdentity_whenMiraklProofOfIdentityBusinessStakeHolderFieldValueHasAValue_shouldSetProofOfIdentity() {
		final MiraklValueListAdditionalFieldValue tokenBusinessStakeHolderField = new MiraklValueListAdditionalFieldValue();
		tokenBusinessStakeHolderField.setCode(PROOF_OF_IDENTITY_TYPE);
		tokenBusinessStakeHolderField.setValue("GOVERNMENT_ID");
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel result = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.proofOfIdentity(List.of(tokenBusinessStakeHolderField), VALID_BUSINESS_STAKE_HOLDER_NUMBER)
				.build();
		//@formatter:on

		assertThat(result.getProofOfIdentity()).isEqualTo(KYCProofOfIdentityEnum.GOVERNMENT_ID);
	}

	@Test
	void requiresKYC_whenMiraklRequiresKYCBusinessStakeHolderFieldValueHasValue_shouldSetRequiresKYC() {
		final MiraklBooleanAdditionalFieldValue tokenBusinessStakeHolderField = new MiraklBooleanAdditionalFieldValue();
		tokenBusinessStakeHolderField.setCode(REQUIRES_PROOF_IDENTITY);
		tokenBusinessStakeHolderField.setValue("true");
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel result = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.requiresKYC(List.of(tokenBusinessStakeHolderField), VALID_BUSINESS_STAKE_HOLDER_NUMBER)
				.build();
		//@formatter:on

		assertThat(result.isRequiresKYC()).isTrue();
	}

	@Test
	void countryIsoCode_whenMiraklRequiresKYCBusinessStakeHolderFieldValueHasValue_shouldSetCountryIsoCode() {
		final MiraklStringAdditionalFieldValue tokenBusinessStakeHolderField = new MiraklStringAdditionalFieldValue();
		tokenBusinessStakeHolderField.setCode(REQUIRES_PROOF_IDENTITY_COUNTRY_ISOCODE);
		tokenBusinessStakeHolderField.setValue("ES");
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel result = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.countryIsoCode(List.of(tokenBusinessStakeHolderField), VALID_BUSINESS_STAKE_HOLDER_NUMBER)
				.build();
		//@formatter:on

		assertThat(result.getCountryIsoCode()).isEqualTo("ES");
	}

	@Test
	void containsIdentityDocuments_shouldReturnTrueWhenAllIdentityDocumentsAreFilled() {
		final KYCDocumentModel nationalIdentityCardFront = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_FRONT).build();
		final KYCDocumentModel nationalIdentityCardBack = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_BACK).build();
		final KYCDocumentBusinessStakeHolderInfoModel testObj = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.proofOfIdentity(
						List.of(new MiraklValueListAdditionalFieldValue(PROOF_OF_IDENTITY_TYPE, "GOVERNMENT_ID")),
						VALID_BUSINESS_STAKE_HOLDER_NUMBER)
				.documents(List.of(nationalIdentityCardFront, nationalIdentityCardBack)).build();

		final boolean result = testObj.isIdentityDocumentsFilled();

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnTrueWhenBothAreEquals() {
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelOne = createKYCDocumentBusinessStakeholderInfoModelObject();
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelTwo = createKYCDocumentBusinessStakeholderInfoModelObject();

		final boolean result = kycDocumentBusinessStakeHolderInfoModelOne
				.equals(kycDocumentBusinessStakeHolderInfoModelTwo);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenBothAreNotEquals() {
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelOne = createKYCDocumentBusinessStakeholderInfoModelObject();
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelTwo = createAnotherKYCDocumentBusinessStakeholderInfoModelObject();

		final boolean result = kycDocumentBusinessStakeHolderInfoModelOne
				.equals(kycDocumentBusinessStakeHolderInfoModelTwo);

		assertThat(result).isFalse();
	}

	@Test
	void equals_shouldReturnTrueWhenSameObjectIsCompared() {
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelOne = createKYCDocumentBusinessStakeholderInfoModelObject();

		final boolean result = kycDocumentBusinessStakeHolderInfoModelOne
				.equals(kycDocumentBusinessStakeHolderInfoModelOne);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenComparedWithAnotherInstanceObject() {
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelOne = createKYCDocumentBusinessStakeholderInfoModelObject();

		final Object o = new Object();

		final boolean result = kycDocumentBusinessStakeHolderInfoModelOne.equals(o);

		assertThat(result).isFalse();
	}

	@Test
	void getIdentityDocuments_shouldReturnDocumentsWithIdentificationTypeAndFrontSide() {
		//@formatter:off
		final KYCDocumentModel documentForPassport = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT)
				.build();
		final KYCDocumentModel unnecessaryDocument = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_BACK)
				.build();
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
				.documents(List.of(documentForPassport, unnecessaryDocument))
				.build();
		//@formatter:on

		final List<KYCDocumentModel> result = kycDocumentBusinessStakeholderInfoModel.getIdentityDocuments();

		assertThat(result).hasSize(1).containsExactlyInAnyOrder(documentForPassport);
	}

	@Test
	void getIdentityDocuments_shouldReturnEmptyListWhenNoDocumentIsFilled() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
				.build();
		//@formatter:on
		final List<KYCDocumentModel> result = kycDocumentBusinessStakeholderInfoModel.getIdentityDocuments();

		assertThat(result).isEmpty();
	}

	@Test
	void isIdentityDocumentsFilled_shouldReturnTrueWhenProofOfIdentityAndDocumentsAreFilled() {
		//@formatter:off
		final KYCDocumentModel documentForPassport = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT)
				.build();
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
				.documents(List.of(documentForPassport))
				.build();
		//@formatter:on

		final boolean result = kycDocumentBusinessStakeholderInfoModel.isIdentityDocumentsFilled();

		assertThat(result).isTrue();
	}

	@Test
	void isIdentityDocumentsFilled_shouldReturnFalseWhenProofOfIdentityIsEmptyAndDocumentsAreFilled() {
		//@formatter:off
		final KYCDocumentModel documentForPassport = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT)
				.build();
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.documents(List.of(documentForPassport))
				.build();
		//@formatter:on

		final boolean result = kycDocumentBusinessStakeholderInfoModel.isIdentityDocumentsFilled();

		assertThat(result).isFalse();
	}

	@Test
	void isEmpty_shouldReturnTrueWhenBusinessStakeholderTokenAndProofOfIdentityAndDocumentsAreNull() {
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder().build();

		final boolean result = kycDocumentBusinessStakeholderInfoModel.isEmpty();

		assertThat(result).isTrue();
	}

	@Test
	void isEmpty_shouldReturnFalseWhenBusinessStakeholderTokenIsFilledAndProofOfIdentityAndDocumentsAreNull() {
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder().token(BUSINESS_STAKEHOLDER_TOKEN).build();

		final boolean result = kycDocumentBusinessStakeholderInfoModel.isEmpty();

		assertThat(result).isFalse();
	}

	@Test
	void isEmpty_shouldReturnFalseWhenBusinessStakeholderTokenIsEmptyAndProofOfIdentityIsFilledAndDocumentsIsEmpty() {
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder().proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID).build();

		final boolean result = kycDocumentBusinessStakeholderInfoModel.isEmpty();

		assertThat(result).isFalse();
	}

	@Test
	void isEmpty_shouldReturnFalseWhenBusinessStakeholderTokenIsEmptyAndProofOfIdentityIsEmptyAndDocumentsIsFilled() {
		final KYCDocumentModel document = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_AUTHORIZATION).build();

		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder().documents(List.of(document)).build();

		final boolean result = kycDocumentBusinessStakeholderInfoModel.isEmpty();

		assertThat(result).isFalse();
	}

	@Test
	void hasSelectedDocumentsControlFieldsInBusinessStakeholder_shouldReturnTrueWhenProofOfIdentityIsFilledAndRequiresKYC() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder()
				.proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
				.requiresKYC(Boolean.TRUE)
				.build();
		//@formatter:on

		final boolean result = kycDocumentBusinessStakeholderInfoModel
				.hasSelectedDocumentsControlFieldsInBusinessStakeholder();

		assertThat(result).isTrue();
	}

	@Test
	void hasSelectedDocumentsControlFieldsInBusinessStakeholder_shouldReturnFalseWhenProofOfIdentityIsEmptyAndRequiresKYC() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder()
				.requiresKYC(Boolean.TRUE)
				.build();
		//@formatter:on
		final boolean result = kycDocumentBusinessStakeholderInfoModel
				.hasSelectedDocumentsControlFieldsInBusinessStakeholder();

		assertThat(result).isFalse();
	}

	@Test
	void hasSelectedDocumentsControlFieldsInBusinessStakeholder_shouldReturnTrueWhenRequiresKYCIsFalse() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder()
				.requiresKYC(Boolean.FALSE)
				.build();
		//@formatter:on
		final boolean result = kycDocumentBusinessStakeholderInfoModel
				.hasSelectedDocumentsControlFieldsInBusinessStakeholder();

		assertThat(result).isTrue();
	}

	@Test
	void isIdentityDocumentsFilled_shouldReturnFalseWhenProofOfIdentityIsFilledAndDocumentsAreEmpty() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
				.build();
		//@formatter:on

		final boolean result = kycDocumentBusinessStakeholderInfoModel.isIdentityDocumentsFilled();

		assertThat(result).isFalse();
	}

	@Test
	void token_shouldLogMessageWhenBusinessStakeholderNumberIsInvalid() {
		//@formatter:off
		final MiraklStringAdditionalFieldValue tokenBusinessStakeHolderField = new MiraklStringAdditionalFieldValue();
		tokenBusinessStakeHolderField.setCode(TOKEN);
		tokenBusinessStakeHolderField.setValue("token");
		//@formatter:off
		KYCDocumentBusinessStakeHolderInfoModel.builder()
				.token(List.of(tokenBusinessStakeHolderField), INVALID_BUSINESS_STAKE_HOLDER_NUMBER)
				.build();
		//@formatter:on
		assertThat(logTrackerStub.contains(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE)).isTrue();
	}

	@Test
	void requiresKYC_shouldLogMessageWhenBusinessStakeholderNumberIsInvalid() {
		final MiraklBooleanAdditionalFieldValue tokenBusinessStakeHolderField = new MiraklBooleanAdditionalFieldValue();
		tokenBusinessStakeHolderField.setCode(REQUIRES_PROOF_IDENTITY);
		tokenBusinessStakeHolderField.setValue("true");
		//@formatter:off
		KYCDocumentBusinessStakeHolderInfoModel.builder()
				.requiresKYC(List.of(tokenBusinessStakeHolderField), INVALID_BUSINESS_STAKE_HOLDER_NUMBER)
				.build();
		//@formatter:on
		assertThat(logTrackerStub.contains(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE)).isTrue();
	}

	@Test
	void proofOfIdentity_shouldLogMessageWhenBusinessStakeholderNumberIsInvalid() {
		final MiraklValueListAdditionalFieldValue proofOfIdentityBusinessStakeHolderField = new MiraklValueListAdditionalFieldValue();
		proofOfIdentityBusinessStakeHolderField.setCode(PROOF_OF_IDENTITY_TYPE);
		proofOfIdentityBusinessStakeHolderField.setValue("GOVERNMENT_ID");
		//@formatter:off
		KYCDocumentBusinessStakeHolderInfoModel.builder()
				.proofOfIdentity(List.of(proofOfIdentityBusinessStakeHolderField), INVALID_BUSINESS_STAKE_HOLDER_NUMBER)
				.build();
		//@formatter:on
		assertThat(logTrackerStub.contains(BUSINESS_STAKE_HOLDER_OUT_OF_INBOUND_MESSAGE)).isTrue();
	}

	@Test
	void getLetterOfAuthorizationDocument_shouldReturnDocumentWhenDocumentIsFilledAndBusinessStakeholderIsContactAndRequiresLetterOfAuthorization() {
		//@formatter:off
		final KYCDocumentModel documentForLetterOfAuthorization = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_AUTHORIZATION)
				.build();
		final KYCDocumentModel unnecessaryDocument = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_FRONT)
				.build();
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.contact(Boolean.TRUE)
				.requiresLetterOfAuthorization(Boolean.TRUE)
				.documents(List.of(documentForLetterOfAuthorization, unnecessaryDocument))
				.build();
		//@formatter:on

		final List<KYCDocumentModel> result = kycDocumentBusinessStakeholderInfoModel
				.getLetterOfAuthorizationDocument();

		assertThat(result).hasSize(1).containsExactlyInAnyOrder(documentForLetterOfAuthorization);
	}

	@Test
	void getLetterOfAuthorizationDocument_shouldEmptyListWhenDocumentIsNotFilledAndBusinessStakeholderIsContactAndRequiresLetterOfAuthorization() {
		//@formatter:off
		final KYCDocumentModel unnecessaryDocument = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_FRONT)
				.build();
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.contact(Boolean.TRUE)
				.requiresLetterOfAuthorization(Boolean.TRUE)
				.documents(List.of(unnecessaryDocument))
				.build();
		//@formatter:on

		final List<KYCDocumentModel> result = kycDocumentBusinessStakeholderInfoModel
				.getLetterOfAuthorizationDocument();

		assertThat(result).isEmpty();
	}

	@Test
	void getLetterOfAuthorizationDocument_shouldReturnEmptyListWhenDocumentIsFilledAndBusinessStakeholderIsNotContactAndRequiresLetterOfAuthorization() {
		//@formatter:off
		final KYCDocumentModel documentForLetterOfAuthorization = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_AUTHORIZATION)
				.build();
		final KYCDocumentModel unnecessaryDocument = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_FRONT)
				.build();
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.contact(Boolean.FALSE)
				.requiresLetterOfAuthorization(Boolean.TRUE)
				.documents(List.of(documentForLetterOfAuthorization, unnecessaryDocument))
				.build();
		//@formatter:on

		final List<KYCDocumentModel> result = kycDocumentBusinessStakeholderInfoModel
				.getLetterOfAuthorizationDocument();

		assertThat(result).isEmpty();
	}

	@Test
	void getLetterOfAuthorizationDocument_shouldReturnEmptyListWhenDocumentIsFilledAndBusinessStakeholderIsContactAndRequiresLetterOfAuthorizationIsFalse() {
		//@formatter:off
		final KYCDocumentModel documentForLetterOfAuthorization = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_AUTHORIZATION)
				.build();
		final KYCDocumentModel unnecessaryDocument = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_FRONT)
				.build();
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.contact(Boolean.TRUE)
				.requiresLetterOfAuthorization(Boolean.FALSE)
				.documents(List.of(documentForLetterOfAuthorization, unnecessaryDocument))
				.build();
		//@formatter:on

		final List<KYCDocumentModel> result = kycDocumentBusinessStakeholderInfoModel
				.getLetterOfAuthorizationDocument();

		assertThat(result).isEmpty();
	}

	@Test
	void existsLetterOfAuthorizationDocumentInMirakl_shouldReturnTrueWhenMiraklContainsProofOfAuthorizationAndRequiresLetterOfAuthorizationIsTrue() {
		//@formatter:off
		final MiraklShopDocument documentForLetterOfAuthorization = new MiraklShopDocument();
		documentForLetterOfAuthorization.setTypeCode(PROOF_OF_AUTHORIZATION);

		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.contact(Boolean.TRUE)
				.miraklShopDocuments(List.of(documentForLetterOfAuthorization))
				.requiresLetterOfAuthorization(Boolean.TRUE)
				.build();
		//@formatter:on

		final boolean result = kycDocumentBusinessStakeholderInfoModel.existsLetterOfAuthorizationDocumentInMirakl();

		assertThat(result).isTrue();
	}

	@Test
	void existsLetterOfAuthorizationDocumentInMirakl_shouldReturnTrueWhenMiraklNotContainsProofOfAuthorizationAndRequiresLetterOfAuthorizationIsTrue() {
		//@formatter:off
		final MiraklShopDocument documentDifferentFromLetterOfAuthorization = new MiraklShopDocument();
		documentDifferentFromLetterOfAuthorization.setTypeCode(PROOF_OF_IDENTITY_TYPE);

		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.contact(Boolean.TRUE)
				.miraklShopDocuments(List.of(documentDifferentFromLetterOfAuthorization))
				.requiresLetterOfAuthorization(Boolean.TRUE)
				.build();
		//@formatter:on

		final boolean result = kycDocumentBusinessStakeholderInfoModel.existsLetterOfAuthorizationDocumentInMirakl();

		assertThat(result).isFalse();
	}

	@Test
	void existsLetterOfAuthorizationDocumentInMirakl_shouldReturnTrueWhenMiraklContainsProofOfAuthorizationAndRequiresLetterOfAuthorizationIsFalse() {
		final MiraklShopDocument documentForLetterOfAuthorization = new MiraklShopDocument();
		documentForLetterOfAuthorization.setTypeCode(PROOF_OF_AUTHORIZATION);

		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.contact(Boolean.TRUE)
				.miraklShopDocuments(List.of(documentForLetterOfAuthorization))
				.requiresLetterOfAuthorization(Boolean.FALSE)
				.build();
		//@formatter:on

		final boolean result = kycDocumentBusinessStakeholderInfoModel.existsLetterOfAuthorizationDocumentInMirakl();

		assertThat(result).isFalse();
	}

	@Test
	void areDocumentsFilled_shouldReturnTrueWhenRequiresKYCIsFalse() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = spy(KYCDocumentBusinessStakeHolderInfoModel.builder()
				.requiresKYC(Boolean.FALSE)
				.build());
		//@formatter:on

		final boolean result = kycDocumentBusinessStakeholderInfoModel.areDocumentsFilled();

		assertThat(result).isTrue();
	}

	@Test
	void areDocumentsFilled_shouldReturnTrueWhenIsIdentityDocumentsFilledIsTrueAndRequiresKYCIsTrue() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = spy(KYCDocumentBusinessStakeHolderInfoModel.builder()
				.requiresKYC(Boolean.TRUE)
				.build());
		//@formatter:on

		doReturn(Boolean.TRUE).when(kycDocumentBusinessStakeholderInfoModel).isIdentityDocumentsFilled();

		final boolean result = kycDocumentBusinessStakeholderInfoModel.areDocumentsFilled();

		assertThat(result).isTrue();
	}

	@Test
	void areDocumentsFilled_shouldReturnFalseWhenIsIdentityDocumentsFilledIsFalseAndRequiresKYCIsTrue() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = spy(KYCDocumentBusinessStakeHolderInfoModel.builder()
				.requiresKYC(Boolean.TRUE)
				.build());
		//@formatter:on

		doReturn(Boolean.FALSE).when(kycDocumentBusinessStakeholderInfoModel).isIdentityDocumentsFilled();

		final boolean result = kycDocumentBusinessStakeholderInfoModel.areDocumentsFilled();

		assertThat(result).isFalse();
	}

	@Test
	void areDocumentsFilled_shouldReturnTrueWhenRequiresKYCIsFalseAndRequiresLetterOfAuthorizationIsFalse() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.requiresKYC(Boolean.FALSE)
				.requiresLetterOfAuthorization(Boolean.FALSE)
				.build();
		//@formatter:on

		final boolean result = kycDocumentBusinessStakeholderInfoModel.areDocumentsFilled();

		assertThat(result).isTrue();
	}

	@Test
	void areDocumentsFilled_shouldReturnTrueWhenRequiresKYCIsFalseAndRequiresLetterOfAuthorizationIsTrueAndGetLetterOfAuthorizationDocumentIsNotEmpty() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = spy(KYCDocumentBusinessStakeHolderInfoModel.builder()
				.requiresKYC(Boolean.FALSE)
				.requiresLetterOfAuthorization(Boolean.TRUE)
				.build());
		//@formatter:on

		doReturn(List.of(kycDocumentModelMock)).when(kycDocumentBusinessStakeholderInfoModel)
				.getLetterOfAuthorizationDocument();

		final boolean result = kycDocumentBusinessStakeholderInfoModel.areDocumentsFilled();

		assertThat(result).isTrue();
	}

	@Test
	void areDocumentsFilled_shouldReturnFalseWhenRequiresKYCIsFalseAndRequiresLetterOfAuthorizationIsTrueAndGetLetterOfAuthorizationDocumentIsEmpty() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModel = spy(KYCDocumentBusinessStakeHolderInfoModel.builder()
				.requiresKYC(Boolean.FALSE)
				.requiresLetterOfAuthorization(Boolean.TRUE)
				.build());
		//@formatter:on

		doReturn(List.of()).when(kycDocumentBusinessStakeholderInfoModel).getLetterOfAuthorizationDocument();

		final boolean result = kycDocumentBusinessStakeholderInfoModel.areDocumentsFilled();

		assertThat(result).isFalse();
	}

	private KYCDocumentBusinessStakeHolderInfoModel createKYCDocumentBusinessStakeholderInfoModelObject() {
		final List<MiraklAdditionalFieldValue> additionalValues = List.of(
				new MiraklBooleanAdditionalFieldValue(REQUIRES_PROOF_IDENTITY, Boolean.TRUE.toString()),
				new MiraklStringAdditionalFieldValue(HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN),
				new MiraklStringAdditionalFieldValue(TOKEN, BUSINESS_STAKEHOLDER_TOKEN),
				new MiraklValueListAdditionalFieldValue(PROOF_OF_IDENTITY_TYPE, "GOVERNMENT_ID"));

		//@formatter:off
		return KYCDocumentBusinessStakeHolderInfoModel.builder()
				.userToken(additionalValues)
				.clientUserId(SHOP_ID_2001)
				.requiresKYC(additionalValues, VALID_BUSINESS_STAKE_HOLDER_NUMBER)
				.proofOfIdentity(additionalValues, VALID_BUSINESS_STAKE_HOLDER_NUMBER)
				.build();
		//@formatter:on
	}

	private KYCDocumentBusinessStakeHolderInfoModel createAnotherKYCDocumentBusinessStakeholderInfoModelObject() {
		final List<MiraklAdditionalFieldValue> additionalValues = List.of(
				new MiraklBooleanAdditionalFieldValue(REQUIRES_PROOF_IDENTITY, Boolean.TRUE.toString()),
				new MiraklStringAdditionalFieldValue(HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN),
				new MiraklStringAdditionalFieldValue(TOKEN, BUSINESS_STAKEHOLDER_TOKEN),
				new MiraklValueListAdditionalFieldValue(PROOF_OF_IDENTITY_TYPE, "GOVERNMENT_ID"));
		//@formatter:off
		return KYCDocumentBusinessStakeHolderInfoModel.builder()
				.userToken(additionalValues)
				.clientUserId(SHOP_ID_2000)
				.requiresKYC(additionalValues, VALID_BUSINESS_STAKE_HOLDER_NUMBER)
				.proofOfIdentity(additionalValues, VALID_BUSINESS_STAKE_HOLDER_NUMBER)
				.build();
		//@formatter:on
	}

}
