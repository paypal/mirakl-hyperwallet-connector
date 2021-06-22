package com.paypal.kyc.strategies.documents.files.mirakl.impl;

import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.strategies.documents.files.mirakl.AbstractMiraklSelectedDocumentsStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paypal.kyc.model.KYCConstants.HwDocuments.PROOF_OF_AUTHORIZATION;

@Service
public class MiraklLetterOfAuthorizationBusinessStakeholderStrategy extends AbstractMiraklSelectedDocumentsStrategy {

	protected MiraklLetterOfAuthorizationBusinessStakeholderStrategy(
			final MiraklMarketplacePlatformOperatorApiClient miraklApiClient) {
		super(miraklApiClient);
	}

	@Override
	protected List<String> getMiraklFieldNames(final KYCDocumentInfoModel source) {
		return List.of(PROOF_OF_AUTHORIZATION);
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
		return kycDocumentBusinessStakeHolderInfoModel.isContact()
				&& kycDocumentBusinessStakeHolderInfoModel.isRequiresLetterOfAuthorization();
	}

}
