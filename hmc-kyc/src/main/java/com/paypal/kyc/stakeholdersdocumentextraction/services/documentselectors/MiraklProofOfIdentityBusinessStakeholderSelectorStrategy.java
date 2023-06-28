package com.paypal.kyc.stakeholdersdocumentextraction.services.documentselectors;

import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.kyc.documentextractioncommons.support.AbstractMiraklDocumentsSelectorStrategy;
import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCProofOfIdentityEnum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MiraklProofOfIdentityBusinessStakeholderSelectorStrategy extends AbstractMiraklDocumentsSelectorStrategy {

	protected MiraklProofOfIdentityBusinessStakeholderSelectorStrategy(final MiraklClient miraklApiClient) {
		super(miraklApiClient);
	}

	@Override
	protected List<String> getMiraklFieldNames(final KYCDocumentInfoModel source) {
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = (KYCDocumentBusinessStakeHolderInfoModel) source;

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

		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = (KYCDocumentBusinessStakeHolderInfoModel) source;
		return source.isRequiresKYC() && Objects.nonNull(kycDocumentBusinessStakeHolderInfoModel.getProofOfIdentity());
	}

}
