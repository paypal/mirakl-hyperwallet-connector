package com.paypal.kyc.stakeholdersdocumentextraction.services.documentselectors;

import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.kyc.documentextractioncommons.support.AbstractMiraklDocumentsSelectorStrategy;
import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paypal.kyc.documentextractioncommons.model.KYCConstants.HwDocuments.PROOF_OF_AUTHORIZATION;

@Service
public class MiraklLetterOfAuthorizationBusinessStakeholderSelectorStrategy
		extends AbstractMiraklDocumentsSelectorStrategy {

	protected MiraklLetterOfAuthorizationBusinessStakeholderSelectorStrategy(final MiraklClient miraklApiClient) {
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

		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = (KYCDocumentBusinessStakeHolderInfoModel) source;
		return kycDocumentBusinessStakeHolderInfoModel.isContact()
				&& kycDocumentBusinessStakeHolderInfoModel.isRequiresLetterOfAuthorization();
	}

}
