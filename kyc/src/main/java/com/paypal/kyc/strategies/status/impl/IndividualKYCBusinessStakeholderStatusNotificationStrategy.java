package com.paypal.kyc.strategies.status.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mchange.v2.lang.StringUtils;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.kyc.infrastructure.configuration.KYCHyperwalletApiConfig;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import com.paypal.kyc.model.KYCConstants;
import com.paypal.kyc.service.HyperwalletSDKService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IndividualKYCBusinessStakeholderStatusNotificationStrategy
		extends AbstractKYCBusinessStakeholderNotificationStrategy {

	protected final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService;

	protected final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient;

	public IndividualKYCBusinessStakeholderStatusNotificationStrategy(final HyperwalletSDKService hyperwalletSDKService,
			final KYCHyperwalletApiConfig kycHyperwalletApiConfig,
			final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService,
			final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient) {
		super(hyperwalletSDKService, kycHyperwalletApiConfig);
		this.miraklBusinessStakeholderDocumentsExtractService = miraklBusinessStakeholderDocumentsExtractService;
		this.miraklMarketplacePlatformOperatorApiClient = miraklMarketplacePlatformOperatorApiClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void execute(
			final KYCBusinessStakeholderStatusNotificationBodyModel kycBusinessStakeholderStatusNotificationBodyModel) {
		final HyperwalletUser hyperWalletUser = getHyperWalletUser(kycBusinessStakeholderStatusNotificationBodyModel);
		if (Objects.nonNull(hyperWalletUser)) {
			final List<String> miraklProofOfIdentityCustomFieldNames = miraklBusinessStakeholderDocumentsExtractService
					.getKYCCustomValuesRequiredVerificationBusinessStakeholders(hyperWalletUser.getClientUserId(),
							List.of(kycBusinessStakeholderStatusNotificationBodyModel.getToken()));
			final HyperwalletUser.VerificationStatus verificationStatus = kycBusinessStakeholderStatusNotificationBodyModel
					.getVerificationStatus();

			if (CollectionUtils.isNotEmpty(miraklProofOfIdentityCustomFieldNames)) {
				updateMiraklProofIdentityFlagStatus(hyperWalletUser.getClientUserId(),
						miraklProofOfIdentityCustomFieldNames.get(0), verificationStatus);
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final KYCBusinessStakeholderStatusNotificationBodyModel source) {
		return HyperwalletUser.ProfileType.INDIVIDUAL.equals(source.getProfileType())
				&& source.getHyperwalletWebhookNotificationType().contains(
						KYCConstants.HwWebhookNotificationType.USERS_BUSINESS_STAKEHOLDERS_VERIFICATION_STATUS);
	}

	protected void updateMiraklProofIdentityFlagStatus(final String miraklShopId,
			final String kycCustomValuesRequiredVerificationBusinessStakeholder,
			final HyperwalletUser.VerificationStatus verificationStatus) {
		if (StringUtils.nonEmptyString(kycCustomValuesRequiredVerificationBusinessStakeholder)) {
			final MiraklUpdateShop updateShop = new MiraklUpdateShop();

			final List<MiraklRequestAdditionalFieldValue> additionalFieldValues = Optional
					.of(kycCustomValuesRequiredVerificationBusinessStakeholder).stream()
					.map(kycCustomValueRequiredVerification -> new MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue(
							kycCustomValueRequiredVerification,
							Boolean.toString(HyperwalletUser.VerificationStatus.REQUIRED.equals(verificationStatus))))
					.collect(Collectors.toList());

			updateShop.setShopId(Long.valueOf(miraklShopId));
			updateShop.setAdditionalFieldValues(additionalFieldValues);

			log.info("Updating KYC proof of identity flag in Mirakl for business Stakeholder for shopId [{}]",
					miraklShopId);
			final MiraklUpdateShopsRequest miraklUpdateShopsRequest = new MiraklUpdateShopsRequest(List.of(updateShop));
			miraklMarketplacePlatformOperatorApiClient.updateShops(miraklUpdateShopsRequest);
			log.info("Proof of identity flag updated for business Stakeholder for shopId [{}]", miraklShopId);
		}
	}

}
