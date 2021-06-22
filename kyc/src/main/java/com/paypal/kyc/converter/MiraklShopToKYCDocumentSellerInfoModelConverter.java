package com.paypal.kyc.converter;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * Converts {@link MiraklShop} object into {@link KYCDocumentSellerInfoModel}
 */
@Service
public class MiraklShopToKYCDocumentSellerInfoModelConverter
		implements Converter<MiraklShop, KYCDocumentSellerInfoModel> {

	@Override
	public KYCDocumentSellerInfoModel convert(@NonNull final MiraklShop source) {
		//@formatter:off
        return KYCDocumentSellerInfoModel.builder()
                .clientUserId(source.getId())
                .professional(source.isProfessional())
                .userToken(source.getAdditionalFieldValues())
                .requiresKYC(source.getAdditionalFieldValues())
                .proofOfAddress(source.getAdditionalFieldValues())
                .proofOfIdentity(source.getAdditionalFieldValues())
                .proofOfBusiness(source.getAdditionalFieldValues())
                .countryIsoCode(source.getAdditionalFieldValues())
				.hyperwalletProgram(source.getAdditionalFieldValues())
                .build();
        //@formatter:on
	}

}
