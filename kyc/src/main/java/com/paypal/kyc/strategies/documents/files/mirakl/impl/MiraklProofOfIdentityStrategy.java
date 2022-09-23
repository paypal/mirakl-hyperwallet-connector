package com.paypal.kyc.strategies.documents.files.mirakl.impl;

import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
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

	protected MiraklProofOfIdentityStrategy(final MiraklMarketplacePlatformOperatorApiWrapper miraklApiClient) {
		super(miraklApiClient);
	}

	@Override
	protected List<String> getMiraklFieldNames(final KYCDocumentInfoModel source) {
		return KYCProofOfIdentityEnum.getMiraklFields(source.getProofOfIdentity());
	}

	/**
	 * Executes the strategy if the {@code source} is of type
	 * {@link KYCDocumentSellerInfoModel} and it's not a professional
	 * @param source the source object
	 * @return
	 */
	@Override
	public boolean isApplicable(final KYCDocumentInfoModel source) {
		if (!(source instanceof KYCDocumentSellerInfoModel)) {
			return false;
		}

		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = (KYCDocumentSellerInfoModel) source;
		return Objects.nonNull(kycDocumentSellerInfoModel.getProofOfIdentity())
				&& !kycDocumentSellerInfoModel.isProfessional();
	}

}
