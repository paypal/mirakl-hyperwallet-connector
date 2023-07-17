package com.paypal.kyc.sellersdocumentextraction.services.documentselectors;

import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.kyc.documentextractioncommons.support.AbstractMiraklDocumentsSelectorStrategy;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.sellersdocumentextraction.model.KYCProofOfBusinessEnum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MiraklProofOfBusinessSelectorStrategy extends AbstractMiraklDocumentsSelectorStrategy {

	protected MiraklProofOfBusinessSelectorStrategy(final MiraklClient miraklApiClient) {
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

		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = (KYCDocumentSellerInfoModel) source;
		return Objects.nonNull(kycDocumentSellerInfoModel.getProofOfBusiness());
	}

}
