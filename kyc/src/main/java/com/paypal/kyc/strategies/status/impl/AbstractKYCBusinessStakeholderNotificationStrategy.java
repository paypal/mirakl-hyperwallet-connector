package com.paypal.kyc.strategies.status.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.exceptions.HMCException;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.infrastructure.hyperwallet.api.UserHyperwalletApiConfig;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractKYCBusinessStakeholderNotificationStrategy
		implements Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Void> {

	protected final HyperwalletSDKUserService hyperwalletSDKUserService;

	protected final UserHyperwalletApiConfig kycHyperwalletApiConfig;

	protected AbstractKYCBusinessStakeholderNotificationStrategy(
			final HyperwalletSDKUserService hyperwalletSDKUserService,
			final UserHyperwalletApiConfig kycHyperwalletApiConfig) {
		this.hyperwalletSDKUserService = hyperwalletSDKUserService;
		this.kycHyperwalletApiConfig = kycHyperwalletApiConfig;
	}

	protected HyperwalletUser getHyperWalletUser(
			final KYCBusinessStakeholderStatusNotificationBodyModel kycBusinessStakeholderStatusNotificationBodyModel) {
		final List<HyperwalletUser> hyperWalletUser = kycHyperwalletApiConfig.getTokens().keySet().stream()
				.map(hyperwalletSDKUserService::getHyperwalletInstanceByHyperwalletProgram)
				.map(hyperwallet -> callHyperwalletSDKCatchingException(hyperwallet,
						kycBusinessStakeholderStatusNotificationBodyModel.getUserToken()))
				.filter(Objects::nonNull).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(hyperWalletUser)) {
			throw new HMCException(
					String.format("No Hyperwallet users were found for user token %s in the system instance(s)",
							kycBusinessStakeholderStatusNotificationBodyModel.getUserToken()));
		}
		return hyperWalletUser.get(0);
	}

	protected HyperwalletUser callHyperwalletSDKCatchingException(final Hyperwallet hyperwallet,
			final String userToken) {
		try {
			return hyperwallet.getUser(userToken);
		}
		catch (final RuntimeException e) {
			return null;
		}
	}

}
