package com.paypal.kyc.stakeholdersdocumentextraction.services.converters;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link KYCBusinessStakeHolderConverter} interface
 */
@Service
public class MiraklShopToKYCDocumentBusinessStakeholderInfoModelConverter implements KYCBusinessStakeHolderConverter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KYCDocumentBusinessStakeHolderInfoModel convert(final MiraklShop source,
			final int businessStakeholderNumber) {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocStk = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.clientUserId(source.getId())
				.businessStakeholderMiraklNumber(businessStakeholderNumber)
				.countryIsoCode(source.getAdditionalFieldValues(), businessStakeholderNumber)
				.userToken(source.getAdditionalFieldValues())
				.token(source.getAdditionalFieldValues(), businessStakeholderNumber)
				.requiresKYC(source.getAdditionalFieldValues(), businessStakeholderNumber)
				.proofOfIdentity(source.getAdditionalFieldValues(), businessStakeholderNumber)
				.contact(source.getAdditionalFieldValues(), businessStakeholderNumber)
				.hyperwalletProgram(source.getAdditionalFieldValues())
				.build();
		//@formatter:on

		if (kycDocStk.isContact()) {
			return kycDocStk.toBuilder().requiresLetterOfAuthorization(source.getAdditionalFieldValues()).build();
		}

		return kycDocStk;
	}

}
