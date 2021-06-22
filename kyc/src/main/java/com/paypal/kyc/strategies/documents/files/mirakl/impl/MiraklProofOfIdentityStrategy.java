package com.paypal.kyc.strategies.documents.files.mirakl.impl;

import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.model.KYCProofOfIdentityEnum;
import com.paypal.kyc.strategies.documents.files.mirakl.AbstractMiraklSelectedDocumentsStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MiraklProofOfIdentityStrategy extends AbstractMiraklSelectedDocumentsStrategy {

	protected MiraklProofOfIdentityStrategy(final MiraklMarketplacePlatformOperatorApiClient miraklApiClient) {
		super(miraklApiClient);
	}

	@Override
	protected List<String> getMiraklFieldNames(final KYCDocumentInfoModel source) {
		return KYCProofOfIdentityEnum.getMiraklFields(source.getProofOfIdentity());
	}

	@Override
	public boolean isApplicable(final KYCDocumentInfoModel source) {
		if (!(source instanceof KYCDocumentSellerInfoModel)) {
			return false;
		}

		KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = (KYCDocumentSellerInfoModel) source;
		return Objects.nonNull(kycDocumentSellerInfoModel.getProofOfIdentity());
	}

}
