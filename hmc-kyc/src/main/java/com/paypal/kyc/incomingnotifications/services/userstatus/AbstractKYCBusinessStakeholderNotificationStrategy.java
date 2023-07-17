package com.paypal.kyc.incomingnotifications.services.userstatus;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import com.paypal.infrastructure.support.exceptions.HMCException;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.kyc.incomingnotifications.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractKYCBusinessStakeholderNotificationStrategy
		implements Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Void> {

	protected final UserHyperwalletSDKService userHyperwalletSDKService;

	protected final HyperwalletProgramsConfiguration hyperwalletProgramsConfiguration;

	protected AbstractKYCBusinessStakeholderNotificationStrategy(
			final UserHyperwalletSDKService userHyperwalletSDKService,
			final HyperwalletProgramsConfiguration hyperwalletProgramsConfiguration) {
		this.userHyperwalletSDKService = userHyperwalletSDKService;
		this.hyperwalletProgramsConfiguration = hyperwalletProgramsConfiguration;
	}

	protected HyperwalletUser getHyperWalletUser(
			final KYCBusinessStakeholderStatusNotificationBodyModel kycBusinessStakeholderStatusNotificationBodyModel) {
		//@formatter:off
		final List<HyperwalletUser> hyperWalletUser = hyperwalletProgramsConfiguration.getAllProgramConfigurations().stream()
				.map(HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration::getUsersProgramToken)
				.map(userHyperwalletSDKService::getHyperwalletInstanceByProgramToken)
				.map(hyperwallet -> callHyperwalletSDKCatchingException(hyperwallet,
						kycBusinessStakeholderStatusNotificationBodyModel.getUserToken()))
				.filter(Objects::nonNull).collect(Collectors.toList());
		//@formatter:on
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
