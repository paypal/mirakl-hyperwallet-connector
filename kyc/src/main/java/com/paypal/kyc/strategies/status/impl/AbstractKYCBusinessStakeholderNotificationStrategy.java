package com.paypal.kyc.strategies.status.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.infrastructure.configuration.KYCHyperwalletApiConfig;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import com.paypal.kyc.service.HyperwalletSDKService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractKYCBusinessStakeholderNotificationStrategy
		implements Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Optional<Void>> {

	protected final HyperwalletSDKService hyperwalletSDKService;

	protected final KYCHyperwalletApiConfig kycHyperwalletApiConfig;

	protected AbstractKYCBusinessStakeholderNotificationStrategy(final HyperwalletSDKService hyperwalletSDKService,
			final KYCHyperwalletApiConfig kycHyperwalletApiConfig) {
		this.hyperwalletSDKService = hyperwalletSDKService;
		this.kycHyperwalletApiConfig = kycHyperwalletApiConfig;
	}

	protected HyperwalletUser getHyperWalletUser(
			final KYCBusinessStakeholderStatusNotificationBodyModel kycBusinessStakeholderStatusNotificationBodyModel) {
		final List<HyperwalletUser> hyperWalletUser = kycHyperwalletApiConfig.getUserStoreTokens().entrySet().stream()
				.map(Map.Entry::getKey).map(hyperwalletSDKService::getHyperwalletInstance)
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
