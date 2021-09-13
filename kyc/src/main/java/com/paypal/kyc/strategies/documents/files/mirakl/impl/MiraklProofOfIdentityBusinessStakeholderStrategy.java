package com.paypal.kyc.strategies.documents.files.mirakl.impl;

import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCProofOfIdentityEnum;
import com.paypal.kyc.strategies.documents.files.mirakl.AbstractMiraklSelectedDocumentsStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MiraklProofOfIdentityBusinessStakeholderStrategy extends AbstractMiraklSelectedDocumentsStrategy {

	protected MiraklProofOfIdentityBusinessStakeholderStrategy(
			final MiraklMarketplacePlatformOperatorApiClient miraklApiClient) {
		super(miraklApiClient);
	}

	@Override
	protected List<String> getMiraklFieldNames(final KYCDocumentInfoModel source) {
		KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = (KYCDocumentBusinessStakeHolderInfoModel) source;

		return KYCProofOfIdentityEnum.getMiraklFields(source.getProofOfIdentity(),
				kycDocumentBusinessStakeHolderInfoModel.getBusinessStakeholderMiraklNumber());
	}

	/**
	 * Checks whether the strategy must be executed based on the {@code source}
	 * @param source the source object
	 * @return returns whether the strategy is applicable or not
	 */
	@Override
	public boolean isApplicable(final KYCDocumentInfoModel source) {
		if (!(source instanceof KYCDocumentBusinessStakeHolderInfoModel)) {
			return false;
		}

		KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = (KYCDocumentBusinessStakeHolderInfoModel) source;
		return source.isRequiresKYC() && Objects.nonNull(kycDocumentBusinessStakeHolderInfoModel.getProofOfIdentity());
	}

}
