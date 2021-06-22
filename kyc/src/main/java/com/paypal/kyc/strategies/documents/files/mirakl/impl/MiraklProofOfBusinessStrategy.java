package com.paypal.kyc.strategies.documents.files.mirakl.impl;

import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.model.KYCProofOfBusinessEnum;
import com.paypal.kyc.strategies.documents.files.mirakl.AbstractMiraklSelectedDocumentsStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MiraklProofOfBusinessStrategy extends AbstractMiraklSelectedDocumentsStrategy {

	protected MiraklProofOfBusinessStrategy(final MiraklMarketplacePlatformOperatorApiClient miraklApiClient) {
		super(miraklApiClient);
	}

	@Override
	protected List<String> getMiraklFieldNames(final KYCDocumentInfoModel source) {
		return KYCProofOfBusinessEnum.getMiraklFields();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final KYCDocumentInfoModel source) {
		if (!(source instanceof KYCDocumentSellerInfoModel)) {
			return false;
		}

		KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = (KYCDocumentSellerInfoModel) source;
		return Objects.nonNull(kycDocumentSellerInfoModel.getProofOfBusiness());
	}

}
