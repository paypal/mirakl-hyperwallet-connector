package com.paypal.kyc.converter;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.model.KYCProofOfAddressEnum;
import com.paypal.kyc.model.KYCProofOfIdentityEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.paypal.kyc.model.KYCConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MiraklShopToKYCDocumentSellerInfoModelConverterTest {

	private static final String USER_TOKEN = "usr-token-23324156485";

	private static final String SHOP_ID = "2000";

	private static final String EUROPE_HYPERWALLET_PROGRAM = "EUROPE";

	@InjectMocks
	private MiraklShopToKYCDocumentSellerInfoModelConverter testObj;

	@Test
	void convert_shouldConvertFromShopToKyCDocumentInfoModel() {
		final List<MiraklAdditionalFieldValue> additionalValues = List.of(
				new MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue(
						HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD, Boolean.TRUE.toString()),
				new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(HYPERWALLET_USER_TOKEN_FIELD,
						USER_TOKEN),
				new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_FIELD, "GOVERNMENT_ID"),
				new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						HYPERWALLET_KYC_IND_PROOF_OF_ADDRESS_FIELD, "BANK_STATEMENT"),
				new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(HW_PROGRAM, "EUROPE"));
		final MiraklShop miraklShopStub = new MiraklShop();
		miraklShopStub.setProfessional(true);
		miraklShopStub.setId(SHOP_ID);
		miraklShopStub.setAdditionalFieldValues(additionalValues);

		final KYCDocumentSellerInfoModel result = testObj.convert(miraklShopStub);

		assertThat(result.getUserToken()).isEqualTo(USER_TOKEN);
		assertThat(result.getClientUserId()).isEqualTo(SHOP_ID);
		assertThat(result.getProofOfAddress()).isEqualTo(KYCProofOfAddressEnum.BANK_STATEMENT);
		assertThat(result.getProofOfIdentity()).isEqualTo(KYCProofOfIdentityEnum.GOVERNMENT_ID);
		assertThat(result.isRequiresKYC()).isTrue();
		assertThat(result.isProfessional()).isTrue();
		assertThat(result.getHyperwalletProgram()).isEqualTo(EUROPE_HYPERWALLET_PROGRAM);
	}

}
