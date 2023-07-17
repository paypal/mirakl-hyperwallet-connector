package com.paypal.kyc.stakeholdersdocumentextraction.services.converters;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCProofOfIdentityEnum;
import com.paypal.kyc.stakeholdersdocumentextraction.services.converters.MiraklShopToKYCDocumentBusinessStakeholderInfoModelConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.paypal.kyc.documentextractioncommons.model.KYCConstants.HYPERWALLET_USER_TOKEN_FIELD;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MiraklShopToKYCDocumentBusinessStakeholderInfoModelConverterTest {

	private static final String SHOP_ID = "2000";

	private static final String USER_TOKEN = "userToken";

	private static final String BUSINESS_STAKEHOLDER_TOKEN = "businessStakeholderToken";

	private static final String COUNTRY_ISO_CODE = "US";

	private static final String HW_STAKEHOLDER_BUSINESS_CONTACT_1 = "hw-stakeholder-business-contact-1";

	private static final String HW_KYC_REQ_PROOF_AUTHORIZATION = "hw-kyc-req-proof-authorization";

	private static final String GOVERNMENT_ID_DOC = "GOVERNMENT_ID";

	private static final int BUSINESS_STAKEHOLDER_NUMBER = 1;

	private static final String HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_STAKEHOLDER_FIELD = "hw-stakeholder-req-proof-identity-1";

	private static final String HYPERWALLET_BUSINESS_STAKEHOLDER_TOKEN_FIELD = "hw-stakeholder-token-1";

	private static final String HYPERWALLET_BUSINESS_STAKEHOLDER_PROOF_OF_IDENTITY_COUNTRY_ISOCODE_FIELD = "hw-stakeholder-proof-identity-ctry-1";

	private static final String HYPERWALLET_BUSINESS_STAKEHOLDER_PROOF_IDENTITY_TYPE_FIELD = "hw-stakeholder-proof-identity-type-1";

	@InjectMocks
	private MiraklShopToKYCDocumentBusinessStakeholderInfoModelConverter testObj;

	@Test
	void convert_shouldConvertFromMiraklShopToKYCDocumentBusinessStakeHolderInfoModel() {
		final MiraklShop miraklShopStub = createMiraklShop();
		final KYCDocumentBusinessStakeHolderInfoModel result = testObj.convert(miraklShopStub,
				BUSINESS_STAKEHOLDER_NUMBER);

		assertThat(result.getClientUserId()).isEqualTo(SHOP_ID);
		assertThat(result.getUserToken()).isEqualTo(USER_TOKEN);
		assertThat(result.getCountryIsoCode()).isEqualTo(COUNTRY_ISO_CODE);
		assertThat(result.getToken()).isEqualTo(BUSINESS_STAKEHOLDER_TOKEN);
		assertThat(result.isRequiresKYC()).isTrue();
		assertThat(result.getBusinessStakeholderMiraklNumber()).isEqualTo(BUSINESS_STAKEHOLDER_NUMBER);
		assertThat(result.getProofOfIdentity()).isEqualTo(KYCProofOfIdentityEnum.GOVERNMENT_ID);
		assertThat(result.isContact()).isTrue();
		assertThat(result.isRequiresLetterOfAuthorization()).isTrue();
	}

	private MiraklShop createMiraklShop() {
		final List<MiraklAdditionalFieldValue> additionalValues = List.of(
				new MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue(
						HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_STAKEHOLDER_FIELD, Boolean.TRUE.toString()),
				new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(HYPERWALLET_USER_TOKEN_FIELD,
						USER_TOKEN),
				new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						HYPERWALLET_BUSINESS_STAKEHOLDER_PROOF_OF_IDENTITY_COUNTRY_ISOCODE_FIELD, COUNTRY_ISO_CODE),
				new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						HYPERWALLET_BUSINESS_STAKEHOLDER_TOKEN_FIELD, BUSINESS_STAKEHOLDER_TOKEN),
				new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						HYPERWALLET_BUSINESS_STAKEHOLDER_PROOF_IDENTITY_TYPE_FIELD, GOVERNMENT_ID_DOC),
				new MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue(HW_STAKEHOLDER_BUSINESS_CONTACT_1,
						Boolean.TRUE.toString()),
				new MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue(HW_KYC_REQ_PROOF_AUTHORIZATION,
						Boolean.TRUE.toString()));

		final MiraklShop miraklShop = new MiraklShop();
		miraklShop.setId(SHOP_ID);
		miraklShop.setAdditionalFieldValues(additionalValues);

		return miraklShop;
	}

}
