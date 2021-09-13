package com.paypal.kyc.strategies.status.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mchange.v2.lang.StringUtils;
import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.infrastructure.configuration.KYCHyperwalletApiConfig;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import com.paypal.kyc.service.HyperwalletSDKService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IndividualKYCBusinessStakeholderNotificationStrategy
		implements Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Optional<Void>> {

	protected final HyperwalletSDKService hyperwalletSDKService;

	protected final KYCHyperwalletApiConfig kycHyperwalletApiConfig;

	protected final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService;

	protected final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient;

	public IndividualKYCBusinessStakeholderNotificationStrategy(final HyperwalletSDKService hyperwalletSDKService,
			final KYCHyperwalletApiConfig kycHyperwalletApiConfig,
			final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService,
			final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient) {
		this.hyperwalletSDKService = hyperwalletSDKService;
		this.kycHyperwalletApiConfig = kycHyperwalletApiConfig;
		this.miraklBusinessStakeholderDocumentsExtractService = miraklBusinessStakeholderDocumentsExtractService;
		this.miraklMarketplacePlatformOperatorApiClient = miraklMarketplacePlatformOperatorApiClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Void> execute(
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
		return Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final KYCBusinessStakeholderStatusNotificationBodyModel source) {
		return (HyperwalletUser.ProfileType.INDIVIDUAL.equals(source.getProfileType()));
	}

	protected Optional<Void> updateMiraklProofIdentityFlagStatus(final String miraklShopId,
			final String kycCustomValuesRequiredVerificationBussinessStakeholder,
			final HyperwalletUser.VerificationStatus verificationStatus) {
		if (StringUtils.nonEmptyString(kycCustomValuesRequiredVerificationBussinessStakeholder)) {
			final MiraklUpdateShop updateShop = new MiraklUpdateShop();

			final List<MiraklRequestAdditionalFieldValue> additionalFieldValues = Optional
					.ofNullable(kycCustomValuesRequiredVerificationBussinessStakeholder).stream()
					.map(kycCustomValueRequiredVerification -> new MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue(
							kycCustomValueRequiredVerification,
							HyperwalletUser.VerificationStatus.REQUIRED.equals(verificationStatus)
									? Boolean.TRUE.toString() : Boolean.FALSE.toString()))
					.collect(Collectors.toList());

			updateShop.setShopId(Long.valueOf(miraklShopId));
			updateShop.setAdditionalFieldValues(additionalFieldValues);

			try {
				log.debug("Updating KYC proof of identity flag in Mirakl for business Stakeholder for shopId [{}]",
						miraklShopId);
				final MiraklUpdateShopsRequest miraklUpdateShopsRequest = new MiraklUpdateShopsRequest(
						List.of(updateShop));
				miraklMarketplacePlatformOperatorApiClient.updateShops(miraklUpdateShopsRequest);
				log.info("Proof of identity flag updated for business Stakeholder for shopId [{}]", miraklShopId);
			}
			catch (final MiraklException ex) {
				log.error(
						"Something went wrong updating KYC business stakeholder information of shop [{}]. Details [{}]",
						miraklShopId, ex.getMessage());
			}
		}
		return Optional.empty();
	}

	protected HyperwalletUser getHyperWalletUser(
			final KYCBusinessStakeholderStatusNotificationBodyModel kycBusinessStakeholderStatusNotificationBodyModel) {
		final List<HyperwalletUser> hyperWalletUser = kycHyperwalletApiConfig.getUserStoreTokens().entrySet().stream()
				.map(Map.Entry::getKey).map(programToken -> hyperwalletSDKService.getHyperwalletInstance(programToken))
				.map(hyperwallet -> callHyperwalletSDKCatchingException(hyperwallet,
						kycBusinessStakeholderStatusNotificationBodyModel.getUserToken()))
				.filter(Objects::nonNull).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(hyperWalletUser)) {
			return null;
		}
		return hyperWalletUser.get(0);
	}

	protected HyperwalletUser callHyperwalletSDKCatchingException(final Hyperwallet hyperwallet,
			final String userToken) {
		try {
			return hyperwallet.getUser(userToken);
		}
		catch (RuntimeException e) {
			return null;
		}
	}

}
