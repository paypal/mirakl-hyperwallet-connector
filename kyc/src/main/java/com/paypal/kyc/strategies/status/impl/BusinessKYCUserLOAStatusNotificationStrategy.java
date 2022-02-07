package com.paypal.kyc.strategies.status.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.model.HyperwalletUser.LetterOfAuthorizationStatus;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue;
import com.paypal.kyc.infrastructure.configuration.KYCHyperwalletApiConfig;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import com.paypal.kyc.model.KYCConstants;
import com.paypal.kyc.service.HyperwalletSDKService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BusinessKYCUserLOAStatusNotificationStrategy extends AbstractKYCBusinessStakeholderNotificationStrategy {

	protected final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient;

	public BusinessKYCUserLOAStatusNotificationStrategy(final HyperwalletSDKService hyperwalletSDKService,
			final KYCHyperwalletApiConfig kycHyperwalletApiConfig,
			final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient) {
		super(hyperwalletSDKService, kycHyperwalletApiConfig);
		this.miraklMarketplacePlatformOperatorApiClient = miraklMarketplacePlatformOperatorApiClient;
	}

	@Override
	public Void execute(
			final KYCBusinessStakeholderStatusNotificationBodyModel kycBusinessStakeholderStatusNotificationBodyModel) {
		Optional.ofNullable(getHyperWalletUser(kycBusinessStakeholderStatusNotificationBodyModel))
				.ifPresent(hyperWalletUser -> updateMiraklLOAStatus(hyperWalletUser.getClientUserId(),
						hyperWalletUser.getLetterOfAuthorizationStatus()));
		return null;
	}

	@Override
	public boolean isApplicable(
			final KYCBusinessStakeholderStatusNotificationBodyModel kycBusinessStakeholderStatusNotificationBodyModel) {
		return kycBusinessStakeholderStatusNotificationBodyModel.getIsBusinessContact()
				&& kycBusinessStakeholderStatusNotificationBodyModel.getIsDirector()
				&& KYCConstants.HwWebhookNotificationType.USERS_BUSINESS_STAKEHOLDERS_CREATED.equals(
						kycBusinessStakeholderStatusNotificationBodyModel.getHyperwalletWebhookNotificationType());
	}

	protected void updateMiraklLOAStatus(final String miraklShopId,
			final HyperwalletUser.LetterOfAuthorizationStatus letterOfAuthorizationStatus) {

		final MiraklUpdateShop updateShop = new MiraklUpdateShop();
		final String isLetterOfAuthorizationRequired = Boolean
				.toString(isLetterOfAuthorizationRequired(letterOfAuthorizationStatus));

		final MiraklSimpleRequestAdditionalFieldValue miraklSimpleRequestAdditionalFieldValue = new MiraklSimpleRequestAdditionalFieldValue(
				KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD,
				isLetterOfAuthorizationRequired);

		updateShop.setShopId(Long.valueOf(miraklShopId));
		updateShop.setAdditionalFieldValues(List.of(miraklSimpleRequestAdditionalFieldValue));

		log.info("Updating KYC Letter of authorization flag in Mirakl for business Stakeholder for shopId [{}]",
				miraklShopId);
		final MiraklUpdateShopsRequest miraklUpdateShopsRequest = new MiraklUpdateShopsRequest(List.of(updateShop));
		miraklMarketplacePlatformOperatorApiClient.updateShops(miraklUpdateShopsRequest);
		log.info("Letter of authorization flag updated to '{}', for business Stakeholder in shopId [{}]",
				isLetterOfAuthorizationRequired, miraklShopId);
	}

	protected boolean isLetterOfAuthorizationRequired(final LetterOfAuthorizationStatus letterOfAuthorizationStatus) {
		return LetterOfAuthorizationStatus.REQUIRED.equals(letterOfAuthorizationStatus)
				|| LetterOfAuthorizationStatus.FAILED.equals(letterOfAuthorizationStatus);
	}

}
