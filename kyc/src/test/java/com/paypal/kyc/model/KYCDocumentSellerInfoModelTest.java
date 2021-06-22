package com.paypal.kyc.model;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.paypal.kyc.model.KYCConstants.HYPERWALLET_USER_TOKEN_FIELD;
import static com.paypal.kyc.model.KYCConstants.HwDocuments.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KYCDocumentSellerInfoModelTest {

	private static final String HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_STAKEHOLDER_FIELD = "hw-stakeholder-req-proof-identity-1";

	private static final String HYPERWALLET_BUSINESS_STAKEHOLDER_TOKEN_FIELD = "hw-stakeholder-token-1";

	private static final String PROOF_OF_ADDRESS = "hw-ind-proof-address";

	public static final String PROOF_OF_BUSINESS = "hw-prof-proof-business-front";

	private static final String HYPERWALLET_BUSINESS_STAKEHOLDER_PROOF_IDENTITY_TYPE_FIELD = "hw-stakeholder-proof-identity-type-1";

	private static final String SHOP_ID_2000 = "2000";

	private static final String SHOP_ID_2001 = "2001";

	private static final String USER_TOKEN = "userToken";

	private static final String BUSINESS_STAKEHOLDER_TOKEN = "businessStakeholderToken";

	@Test
	void containsIdentityDocuments_shouldReturnTrueWhenAllIdentityDocumentsAreFilled() {

		final KYCDocumentModel nationalIdentityCardFront = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_FRONT).build();
		final KYCDocumentModel nationalIdentityCardBack = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_BACK).build();
		final KYCDocumentSellerInfoModel testObj = KYCDocumentSellerInfoModel.builder()
				.proofOfIdentity(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_FIELD, "GOVERNMENT_ID")))
				.documents(List.of(nationalIdentityCardFront, nationalIdentityCardBack)).build();

		final boolean result = testObj.isIdentityDocumentsFilled();

		Assertions.assertThat(result).isTrue();

	}

	@Test
	void containsIdentityDocuments_shouldReturnFalseWhenIdentityDocumentsAreNotCompletelyFilled() {

		final KYCDocumentModel nationalIdentityCardFront = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_FRONT).build();
		final KYCDocumentSellerInfoModel testObj = KYCDocumentSellerInfoModel.builder()
				.proofOfIdentity(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_FIELD, "GOVERNMENT_ID")))
				.documents(List.of(nationalIdentityCardFront)).build();

		final boolean result = testObj.isIdentityDocumentsFilled();

		Assertions.assertThat(result).isFalse();

	}

	@Test
	void containsAddressDocuments_shouldReturnTrueWhenAllIdentityDocumentsAreFilled() {

		final KYCDocumentModel taxReturn = KYCDocumentModel.builder().documentFieldName(PROOF_OF_ADDRESS).build();
		final KYCDocumentSellerInfoModel testObj = KYCDocumentSellerInfoModel.builder()
				.proofOfAddress(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_ADDRESS_FIELD, "TAX_RETURN")))
				.documents(List.of(taxReturn)).build();

		final boolean result = testObj.isAddressDocumentsFilled();

		Assertions.assertThat(result).isTrue();

	}

	@Test
	void containsAddressDocuments_shouldReturnFalseWhenAddressDocumentsAreNotCompletelyFilled() {

		final KYCDocumentModel nationalIdentityCardFront = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_FRONT).build();
		final KYCDocumentSellerInfoModel testObj = KYCDocumentSellerInfoModel.builder()
				.proofOfIdentity(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_FIELD, "TAX_RETURN")))
				.documents(List.of(nationalIdentityCardFront)).build();

		final boolean result = testObj.isIdentityDocumentsFilled();

		Assertions.assertThat(result).isFalse();

	}

	@Test
	void equals_shouldReturnTrueWhenBothAreEquals() {

		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelOne = createKYCDocumentSellerInfoModelObject();
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelTwo = createKYCDocumentSellerInfoModelObject();

		final boolean result = kycDocumentSellerInfoModelOne.equals(kycDocumentSellerInfoModelTwo);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenBothAreNotEquals() {

		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelOne = createKYCDocumentSellerInfoModelObject();
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelTwo = createAnotherKYCDocumentSellerBusinessStakeholderModelObject();

		final boolean result = kycDocumentSellerInfoModelOne.equals(kycDocumentSellerInfoModelTwo);

		assertThat(result).isFalse();
	}

	@Test
	void equals_shouldReturnTrueWhenSameObjectIsCompared() {

		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelOne = createKYCDocumentSellerInfoModelObject();

		final boolean result = kycDocumentSellerInfoModelOne.equals(kycDocumentSellerInfoModelOne);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenComparedWithAnotherInstanceObject() {

		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelOne = createKYCDocumentSellerInfoModelObject();

		final Object o = new Object();

		final boolean result = kycDocumentSellerInfoModelOne.equals(o);

		assertThat(result).isFalse();
	}

	@Test
	void getIdentityDocuments_shouldReturnDocumentsWithIdentificationTypeAndFrontSide() {
		//@formatter:off
		final KYCDocumentModel documentForPassport = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT)
				.build();
		final KYCDocumentModel unnecesaryDocument = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_BACK)
				.build();
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
				.documents(List.of(documentForPassport, unnecesaryDocument))
				.build();
		//@formatter:on

		final List<KYCDocumentModel> result = kycDocumentSellerInfoModel.getIdentityDocuments();

		assertThat(result).hasSize(1).containsExactlyInAnyOrder(documentForPassport);
	}

	@Test
	void getIdentityDocuments_shouldReturnEmptyListWhenNoDocumentIsFilled() {
		//@formatter:off
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
				.build();
		//@formatter:on
		final List<KYCDocumentModel> result = kycDocumentSellerInfoModel.getIdentityDocuments();

		assertThat(result).isEmpty();
	}

	@Test
	void isIdentityDocumentsFilled_shouldReturnTrueWhenProofOfIdentityAndDocumentsAreFilled() {
		//@formatter:off
		final KYCDocumentModel documentForPassport = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT)
				.build();
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
				.documents(List.of(documentForPassport))
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.isIdentityDocumentsFilled();

		assertThat(result).isTrue();
	}

	@Test
	void isIdentityDocumentsFilled_shouldReturnFalseWhenProofOfIdentityIsEmptyAndDocumentsAreFilled() {
		//@formatter:off
		final KYCDocumentModel documentForPassport = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT)
				.build();
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.documents(List.of(documentForPassport))
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.isIdentityDocumentsFilled();

		assertThat(result).isFalse();
	}

	@Test
	void isIdentityDocumentsFilled_shouldReturnFalseWhenProofOfIdentityIsFilledAndDocumentsAreEmpty() {
		//@formatter:off
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.isIdentityDocumentsFilled();

		assertThat(result).isFalse();
	}

	@Test
	void isAddressDocumentsFilled_shouldReturnTrueWhenProofOfAddressAndDocumentsAreFilled() {
		//@formatter:off
		final KYCDocumentModel documentForBankStatement = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_ADDRESS)
				.build();
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.proofOfAddress(KYCProofOfAddressEnum.BANK_STATEMENT)
				.documents(List.of(documentForBankStatement))
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.isAddressDocumentsFilled();

		assertThat(result).isTrue();
	}

	@Test
	void isAddressDocumentsFilled_shouldReturnFalseWhenProofOfAddressIsEmptyAndDocumentsAreFilled() {
		//@formatter:off
		final KYCDocumentModel documentForBankStatement = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_ADDRESS)
				.build();
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.documents(List.of(documentForBankStatement))
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.isAddressDocumentsFilled();

		assertThat(result).isFalse();
	}

	@Test
	void isAddressDocumentsFilled_shouldReturnFalseWhenProofOfAddressIsFilledAndDocumentsAreEmpty() {
		//@formatter:off
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.proofOfAddress(KYCProofOfAddressEnum.BANK_STATEMENT)
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.isAddressDocumentsFilled();

		assertThat(result).isFalse();
	}

	@Test
	void isProofOfBusinessFilled_shouldReturnTrueWhenProofOfBusinessAndDocumentsAreFilled() {
		//@formatter:off
		final KYCDocumentModel documentForBankStatement = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_BUSINESS)
				.build();
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.proofOfBusiness(KYCProofOfBusinessEnum.INCORPORATION)
				.documents(List.of(documentForBankStatement))
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.isProofOfBusinessFilled();

		assertThat(result).isTrue();
	}

	@Test
	void isProofOfBusinessFilled_shouldReturnFalseWhenProofOfBusinessIsEmptyAndDocumentsAreFilled() {
		//@formatter:off
		final KYCDocumentModel documentForBankStatement = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_BUSINESS)
				.build();
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.documents(List.of(documentForBankStatement))
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.isProofOfBusinessFilled();

		assertThat(result).isFalse();
	}

	@Test
	void isProofOfBusinessFilled_shouldReturnFalseWhenProofOfBusinessIsFilledAndDocumentsAreEmpty() {
		//@formatter:off
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.proofOfBusiness(KYCProofOfBusinessEnum.INCORPORATION)
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.isProofOfBusinessFilled();

		assertThat(result).isFalse();
	}

	@Test
	void hasSelectedDocumentsControlFields_shouldReturnTrueWhenSellerIsIndividualAndProofOfIdentityAndProofOfAddressAreFilled() {
		//@formatter:off
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel
				.builder()
				.professional(Boolean.FALSE)
				.proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
				.proofOfAddress(KYCProofOfAddressEnum.BANK_STATEMENT)
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.hasSelectedDocumentControlFields();

		assertThat(result).isTrue();
	}

	@Test
	void hasSelectedDocumentsControlFields_shouldReturnFalseWhenSellerIsIndividualAndProofOfIdentityIsFilledAndProofOfAddressIsEmpty() {
		//@formatter:off
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel
				.builder()
				.professional(Boolean.FALSE)
				.proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.hasSelectedDocumentControlFields();

		assertThat(result).isFalse();
	}

	@Test
	void hasSelectedDocumentsControlFields_shouldReturnFalseWhenSellerIsIndividualAndProofOfIdentityIsEmptyAndProofOfAddressIsFilled() {
		//@formatter:off
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel
				.builder()
				.professional(Boolean.FALSE)
				.proofOfAddress(KYCProofOfAddressEnum.BANK_STATEMENT)
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.hasSelectedDocumentControlFields();

		assertThat(result).isFalse();
	}

	@Test
	void hasSelectedDocumentsControlFields_shouldReturnTrueWhenSellerIsProfessionalAndProofOfBusinessIsFilled() {
		//@formatter:off
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel
				.builder()
				.professional(Boolean.TRUE)
				.proofOfBusiness(KYCProofOfBusinessEnum.INCORPORATION)
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.hasSelectedDocumentControlFields();

		assertThat(result).isTrue();
	}

	@Test
	void hasSelectedDocumentsControlFields_shouldReturnFalseWhenSellerIsProfessionalAndProofOfBusinessIsNotFilled() {
		//@formatter:off
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel
				.builder()
				.professional(Boolean.TRUE)
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.hasSelectedDocumentControlFields();

		assertThat(result).isFalse();
	}

	@Test
	void areDocumentsFilled_shouldReturnTrueWhenSellerIsProfessionalAndDocumentsAreFilled() {
		//@formatter:off
		final KYCDocumentModel documentForBankStatement = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_BUSINESS)
				.build();
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.professional(Boolean.TRUE)
				.proofOfBusiness(KYCProofOfBusinessEnum.INCORPORATION)
				.documents(List.of(documentForBankStatement))
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.areDocumentsFilled();

		assertThat(result).isTrue();
	}

	@Test
	void areDocumentsFilled_shouldReturnTrueWhenSellerIsIndividualAndDocumentsAreFilled() {
		//@formatter:off
		final KYCDocumentModel documentForBankStatement = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_ADDRESS)
				.build();
		final KYCDocumentModel documentForPassport = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT)
				.build();
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.professional(Boolean.FALSE)
				.proofOfAddress(KYCProofOfAddressEnum.BANK_STATEMENT)
				.proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
				.documents(List.of(documentForBankStatement,documentForPassport))
				.build();
		//@formatter:on

		final boolean result = kycDocumentSellerInfoModel.areDocumentsFilled();

		assertThat(result).isTrue();
	}

	private KYCDocumentSellerInfoModel createAnotherKYCDocumentSellerBusinessStakeholderModelObject() {
		final List<MiraklAdditionalFieldValue> additionalValues = List.of(
				new MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue(
						HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_STAKEHOLDER_FIELD, Boolean.TRUE.toString()),
				new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(HYPERWALLET_USER_TOKEN_FIELD,
						USER_TOKEN),
				new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						HYPERWALLET_BUSINESS_STAKEHOLDER_TOKEN_FIELD, BUSINESS_STAKEHOLDER_TOKEN),
				new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						HYPERWALLET_BUSINESS_STAKEHOLDER_PROOF_IDENTITY_TYPE_FIELD, "GOVERNMENT_ID"));

		//@formatter:off
		return KYCDocumentSellerInfoModel.builder()
				.userToken(additionalValues)
				.clientUserId(SHOP_ID_2001)
				.requiresKYC(additionalValues)
				.requiresKYC(additionalValues)
				.countryIsoCode(additionalValues)
				.proofOfIdentity(additionalValues)
				.proofOfAddress(additionalValues)
				.proofOfBusiness(additionalValues)
				.build();
		//@formatter:on
	}

	private KYCDocumentSellerInfoModel createKYCDocumentSellerInfoModelObject() {

		final List<MiraklAdditionalFieldValue> additionalValues = List.of(
				new MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue(
						HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_STAKEHOLDER_FIELD, Boolean.TRUE.toString()),
				new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(HYPERWALLET_USER_TOKEN_FIELD,
						USER_TOKEN),
				new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						HYPERWALLET_BUSINESS_STAKEHOLDER_TOKEN_FIELD, BUSINESS_STAKEHOLDER_TOKEN),
				new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						HYPERWALLET_BUSINESS_STAKEHOLDER_PROOF_IDENTITY_TYPE_FIELD, "GOVERNMENT_ID"));

		//@formatter:off
		return KYCDocumentSellerInfoModel.builder()
				.userToken(additionalValues)
				.clientUserId(SHOP_ID_2000)
				.requiresKYC(additionalValues)
				.requiresKYC(additionalValues)
				.countryIsoCode(additionalValues)
				.proofOfIdentity(additionalValues)
				.proofOfAddress(additionalValues)
				.proofOfBusiness(additionalValues)
				.build();
		//@formatter:on
	}

}
