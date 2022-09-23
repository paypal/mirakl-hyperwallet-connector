package com.paypal.kyc.strategies.documents.files.mirakl.impl;

import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.model.KYCProofOfAddressEnum;
import com.paypal.kyc.strategies.documents.files.mirakl.AbstractMiraklSelectedDocumentsStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MiraklProofOfAddressStrategy extends AbstractMiraklSelectedDocumentsStrategy {

	protected MiraklProofOfAddressStrategy(final MiraklMarketplacePlatformOperatorApiWrapper miraklApiClient) {
		super(miraklApiClient);
	}

	@Override
	public boolean isApplicable(final KYCDocumentInfoModel source) {
		if (!(source instanceof KYCDocumentSellerInfoModel)) {
			return false;
		}
		KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = (KYCDocumentSellerInfoModel) source;
		return Objects.nonNull(kycDocumentSellerInfoModel.getProofOfAddress());
	}

	@Override
	protected List<String> getMiraklFieldNames(final KYCDocumentInfoModel source) {
		return KYCProofOfAddressEnum.getMiraklFields();
	}

}
