package com.paypal.kyc.sellersdocumentextraction.services.documentselectors;

import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.kyc.documentextractioncommons.support.AbstractMiraklDocumentsSelectorStrategy;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.sellersdocumentextraction.model.KYCProofOfAddressEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MiraklProofOfAddressSelectorStrategy extends AbstractMiraklDocumentsSelectorStrategy {

	protected MiraklProofOfAddressSelectorStrategy(final MiraklClient miraklApiClient) {
		super(miraklApiClient);
	}

	@Override
	public boolean isApplicable(final KYCDocumentInfoModel source) {
		if (!(source instanceof KYCDocumentSellerInfoModel)) {
			return false;
		}
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = (KYCDocumentSellerInfoModel) source;
		return Objects.nonNull(kycDocumentSellerInfoModel.getProofOfAddress());
	}

	@Override
	protected List<String> getMiraklFieldNames(final KYCDocumentInfoModel source) {
		return KYCProofOfAddressEnum.getMiraklFields();
	}

}
